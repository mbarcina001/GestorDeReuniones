<?php
	$SQL_HOST = 'localhost'; 
	$SQL_USER = 'Xmbarcina001'; 
	$SQL_PASS = '7nCYWMC5mr'; 
	$SQL_DB = 'Xmbarcina001_usuarios';
	
	$mail = $_POST["Mail"];
	$password = $_POST["Password"];

	$con = mysqli_connect($SQL_HOST, $SQL_USER, $SQL_PASS, $SQL_DB);
	
	if(mysqli_connect_errno()){
		echo 'Error de conexión: ' . mysqli_connect_error();
	}
	
	$consulta = "INSERT INTO Usuario(correo, contrasena) VALUES('" . $mail . "', '" . $password . "');";
	$resultado = mysqli_query($con, $consulta);
	
	if(!$resultado){
		echo $consulta;
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
		exit;
	}else{
		echo 'Registrado correctamente';
	}
	
	echo $resultado;
?>