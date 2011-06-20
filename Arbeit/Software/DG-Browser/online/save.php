<?php
/* save.php?name=test?action=save?titel=Test?adress=unwichtig?lat=23?lng=-122?type=Test
*/
echo "start<br />";
require('phpsqlajax_dbinfo.php');
    mysql_connect($server, $username, $password);
	@mysql_select_db($database) or die( "Unable to select database");   
    $qry = "INSERT INTO `markers` (`name`, `address`, `lat`, `lng`, `type`) VALUES ('".$_POST["titel"]."', '".$_POST["adress"]."', '".$_POST["lat"]."', '".$_POST["lng"]."', '".$_POST["type"]."');";
    $result = mysql_query($qry);
mysql_close();
?>
