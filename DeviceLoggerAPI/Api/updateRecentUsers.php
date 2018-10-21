<?php
	require_once '../include/DB_Functions.php';
	$db = new DB_Functions();
	
	$response = array();

	if (isset($_GET['Mobile_Serial_Number']))
	{
		$Mobile_Serial_Number = $_GET['Mobile_Serial_Number'];
	
		$user = array();
		$user = $db->getRecentUsers($Mobile_Serial_Number);
	 
		if ($user != false) 
		{
			$response = $user;		
			echo json_encode($response);
		}
		else 
		{
			$response["error"] = TRUE;
			echo json_encode($response);
		}
	}
	else 
	{
		// required post params is missing
		//echo $response;
		$response["error"] = TRUE;
		$response["error_msg"] = "Required parameters are missing!";
		echo json_encode($response);
	}
?>