 
<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "user";

$conn = new mysqli($servername, $username, $password, $dbname);

$sql = "SELECT tlat FROM namrata123";
$sql_2 = "SELECT tlong FROM namrata123";
$result = $conn->query($sql);
$result_2 = $conn->query($sql_2);

$array = array();
$array_2 = array();
$tablename="Namrata123";
$arfile = fopen("location/".$tablename."_ar.txt","w");
		$ar2file = fopen("location/".$tablename."_ar2.txt","w");
while($row = $result->fetch_assoc()){
	 $array[] = $row["tlat"];
	 fwrite($arfile,$row["tlat"]."\n");
}

while($row_2 = $result_2->fetch_assoc()){
	 $array_2[] = $row_2["tlong"];
	 fwrite($ar2file,$row_2["tlong"]."\n");
}
$l = sizeof($array);

fclose($arfile);
fclose($ar2file);

$JAVA_HOME = "\jdk1.8.0_121";
$PATH = "$JAVA_HOME/bin:".getenv('PATH');
putenv("JAVA_HOME=$JAVA_HOME");
putenv("PATH=$PATH");
$output = shell_exec("java jDBScan ".$arfile." ".$ar2file);
if(strcmp($output,"0"))
	$tmp =array("success"=>false,"message"=>"Suspicious Transaction-Confirmation Required in location");
else
	$tmp =array("success"=>false,"message"=>"Transaction Successful");

//echo $output;
echo json_encode($tmp);	

$conn->close();
?>

