<?php

/*
 *	Stellt einen simplen Logging-Mechanismus bereit.
**/

// Zugriff auf Pfad- und URL-Konstanten
require_once('Config.php');

function logMe($text){
	if(is_writeable(FILE_PATH_LOGGING)){
		
		if(!$logfile = fopen(FILE_PATH_LOGGING, a)){
			echo "Kann die Datei " . FILE_PATH_LOGGING . " nicht öffnen";
			exit;
		}
		
		$logEntry = date('c') . " | $text\n";
		
		if (!fwrite($logfile, $logEntry)) {
			echo "Kann in die Datei " . FILE_PATH_LOGGING . " nicht schreiben";
			exit;
		}
		
		fclose($logfile);
	}
}
?>
