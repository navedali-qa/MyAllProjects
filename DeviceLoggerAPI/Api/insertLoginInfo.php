<?php
	require_once '../include/DB_Functions.php';
	$db = new DB_Functions();
	
	$response = array();

	if (isset($_GET['UserName']) && isset($_GET['Mobile_Serial_Number']) && isset($_GET['Start_Time']) && isset($_GET['End_Time']) && isset($_GET['Brand']) && isset($_GET['Mobile_Name']) && isset($_GET['Version']) && isset($_GET['Screen_Size'])) 
	{
	 
		// receiving the post params
		$UserName = $_GET['UserName'];
		$Mobile_Serial_Number = $_GET['Mobile_Serial_Number'];
		$Start_Time = $_GET['Start_Time'];
		$End_Time = $_GET['End_Time'];
		$Brand = $_GET['Brand'];
		$Mobile_Name = $_GET['Mobile_Name'];
		$Version = $_GET['Version'];
		$Screen_Size = $_GET['Screen_Size'];
	 
		// get the user by username and password
		$user = $db->insertDataInLoginInfo($UserName, $Mobile_Serial_Number, $Start_Time, $End_Time, $Brand, $Mobile_Name, $Version, $Screen_Size);
	 
		if ($user != false) 
		{
			$response["successMessage"] = "New record created successfully";
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
		$response["error_msg"] = "Required parameters are missing!";
		echo json_encode($response);
	}
?>