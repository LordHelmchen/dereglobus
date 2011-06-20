<?php
// Titel wird mit php?name=Gareth eingegeben
$name = trim ((!empty($_POST['title'])) ? $_POST['title'] : $_GET['title'] );
// aus der Datenbank wird die positionen zu diesem Ort gesucht

require('phpsqlajax_dbinfo.php');

 // Opens a connection to a MySQL server.
$connection = mysql_connect ($server, $username, $password);
if (!$connection) {die('Not connected : ' . mysql_error());}

// Sets the active MySQL database.
$db_selected = mysql_select_db($database, $connection);
if (!$db_selected) 
{die ('Can\'t use db : ' . mysql_error());}

// Selects all the rows in the markers table.
$query = 'SELECT * FROM markers WHERE name = \'' . $name . '\'';
$result = mysql_query($query);
if (!$result) {die('Invalid query: ' . mysql_error()); }

// Iterates through the rows, printing a node for each row.
while ($row = @mysql_fetch_assoc($result)) 
{
  $kml[] = ' <Point>';
  $kml[] = ' <coordinates>' . $row['lng'] . ','  . $row['lat'] . '</coordinates>';
  $kml[] = ' </Point>';
} 

// End XML file
$kml[] = ' </Document>';
$kml[] = '</kml>';
$kmlOutput = join("\n", $kml);
header('Content-type: application/vnd.google-earth.kml+xml');
echo $kmlOutput;
?>
