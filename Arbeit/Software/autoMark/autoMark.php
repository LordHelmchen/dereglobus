<?php

/* Prototyp des WIZARD-Skriptes
 *	
 * Grobbeschreibung der Anforderungen (Auszug aus dem Forum):
 * 		- Das Skript würde praktisch in regelmäßigen Abständen (vom Server gesteuert 
 * 			z.B. durch "Cron") ausgeführt werden,
 * 		- im Wiki nachschauen was sich geändert hat (RSS-Feed der letzten Änderungen),
 * 		- diese Änderungen, falls relevant, in die entsprechenden Dateien 
 * 			(z.B. Siedlungen.kml) aufnehmen und
 * 		- sobald der Nutzer das nächste mal DG öffnet (bzw. ein Refresh des 
 * 			Netzwerklinks auf die Datei ausgelöst wird) wären die Daten verfügbar..
 * 	
 * 		- einen Menüpunkt "Deine Ortsmarken umwandeln" (oder so) ins "Wizards"-Menü 
 * 			zu integrieren. Der Beschreibungstext besteht aus einem HTML-Formular 
 * 			in das der Nutzer einfach den KML-Code (per rechte Maustasten seinen 
 * 			Ordner kopieren) einfügt und mit "Senden" an das Skript sendet. Dieses 
 * 			wandelt die Datei um und integriert die Ortsmarken in die Release-Kml 
 * 			oder gibt sie als eigene KML-Datei aus, die (unter einem weiterer 
 * 			Menüpunkt "Umgewandelte Ortsmarken") direkt wieder in DG verfügbar ist 
 * 			(zum anschauen und nachprüfen)....
**/

// REQUIRE
// Stellt die Funktion extractFromWiki($url) bereit
require('Wiki_interface.php');

// Zugriff auf Pfad- und URL-Konstanten
require('Config.php');

// Logging-Komponente: logMe($logtext)
require('LogMe.php');

function genFromCode($fileName, $userCode){
	// Time Out auf 300 ansonsten bricht das Skript nach 60 Sekunden ab
	// ACHTUNG im Falle von safe_mode = on in php.ini geht dies nicht dann womöglich über ini_set('max_execution_time','600');
	set_time_limit(300);
	
	// Daten mit Hilfe von SimpleXML einlesen
	$kmlFromCode = getKMLFromCode($userCode);
	
	// Objektklasseninformation extrahieren und Style zuweisen
	recursiveGenDetails($kmlFromCode->Document);
	
	// Ausgabe der KML-Datei
	$kml = array('<?xml version="1.0" encoding="UTF-8"?>');
	$kml[] = '<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:kml="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom">';
	
	$elemToGenerate = array(
						'Document', 
						'Folder',
						'name',
						'open',
						'Placemark',
						'description',
						'LookAt',
						'longitude',
						'latitude',
						'altitude',
						'range',
						'tilt',
						'heading',
						'altitudeMode',
						'styleUrl',
						'Point',
						'coordinates'
						);
	$kml[] = recursiveGenKML($kmlFromCode->Document, $elemToGenerate);
		
	$kml[] = '</kml>';
	$kmlOutput = join("\n", $kml);
	
	//header('Content-type: application/vnd.google-earth.kml+xml');
	
	//Datei speichern
	//ACHTUNG: Wenn die Datei schon existiert wird sie übereschrieben
	if (!$userKmlFile = fopen(HOMEDIR_PATH . $fileName . '.kml', w)) {
		logMe("Kann die Datei $fileName nicht öffnen");
		exit;
	}
		
	// Schreibe $somecontent in die geöffnete Datei.
	if (!fwrite($userKmlFile, $kmlOutput)) {
		logMe("Kann in die Datei $fileName nicht schreiben");
		exit;
	}
	fclose($userKmlFile);
	
	//Link zur erstellten Datei in Umgewandelte Dateien einpflegen
	
	$kmlFromFile = getKMLFromFile(HOMEDIR_PATH . 'UmgewandelteDateien.kml');
	
	$kml = array('<?xml version="1.0" encoding="UTF-8"?>');
	$kml[] = '<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:kml="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom">';
	$kml[] = '<Document>';
	
	$kml[] = '<NetworkLink>';
	$kml[] = '<name>' . $fileName . '</name>';
	$kml[] = '<Link>';
	$kml[] = '<href>' . HOMEDIR_PATH . $fileName . '.kml' . '</href>';
	$kml[] = '</Link>';
	$kml[] = '</NetworkLink>';
	
	$elemToGenerate = array( 
						'NetworkLink',
						'name',
						'Link',
						'href');
	
	$kml[] = recursiveGenKML($kmlFromFile->Document, $elemToGenerate);
	
	$kml[] = '</Document>';
	$kml[] = '</kml>';
	
	$kmlOutput = join("\n", $kml);
	
	//Datei speichern
	//ACHTUNG: Wenn die Datei schon existiert wird sie übereschrieben
	if (!$userKmlFile = fopen(HOMEDIR_PATH . 'UmgewandelteDateien.kml', w)) {
		logMe("Kann die Datei UmgewandelteDateien.kml nicht öffnen");
		exit;
	}
	
	// Schreibe $somecontent in die geöffnete Datei.
	if (!fwrite($userKmlFile, $kmlOutput)) {
		logMe("Kann in die Datei UmgewandelteDateien.kml nicht schreiben");
		exit;
	}
	fclose($userKmlFile);
	echo 'Das Fenster kann jetzt geschlossen werden!';
	
}
/*
// FeedReader zum Abgelich ob es Änderungen gegeben hat
$feed = simplexml_load_file(FEED_URL_RECENT_CHANGES, 'SimpleXMLElement', LIBXML_NOCDATA);
if (!$feed){
	// Auswerten des Feeds: Abgleich der Änderungen mit den Elementen der entsprechden KML files
	echo $feed->title;
}else{
	exit ('Konnte ' . FEED_URL_RECENT_CHANGES . ' nicht lesen.'); 
}
**/

// Daten einer Datei als SimpleXMLElement mit SimpleXML einlesen
function getKMLFromFile($path){
	if (file_exists($path)) {
		return simplexml_load_file($path, 'SimpleXMLElement', LIBXML_NOCDATA);
	} else {
		//FEHLERBEHANDLUNG: Pfad-Fehler!!
		logMe('Konnte ' . $path . ' nicht öffnen.');
		exit;
	}
}



// Daten eines Strings als SimpleXMLElement mit SimpleXML einlesen
function getKMLFromCode($code){
	$code = stripslashes($code);
	
	$kml = simplexml_load_string($code);
	if(!$kml === false){
		return $kml;
	}else{
		logMe('Dein Code konnte nicht in ein SimpleXMLElement umwandeln.');
		exit('Dein Code konnte nicht in ein SimpleXMLElement umwandeln.');
	}
}

function recursiveGenDetails($recFolder){
	foreach($recFolder->Folder as $folder){
		recursiveGenDetails($folder);
		foreach($folder->Placemark as $place){
			// <type>: StyleUrl abhängig von <type> in der description festlegen
			//FEHLERBEHANDLUNG: kein type gefunden!!
			$place->styleUrl = STYLE_URL_STADT 
								. '#' 
								. extractStr($place->description, '<type>', '</type>');
				
			// <wLink>: Wiki-Exzerpt für den <wLink>
			//FEHLERBEHANDLUNG: kein wLink gefunden, kein Exzerpt möglich,...!!
			
			// Achtung sonderzeichen im Link ersetzten!!

			$wLink = str_replace(' ', '_', extractStr($place->description, '<wLink>', '</wLink>'));
			$wikiExzerpt = extractFromWiki('http://www.wiki-aventurica.de/index.php?title=' 
					. $wLink);
			
			//TODO: css als Datei-Einlesen
			$desc = array('<![CDATA[');
			$desc[] = '	<style type="text/css">';
			$desc[] = '		<!--';
			$desc[] = '			a:link, a:visited { text-decoration: none; color: #B21C19; }';
			$desc[] = '			a:hover { text-decoration: underline; color: #B21C19; }';
			$desc[] = '			hr { width:100%; color:black; background-color:black; height:1px; margin-bottom:0px; }';
			$desc[] = '			#wiki { font-size:10px; }';
			$desc[] = '		-->';
			$desc[] = ' </style>';
			$desc[] = '	<table cellpadding="10" cellspacing="0" align="center">';
			$desc[] = ' <tr>';
			$desc[] = ' <td>';
			$desc[] = ' <p>';
			$desc[] = ' <a href="http://www.wiki-aventurica.de/index.php?title=' . $wLink . '">' . $place->name . '</a>';
			$desc[] = ' </p>';
			$desc[] = $wikiExzerpt;
			$desc[] = '</p>';
			$desc[] = '</td>';
			$desc[] = '</tr>';
			$desc[] = '</table>';
			$desc[] = ']]>';
			$place->description = join("\n", $desc);
				
		}
	}
}

// Extrahiert den Teil eines Strings der zwischen $startTag und $endTag liegt
function extractStr($str, $startTag, $endTag){
	$posStart = strpos($str, $startTag) + strlen($startTag);
	$strLength = strpos($str, $endTag) - strlen($startTag) - strpos($str, $startTag);
	return substr($str, $posStart, $strLength);
}

// Iteriert über die Folder und Placemarks und erzeugt KML-Code für diese Elemente
function recursiveGenKML($node, $elemToGenerate){
	$kml = array();
	foreach ($node as $elemName => $elemVal) {
		$elemName . ' ' . $elemVal . '<br />';
		if (in_array($elemName, $elemToGenerate)){
			$kml[] = '<' . $elemName . '>';
			if ($elemVal->children()) {
				$kml[] = recursiveGenKML($elemVal, $elemToGenerate);
			}else{
				$kml[] = $elemVal;
			}
			$kml[] = '</' . $elemName . '>';
		}
	}
	
	return join("\n", $kml);
}

?>
