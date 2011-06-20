<?php

/*
 *	Steuerungszentrale für Endnutzerkommunikation
 *		- Aufruf der unterschiedlichen WIZARD-Funktionen per Link
**/

require_once('D:\Projekte\DereGlobus\Workspace\Projekte\Software\DG Wizard - Server\autoMark.php');
require_once('D:\Projekte\DereGlobus\Workspace\Projekte\Software\DG Wizard - Server\LogMe.php');
//require_once('autoMark.php');
//require_once('LogMe.php');
require_once('D:\Projekte\DereGlobus\Workspace\Projekte\Software\DG Wizard - Server\test.php');

switch ($_GET['exec']){
	case 'genFromCode':
		genFromCode($_POST['fileName'], $_POST['kmlCode']);
		break;
	case 'testSession':
		session_start();
		$_SESSION['status'] = 0;
		testSession();
		echo "SID: " . session_id();
		break;
	case 'getStatus':
		session_start();
		break;	
		
}

?>
