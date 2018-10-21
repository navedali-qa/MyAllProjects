<?php
	require_once '../include/DB_Functions.php';
	$db = new DB_Functions();
	
	$response = array();

	if (isset($_GET['Username']))
	{
		$Username = $_GET['Username'];
	
		$user = array();
		$user = $db->getRecentUserName($Username);
	 
		if ($user != false) 
		{
			$response["FirstName"] = $user["FirstName"];
			$response["LastName"] = $user["LastName"];
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