<?php
	require_once '../include/DB_Functions.php';
	$db = new DB_Functions();
	
	$response = array();

	if (isset($_GET['End_Time']) && isset($_GET['Mobile_Serial_Number']) && isset($_GET['UserName'])) 
	{
	 
		// receiving the post params
		$End_Time = $_GET['End_Time'];
		$Mobile_Serial_Number = $_GET['Mobile_Serial_Number'];
		$UserName = $_GET['UserName'];
	 
		// get the user by username and password
		$user = $db->updateLoginInfo($End_Time, $Mobile_Serial_Number, $UserName);
	 
		if ($user != false) 
		{
			$response["successMessage"] = "Record updated successfully";
			echo json_encode($response);
		}
		else 
		{
			// user is not found with the credentials
			$response["error"] = TRUE;
			echo json_encode($response);
		}
	}
	else 
	{
		// required post params is missing
		//echo $response;
		$response["error"] = TRUE;
		$response["error_msg"] = "Required parameters username or password is missing!";
		echo json_encode($response);
	}
?>