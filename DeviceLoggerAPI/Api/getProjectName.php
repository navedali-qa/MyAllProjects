<?php
	require_once '../include/DB_Functions.php';
	$db = new DB_Functions();
	
	$response = array();

	if (isset($_GET['Mobile_Name']) && isset($_GET['Brand']) && isset($_GET['Mobile_Serial_Number']) && isset($_GET['Version']) && isset($_GET['Screen_Size'])) 
	{
		$Mobile_Name = $_GET['Mobile_Name'];
		$Brand = $_GET['Brand'];
		$Mobile_Serial_Number = $_GET['Mobile_Serial_Number'];
		$Version = $_GET['Version'];		
		$Screen_Size = $_GET['Screen_Size'];
	
		$user = $db->getProject($Mobile_Name, $Brand, $Mobile_Serial_Number, $Version, $Screen_Size);
	 
		if ($user != false) 
		{
			$response["Project"] = $user["Project"];
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