<?php
	ini_set('display_errors', '1');

	# - Global variables ----------------------------------------------------------------------------------------------------
	$api_key = " AIzaSyDld2twscGUlZYrrmlteLiVneBEIkG0mCA";
	$GCM_url = "https://android.googleapis.com/gcm/send";
	# ------------------------------------------------------------------------------------------------------------------------------

	# - Chequea si hay entradas desde formulario ------------------------------------------------------------------------------------------------
	$error = 0;
	if ($_POST){
		$numElem = count($_POST);
		if ($numElem != 5){
			$error = 1;
		}
	}
	else{
		$error = 1;
	}
	# ------------------------------------------------------------------------------------------------------------------------------

	// Send Message to GCM if no errors: --------------------------------------------------------------------------
	if ($error == 1){
	?>
	<html>
	<body>
		<center>Ha ocurrido un error al enviar el formulario</center>
	</body>
	</html>
	<?php
	}else{
		// 00.- Obtenemos registration_ids de la base de datos:
		// Definimos variables:
		$SQL_BEZER_HOST = 'localhost'; $SQL_BEZER_USER = 'Xmbarcina001'; $SQL_BEZER_PASS = '7nCYWMC5mr';
		$SQL_BEZER_DB = 'Xmbarcina001_ids';
		// a. Creamos una conexión a la base de datos:
		$con = mysqli_connect($SQL_BEZER_HOST, $SQL_BEZER_USER, $SQL_BEZER_PASS, $SQL_BEZER_DB) or die ("Error " . mysqli_error($con));
		// c. Perform queries:
		$emaitza = mysqli_query($con, "SELECT * FROM Id;");
		$ids = array();
		while ($row = mysqli_fetch_array($emaitza)){
			$ids[] = $row['id'];	// Atzean gehituz doa
		}
		// d. Close connection:
		mysqli_close($con);

		//00.- Se preparan los parámetros a enviar
		$nombre = $_POST["nombre"];
		$lugar = $_POST["lugar"];
		
		if (false === strtotime($_POST["fecha"])){ 
			echo "La fecha es incorrecta";
		}else{		
			$dia = date('d', strtotime($_POST["fecha"]));
			$mes = date('m', strtotime($_POST["fecha"]));
			$anyo = date('Y', strtotime($_POST["fecha"]));
			$horainicio = date('g', strtotime($_POST["horainicio"]));
			$minutoinicio = date('i', strtotime($_POST["horainicio"]));
			$horafin = date('g', strtotime($_POST["horafin"]));
			$minutofin = date('i', strtotime($_POST["horafin"]));
			$fecha = $_POST["fecha"] . " " . $horainicio . ":" . $minutoinicio . ":00";
			
			// 01.- Preparamos la cabecera del mensaje:
			$cabecera = array(
				"Authorization: key=$api_key",
				"Content-Type: application/json"
			);

			// 02.- Preparamos el contenido del mensaje:
			$data = array(
				"Nombre" => $nombre,
				"Lugar" => $lugar,
				"Dia" => $dia,
				"Mes" => $mes,
				"Anyo" => $anyo,
				"HoraInicio" => $horainicio,
				"HoraFin" => $horafin,
				"MinutoInicio" => $minutoinicio,
				"MinutoFin" => $minutofin,
				"Fecha" => $fecha
			);
			
			$info= array(
				"registration_ids" => $ids,
				"collapse_key" => "Reunion",
				"time_to_live" => 200,
				"data" => $data
			);

			// Inicializamos el gestor de curl:
			$ch = curl_init();
			curl_setopt($ch, CURLOPT_URL, $GCM_url);

			// Configuramos el mensaje:
			curl_setopt($ch, CURLOPT_HTTPHEADER, $cabecera);
			curl_setopt($ch, CURLOPT_POST, true);
			curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($info));
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

			// Enviamos el mensaje:
			$resultado = curl_exec($ch);

			// Cerramos el gestor de Curl:
			curl_close($ch);
			echo $resultado;
		}
		
	}
	# ------------------------------------------------------------------------------------------------------------------------------
?>
