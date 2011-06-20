<?php
//Harleaquin: Header am besten immer als erstes sonst funktioniert das nicht zuverlässig
header('Content-type: application/vnd.google-earth.kml+xml');

// Creates an array of strings to hold the lines of the KML file.
// mir ist nicht klar ob das sein mus
$kml = array('<?xml version="1.0" encoding="UTF-8"?>');
$kml[] = '<kml xmlns="http://earth.google.com/kml/2.2">';
$kml[] = ' <Document>';
$kml[] = ' <Style id="linkStyle">';
$kml[] = ' <IconStyle id="linkIcon">';
$kml[] = ' <Icon>';
$kml[] = ' <href>http://maps.google.com/mapfiles/kml/pal2/icon55.png</href>';
$kml[] = ' </Icon>';
$kml[] = ' </IconStyle>';
$kml[] = ' </Style>';
$kml[] = ' <Style id="stadtStyle">';
$kml[] = ' <IconStyle id="stadtIcon">';
$kml[] = ' <Icon>';
$kml[] = ' <href>http://maps.google.com/mapfiles/kml/pal2/icon25.png</href>';
$kml[] = ' </Icon>';
$kml[] = ' </IconStyle>';
$kml[] = ' </Style>';



// Ersteinmal die Beschreibung aus dem Wiki laden

// Titel wird mit kml.php?name=Gareth eingegeben
$name = trim ((!empty($_POST['title'])) ? $_POST['title'] : $_GET['title'] );
$url = "http://www.wiki-aventurica.de/index.php?title=" . $name;

// Der zu lesende htmlCode
$instr = "";
$file = @fopen ($url,"r");

if (trim($file) == "") {echo "Service out of order";} 
else {
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

// Zeichenfolge vor relevanten Einträgen
$startstring = "<a name=\"Kurzbeschreibung\">";
// bis zum nächsten html tag bzw. Zeichenfolge nach relevanten Einträgen
$endstring = "<!--"; 
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


// Dann aus der Datenbank positionen zu diesem Ort suchen

require('phpsqlajax_dbinfo.php');

 // Opens a connection to a MySQL server.
$connection = mysql_connect ($server, $username, $password);
if (!$connection) {die('Not connected : ' . mysql_error());}

// Sets the active MySQL database.
$db_selected = mysql_select_db($database, $connection);
if (!$db_selected) 
{die ('Can\'t use db : ' . mysql_error());}

//Harleaquin: Um SQL Injections zu vermeiden: Sonderzeichen Escapen
$esc_name=mysql_real_escape_string($name, $connection);

// Selects all the rows in the markers table.
$query = 'SELECT * FROM markers WHERE name = \'' . $esc_name . '\'';
$result = mysql_query($query);
if (!$result) {die('Invalid query: ' . mysql_error()); }

// Iterates through the rows, printing a node for each row.
while ($row = @mysql_fetch_assoc($result)) 
{
  $kml[] = ' <Placemark id="placemark' . $row['id'] . '">';
  $kml[] = ' <name>' . htmlentities($row['name']) . '</name>';
  $kml[] = ' <description><![CDATA[ ' . $resultat . ' ]]>
</description>';
  $kml[] = ' <styleUrl>#' . ($row['type']) .'Style</styleUrl>';
  $kml[] = ' <Point>';
  $kml[] = ' <coordinates>' . $row['lng'] . ','  . $row['lat'] . '</coordinates>';
  $kml[] = ' </Point>';
  $kml[] = ' </Placemark>';
} 

// End XML file
$kml[] = ' </Document>';
$kml[] = '</kml>';
$kmlOutput = join("\n", $kml);echo $kmlOutput;
?>
