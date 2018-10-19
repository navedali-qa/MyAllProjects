<?php
	require_once '../include/DB_Functions.php';
	$db = new DB_Functions();
	
	$response = array();

	if (isset($_GET['username']) && isset($_GET['password'])) 
	{
	 
		// receiving the post params
		$username = $_GET['username'];
		$password = $_GET['password'];
	 
		$user = $db->getAdminByusernameAndPassword($username, $password);
	 
		if ($user != false) 
		{
			// use is found
			$response["error"] = False;
			echo json_encode($response);
		}
		else 
		{
			// user is not found with the credentials
			$response["error"] = TRUE;
			$response["error_msg"] = "Login credentials are wrong. Please try again!";
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