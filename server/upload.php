<?php
	$servername = "localhost";
	$username = "root";
	$password = "";
	$dbname = "user";

	$con = new mysqli($servername, $username, $password, $dbname) or die('Unable to connect');
	
	if (isset($_GET['amount']) && isset($_GET['cvv']))
	{    
	  
		$image = $_FILES['image'];
		$amount=$_GET['amount'];
		$cvv=$_GET['cvv'];
		$username=$_GET['username'];
		$fname = substr($username,0,strpos($username," "));
		$file_tmp = $_FILES['image']['tmp_name'];
		$file_name = $_FILES['image']['name'];
		if(move_uploaded_file($file_tmp,"upload/".$fname.$cvv.$file_name)){
			extraction($amount,$cvv,$fname.$cvv.$file_name,$username);			
		}
		else{
			uploadingfailed();
		}
	
		$actualpath = "http://localhost/server/upload/$file_name";
		
	}
	else{
		uploadingfailed();
	}
	
	function success($tablename) {
		$x=unlink("details/".$tablename."details.txt");
		$x=unlink("upload/".$tablename."image.jpg");
		$x=unlink("location/".$tablename."_ar.txt");
		$x=unlink("location/".$tablename."_ar2.txt");
		$tmp =array("success"=>false,"message"=>"Transaction Successful");
		echo json_encode($tmp);	
	}
 
	function uploadingfailed() {
		$tmp =array("success"=>false,"message"=>"Failed Upload");
		echo json_encode($tmp);	
	}

	function imageexpired($tablename) { 
		$x=unlink("details/".$tablename."details.txt");
		$x=unlink("upload/".$tablename."image.jpg");
		$tmp =array("success"=>false,"message"=>"Image expired. Please upload a new one!");
		echo json_encode($tmp);	
	}

	function amountconfirmation($tablename) { 
		$x=unlink("details/".$tablename."details.txt");
		$x=unlink("upload/".$tablename."image.jpg");
		$tmp =array("success"=>false,"message"=>"Suspicious Transaction-Confirmation is required in amount");
		echo json_encode($tmp);	
	}
	
	function locationconfirmation($tablename) { 
		$x=unlink("details/".$tablename."details.txt");
		$x=unlink("upload/".$tablename."image.jpg");
		$x=unlink("location/".$tablename."_ar.txt");
		$x=unlink("location/".$tablename."_ar2.txt");
		$tmp =array("success"=>false,"message"=>"Suspicious Transaction-Confirmation Required in location");
		echo json_encode($tmp);	
	}
	
	function invaliddata($tablename) {
		$x=unlink("details/".$tablename."details.txt");
		$x=unlink("upload/".$tablename."image.jpg");
		$tmp =array("success"=>false,"message"=>"Invalid Data");
		echo json_encode($tmp);	
	}
	
	function extraction($amount,$cvv,$fname,$username) {
	 $JAVA_HOME = "\jdk1.8.0_121";
	 $PATH = "$JAVA_HOME/bin:".getenv('PATH');
	 putenv("JAVA_HOME=$JAVA_HOME");
	 putenv("PATH=$PATH");
	 $details=substr($fname,0,strpos($fname,"image"))."details.txt";
	 $output = shell_exec("java decrypt ".$details." ".$fname);
	 validate($output,$amount,$cvv,$username);
	}
	
	function validate($output,$amount,$cvv1,$mname){
	date_default_timezone_set("Asia/Kolkata");
	$name=substr($output,strrpos($output,"Name: ")+6,strrpos($output," Card")-(strrpos($output,"Name: ")+6));
	$card=substr($output,strrpos($output,"Card ")+9,strrpos($output," Exp")-(strrpos($output,"Card ")+9));
	$exp=substr($output,strrpos($output,"Exp Date: ")+10,strrpos($output," CVV")-(strrpos($output,"Exp Date: ")+10));
	$cvv=substr($output,strrpos($output,"CVV: ")+5,strrpos($output," Lat")-(strrpos($output,"CVV: ")+5));
	$lat=substr($output,strrpos($output,"Latitude: ")+10,strrpos($output," Long")-(strrpos($output,"Latitude: ")+10));
	$long=substr($output,strrpos($output,"Longitude: ")+11,strrpos($output," Location")-(strrpos($output,"Longitude: ")+11));
	$date=substr($output,strrpos($output,"Date: ")+10,6)." ".substr($output,strrpos($output,"Date: ")+26);
	$currentDate = date("M j Y");
	$hours= substr($output,strrpos($output,"Date: ")+17,2);
	$mins= substr($output,strrpos($output,"Date: ")+20,2);
	
	$mname1=substr($mname,0,strrpos($mname," "));
	$tablename= $mname1.$cvv1;
	
	if(strcmp($currentDate,$date))
	{
		$currentHours=date("H");
		$currentMins=date("i");
		
		if( $currentMins < 5 ){
			if($mins < 5 )
				$mins=$mins+5;
			else{
			$currentMins=$currentMins+55;
			$currentHours=$currentHours-1;
			if($currentHours <10)
				$currentHours="0".$currentHours;
			}
		}
		else	
			$currentMins=$currentMins-5;
		
		if(strcmp($hours,$currentHours)==0){
			if($mins>=$currentMins)
			{
				$servername = "localhost";
				$username = "root";
				$password = "";

				$conn = new mysqli($servername, $username, $password, 'user');
				
				if ($conn->connect_error) {
					die("Connection failed: " . $conn->connect_error);
				}
				
				$sql = "SELECT * FROM customer WHERE cnumber='$card' AND cvv='$cvv' AND cvv='$cvv1' AND expiry='$exp' AND cname='$name' AND cname='$mname'";
				$result = $conn->query($sql);
			
				if ($result->num_rows > 0) {
					amountcheck($lat,$long,$date,$amount,$mname,$cvv);
					}
				else
					invaliddata($tablename);
				$conn->close();	
			}
			else imageexpired($tablename);
		}
		else imageexpired($tablename);
	}
	else imageexpired($tablename);
	}
	
	
	function dbLocation($lat,$long,$tid,$date,$tamt,$tablename){
		
		$servername = "localhost";
		$username = "root";
		$password = "";

		$conn = new mysqli($servername, $username, $password, 'user');
		
		if ($conn->connect_error) {
			die("Connection failed: " . $conn->connect_error);
		} 
		
		$sql = "SELECT tlat FROM $tablename";
		$sql_2 = "SELECT tlong FROM $tablename";
		$result = $conn->query($sql);
		$result_2 = $conn->query($sql_2);

		$arfile = fopen("location/".$tablename."_ar.txt","w");
		$ar2file = fopen("location/".$tablename."_ar2.txt","w");
		while($row = $result->fetch_assoc()){
			 fwrite($arfile,$row["tlat"]."\n");
		}

		while($row_2 = $result_2->fetch_assoc()){
			 fwrite($ar2file,$row_2["tlong"]."\n");
		}

		fclose($arfile);
		fclose($ar2file);
		$arfilen="location/".$tablename."_ar.txt";
		$ar2filen="location/".$tablename."_ar2.txt";
		
		$JAVA_HOME = "\jdk1.8.0_121";
		$PATH = "$JAVA_HOME/bin:".getenv('PATH');
		putenv("JAVA_HOME=$JAVA_HOME");
		putenv("PATH=$PATH");
		$output1=1;
		$output1 = shell_exec("java jDBScan ".$arfilen." ".$ar2filen);
		if($output1==0){
			locationconfirmation($tablename);
		}
		else if($output1==1){
			$tid= $tid + 1;		
			$sql_in = "INSERT INTO $tablename (tid, tlat, tlong, tdate, tamt) VALUES ('$tid', '$lat', '$long', '$date', '$tamt')";	
			mysqli_query($conn, $sql_in);
			success($tablename);
		}
		
	}
	
	function amountCheck($lat,$long,$date,$tamt,$mname,$cvv){
	
	$servername = "localhost";
	$username = "root";
	$password = "";

	$conn = new mysqli($servername, $username, $password, 'user');
	
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	} 
	
	$mname=substr($mname,0,strrpos($mname," "));
	$tablename= $mname.$cvv;
	
	$query="Select * from $tablename";
		$result=mysqli_query($conn, $query);
		if(empty($result)){
			$query="Create table $tablename  ( tid int(10),tlat float(4,2),tlong float(4,2),tdate varchar(10),tamt int(10), PRIMARY KEY(tid) )";
			$result=mysqli_query($conn, $query);
		}
	
	
	$sql = "SELECT tamt FROM $tablename";
	$result = $conn->query($sql);
	$array = array();
	while($row = $result->fetch_assoc()){
		$array[] = $row["tamt"];
	}

	$temp=($tamt)-(array_sum($array)/500);
	if($temp>30000)
		amountconfirmation($tablename);	
	else
		dbLocation($lat,$long,sizeof($array),$date,$tamt,$tablename);
	$conn->close();
	}
					
?>