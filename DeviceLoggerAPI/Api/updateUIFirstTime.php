<?php
	require_once '../include/DB_Functions.php';
	
	$urlParams = explode('/', $_SERVER['REQUEST_URI']);
	$functionName = $urlParams[4];
	$functionName($urlParams);

	function isUserLoggedIn ($urlParams) 
	{
		$db = new DB_Functions();
		$logged = $db->getLogedInUser($urlParams[5]);
		if ($logged != false) 
		{
			// use is found
			$response["Username"] = $logged["Username"];
			$response["FirstName"] = $logged["FirstName"];
			$response["LastName"] = $logged["LastName"];
			$response["Password"] = $logged["Password"];
			echo json_encode($response);
		}
		else 
		{
			// user is not found with the credentials
			$response["error"] = TRUE;
			$response["err_msg"] = "User not logged in";
			echo json_encode($response);
		}
	}
?>