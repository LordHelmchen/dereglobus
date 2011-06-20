<?php

// Titel wird mit kurz.php?name=Gareth eingegeben
$name = trim ((!empty($_POST['title'])) ? $_POST['title'] : $_GET['title'] );
$url = "http://www.wiki-aventurica.de/index.php?title=" . $name;
echo "EXZERPT von " . $url;

// Zeichenfolge vor relevanten Einträgen
$startstring = "<a name=\"Kurzbeschreibung\">";

// bis zum nächsten html tag bzw. Zeichenfolge nach relevanten Einträgen
$endstring = "<div class=\"printfooter\">"; 

// Der zu lesende htmlCode
$instr = "";

$file = @fopen ($url,"r");

if (trim($file) == "") {
echo "Service out of order";
} else {
	$i=0;
	while (!feof($file)) {
		// Wenn das File entsprechend groß ist, kann es unter Umständen
		// notwendig sein, die Zahl 2000 entsprechend zu erhöhen. Im Falle
		// eines Buffer-Overflows gibt PHP eine entsprechende Fehlermeldung aus.
		$tmp = fgets($file,4096);
		$instr = $instr .  utf8_decode ($tmp);
	}
}
fclose($file);


// Nun werden die Daten entsprechend gefiltert.
if ($resa = strstr($instr,$startstring)) {
	$resb = str_replace($startstring, "", $resa);
	$endstueck = strstr($resb, $endstring);
	$resultat .= str_replace($endstueck,"",$resb);
}

// UMWANDLUNGEN
// src="/images >> src="http://www.wiki-aventurica.de/images
$resultat = str_replace("src=\"/images", "src=\"http://www.wiki-aventurica.de/images", $resultat);
// index.php?title=Aventurien in kurz.php?title
$resultat = str_replace("/index.php?title=", "kurz.php?title=", $resultat);

// Ausgabe der Daten

echo $resultat . "ENDE";


?>
