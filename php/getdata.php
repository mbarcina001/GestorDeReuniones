<?php
	$SQL_HOST = 'localhost'; 
	$SQL_USER = 'Xmbarcina001'; 
	$SQL_PASS = '7nCYWMC5mr'; 
	$SQL_DB = 'Xmbarcina001_usuarios';
	
	$mail = $_POST["mail"];

	$con = mysqli_connect($SQL_HOST, $SQL_USER, $SQL_PASS, $SQL_DB);
	
	if(mysqli_connect_errno()){
		echo 'Error de conexión: ' . mysqli_connect_error();
	}
	
	$consulta = "SELECT nombre, apellidos, edad, ubicacion FROM Usuario WHERE correo='" . $mail . "';";
	
	$resultado = mysqli_query($con, $consulta);
	
	

	if(!$resultado){
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
		exit;
	}else{
		while($row = mysqli_fetch_assoc($resultado)){
			$test[] = $row; 
		}
	}
	
	echo json_encode($test);
?>