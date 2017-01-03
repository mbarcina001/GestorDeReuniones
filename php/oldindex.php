<?php
	$SQL_HOST = 'localhost'; 
	$SQL_USER = 'Xmbarcina001'; 
	$SQL_PASS = '7nCYWMC5mr'; 
	$SQL_DB = 'Xmbarcina001_usuarios';

	$con = mysqli_connect($SQL_HOST, $SQL_USER, $SQL_PASS, $SQL_DB);
	
	if(mysqli_connect_errno()){
		echo 'Error de conexión: ' . mysqli_connect_error();
	}
	
	$resultado = mysqli_query($con, "SELECT * FROM Usuario;");
	
	if(!$resultado){
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
		exit;
	}
	
	$arrayresultados = array();
	$i = 0;
	
	echo 'DATOS GUARDADOS EN LA BASE DE DATOS<br>';
	
	while($fila = mysqli_fetch_row($resultado)){
		echo 'id=' . $fila[0] . " : " . $fila[1] . " (pass: " . $fila[2] . ")<br>";
		
		$arrayresultados[$i] = array(
			'Email' => $fila[1],
			'Password' => $fila[2]
		);	
		$i++;
	}
	
	echo '<br>';
	echo json_encode($arrayresultados);
?>