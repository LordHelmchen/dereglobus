<?php

function testSession(){
	$time = microtime(true);
	$span = 3;
	$end = $time + $span;
	while(microtime(true)<$end){
		$_SESSION['status'] += 1;
	}
	//echo "done\n";
}

?>
