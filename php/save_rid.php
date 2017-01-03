<?php

	# - Check POST input ------------------------------------------------------------------------------------------------
	$error = 0;
	if ($_POST){
		$numElem = count($_POST);
		if ($numElem != 1){
			$error = 1;
		}
		else{
			if (!isset($_POST['rid'])){
				$error = 1;
			}
		}
	}
	else{
		$error = 1;
	}
	# ------------------------------------------------------------------------------------------------------------------------------

	# - Send Message to GCM if no errors: ------------------------------------------------------------------------
	if ($error == 1){
		echo "1";
		exit;
	}
	else{

			// Difinimos variables:
			$SQL_BEZER_HOST = 'localhost'; $SQL_BEZER_USER = 'Xmbarcina001'; $SQL_BEZER_PASS = '7nCYWMC5mr';
			$SQL_BEZER_DB = 'Xigor.odriozola_DAS';

			// 01. Creamos una conexión a la base de datos:
			$con = mysqli_connect($SQL_BEZER_HOST, $SQL_BEZER_USER, $SQL_BEZER_PASS, $SQL_BEZER_DB) or die ("Error " . mysqli_error($con));
			// 02. Check connection:
			if (mysqli_connect_errno()) {
				echo "1";
				exit;
			}
			// 03. Perform queries:
			$rid_value = $_POST['rid'];
			mysqli_query($con, "INSERT INTO `registration_ids` (`rid`) VALUES ('".$rid_value."')");
			// 04. Close connection:
			mysqli_close($con);

			echo "0";

	}
	# ------------------------------------------------------------------------------------------------------------------------------
?>
