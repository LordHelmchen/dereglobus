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
require_once('Wiki_Interface.php');

// Zugriff auf Pfad- und URL-Konstanten
require_once('Config.php');

// Logging-Komponente: logMe($logtext)
require_once('LogMe.php');

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
	if (!$userKmlFile = fopen(HOME_PATH . $fileName . '.kml', w)) {
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
	
	$kmlFromFile = getKMLFromFile(HOME_PATH . 'UmgewandelteDateien.kml');
	
	$kml = array('<?xml version="1.0" encoding="UTF-8"?>');
	$kml[] = '<kml xmlns="http://www.opengis.net/kml/2.2" xmlns:gx="http://www.google.com/kml/ext/2.2" xmlns:kml="http://www.opengis.net/kml/2.2" xmlns:atom="http://www.w3.org/2005/Atom">';
	$kml[] = '<Document>';
	//TODO: Noch Fehlerhaft, überschreibt bisherigen inhalt
	$kml[] = '<NetworkLink>';
	$kml[] = '<name>' . $fileName . '</name>';
	$kml[] = '<Link>';
	$kml[] = '<href>' . HOME_URL . $fileName . '.kml' . '</href>';
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
	if (!$userKmlFile = fopen(HOME_PATH . 'UmgewandelteDateien.kml', w)) {
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
// ACHTUNG: Was passiert wenn das Skript nach 15 Minuten noch nciht fertig ist und das gleiche
//   Skript nochmal gestartet wird. Beide greifen auf die gleiche Datei zu...
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
		//TODO: FEHLERBEHANDLUNG: Pfad-Fehler!!
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

function recursiveGenDetails($node){
	 //Jedes Element der KML-Datei wird durchlaufen angefangen bei <Document>
	foreach ($node as $elemName => $elemVal) {
		//Prüfe ob das Element <Document>, <Folder> oder <Placemark> ist, wenn nicht tu nichts.
		switch ($elemName){
			// Element ist <Document> oder <Folder>: Durchlaufe das Element
			case 'Document':
			case 'Folder':
				logMe('F: ' . $elemName . ' ' . $elemVal);
				recursiveGenDetails($elemVal);
				break;
			// Element ist <Placemark>: generiere Details
			case 'Placemark':
				logMe('P: ' . $elemName . ' ' . $elemVal);
				genDetails($elemVal);
				break;
		} 
	}
}

function genDetails($place){
	/*
	 * <objektklasse>
	 * Zur unterscheidung was gemacht werden muss (Wappen, Meisterinfo, Coverbild etc.)
	 * Ein MUSS für JEDES zu bearbeitendes Element
	 */
	if(!$objektklasse = extractStr($place->description, '<$objektklasse>', '</$objektklasse>') === false){
					
	}else{
		//TODO: Nutzerfeedback FEHLER
	}
				
	/*
	 * <style>
	 * StyleUrl abhängig von <style> in der description festlegen
	 */
				 
	//TODO: FEHLERBEHANDLUNG leerere String
	//TODO: Ändern auf <style>
				
	$style = extractStr($place->description, '<type>', '</type>');
	logMe("style: " . $style);
	if(!$style === FALSE){
		$place->styleUrl = STYLE_URL_STADT . '#' . $style;
	}else{
		//TODO: Nutzerfeedback FEHLER
	}
				
							
	/*
	 * <wLinkBeschreibung>
	 */ 
	//TODO: FEHLERBEHANDLUNG: kein wLink gefunden, kein Exzerpt möglich,...!!
				
	// Achtung sonderzeichen im Link ersetzten!!
	$wLink  = extractStr($place->description, '<wLink>', '</wLink>');
	if(!$wLink === FALSE){
		$wLink = utf8_decode(
					str_replace(' ', '_',
						$wLink
					)
				);
							
		logMe("wLink: " . $wLink);
		$wikiExzerpt = extractFromWiki('http://www.wiki-aventurica.de/index.php?title=' 
				. $wLink);
					
		$desc = array('<![CDATA[');
		  			
		 //CSS Style des Balloon-HTML-Fensters
		$desc[] = '<link rel="stylesheet" type="text/css" href="' . STYLE_URL . '">';
					
		$desc[] = '<table cellpadding="10" cellspacing="0" align="center">';
		$desc[] = '<tr>';
		$desc[] = '<td>';
		$desc[] = '<p>';
		$desc[] = '<a href="http://www.wiki-aventurica.de/index.php?title=' . $wLink . '">' . $place->name . '</a>';
		$desc[] = '</p>';
		$desc[] = utf8_decode($wikiExzerpt);
		$desc[] = '</p>';
		$desc[] = '</td>';
		$desc[] = '</tr>';
		$desc[] = '</table>';
		$desc[] = ']]>';
		$place->description = join("\n", $desc);
	}else{
		//TODO: Nutzerfeedback FEHLER
	}
				
	/*
	 * <autor>
	 */
				 
	/*
	 * <quellen>
	 */
				 
	/*
	 * <lizenz>
	 */
				 
	/*
	 * <miBeschreibungWLink>
	 */
}


// Extrahiert den Teil eines Strings der zwischen $startTag und $endTag liegt
function extractStr($str, $startTag, $endTag){
	logMe('$str: ' . $str);
	logMe('$startTag: ' . $startTag . ' ' . strpos($str, $startTag));
	logMe('$endTag: ' . $endTag . ' ' . strpos($str, $endTag));
	if (substr_count($str, $startTag) >0 && substr_count($str, $endTag) >0){
		$posStart = strpos($str, $startTag) + strlen($startTag);
		$strLength = strpos($str, $endTag) - strlen($startTag) - strpos($str, $startTag);
		return substr($str, $posStart, $strLength);
	}else{
		logMe("String enthält kein " . $startTag . $endTag);
		return FALSE;
	}	
}
/* 
 * Iteriert über die $node, beachtet dort nur die Elemente in $elemToGenerate 
 * und erzeugt KML-Code für diese Elemente
 */
function recursiveGenKML($node, $elemToGenerate){
	$kml = array();
	foreach ($node as $elemName => $elemVal) {
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
