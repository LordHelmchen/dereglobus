<?php

/*
 *	Interface zum Wiki-Aventurica
**/

// Enthält teilweise Code: http://www.it-academy.cc/article/722/PHP:+Fremde+Webseiten+parsen+auslesen.html
function extractFromWiki($url){
	// Für unterschiedlichen Content: Stadt, Publikation,...
	
	// Zeichenfolge vor relevanten Einträgen
	$startStr = 'rel="nofollow">Ulisses Spiele GmbH</a>';
	
	// bis zum nächsten html tag bzw. Zeichenfolge nach relevanten Einträgen
	$endStr = "</p>"; 
		
	$file = fopen ($url,"r");

	if (trim($file) == "") {
		//FEHLERBEHANDLUNG: leere Datei oder Fehlercode..
		echo "Service out of order";
		} else {
		$i=0;
		while (!feof($file)) {
			// Wenn das File entsprechend groß ist, kann es unter Umständen
			// notwendig sein, die Zahl 2048 entsprechend zu erhöhen. Im Falle
			// eines Buffer-Overflows gibt PHP eine entsprechende Fehlermeldung aus.
			$zeile[$i] = fgets($file,2048);
			$i++;
		}
		fclose($file);
	}
	
	// Nun werden die Daten entsprechend gefiltert.
	$extract = false;
	$result = array();
	for ($j=0;$j<$i;$j++) {
		if (strpos($zeile[$j],$startStr)) {
			$extract = true;
			$j = $j + 2;
		}elseif(strstr($zeile[$j],$endStr)){
			$extract = false;
		}
		
		if($extract){
			$result[] = str_replace('href="/index.php?', 'href="http://www.wiki-aventurica.de/index.php?', $zeile[$j]);
		}
	}
		
	// Ausgabe der Daten
		
	return join("\n", $result);

}
?>
