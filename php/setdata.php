<?php
	$SQL_HOST = 'localhost'; 
	$SQL_USER = 'Xmbarcina001'; 
	$SQL_PASS = '7nCYWMC5mr'; 
	$SQL_DB = 'Xmbarcina001_usuarios';
	
	$mail = $_POST["mail"];
	$nombre = $_POST["nombre"];
	$apellidos = $_POST["apellidos"];
	$edad = $_POST["edad"];
	$ubicacion = $_POST["ubicacion"];

	$con = mysqli_connect($SQL_HOST, $SQL_USER, $SQL_PASS, $SQL_DB);
	
	if(mysqli_connect_errno()){
		echo 'Error de conexión: ' . mysqli_connect_error();
	}
	
	$consulta = "UPDATE Usuario SET nombre = '" . $nombre . "', apellidos = '" . $apellidos . "', edad= '" . $edad . "', ubicacion = '" . $ubicacion . "' WHERE correo='" . $mail . "';";
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