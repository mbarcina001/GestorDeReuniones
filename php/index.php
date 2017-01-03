<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>Mikel Barcina - GCM php server</title>
	<!-- meta -->
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="pragma" content="no-cache">
	<meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
	<div>
		<center>
		<form action="send2GCM.php"  method="POST">
			<br><br>
			Rellene el formulario y haga click en el botón para enviar un mensaje GCM<br>
			que añada una reunión en la agenda de todos los usuarios.<br>
			(Esta página se visualiza mejor con Chrome)<br>
			<br><br>
			<p>Nombre: <input type="text" name="nombre" /></p>
			<p>Lugar: <input type="text" name="lugar" /></p>
			<p>Fecha: <input type="date" name="fecha" step="1" min="2013-01-01" max="2013-12-31" value="2013-01-01" /></p>
			<p>Hora inicio: <input type="time" name="horainicio" value="00:00:00" max="22:59:59" min="00:00:00" step="1" /></p>
			<p>Hora fin: <input type="time" name="horafin" value="00:00:00" max="22:59:59" min="00:00:00" step="1" /></p>
			<input type="submit" value="SEND">
		</form>
		</center>
	</div>
</body>
</html>
