<?php
$JAVA_HOME = "\jdk1.8.0_121";
$PATH = "$JAVA_HOME/bin:".getenv('PATH');
putenv("JAVA_HOME=$JAVA_HOME");
putenv("PATH=$PATH");
$output = 23;
$output = shell_exec("java jDBScan ar.txt ar2.txt");

echo $output;

?>