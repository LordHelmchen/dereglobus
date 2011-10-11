<?
$input = $_POST["text1"]; // Man umgeht register_globals=off nicht, dass ist schlechter stil und unsicher

// $myarray = array($input);
$mn=split(" ",$input);
$result = array_reverse($mn);
while (list ($key, $val) = each ($result)) {
echo "$val ";
}

    ?>