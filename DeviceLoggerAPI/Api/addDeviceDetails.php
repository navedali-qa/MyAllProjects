<?php
	require_once '../include/DB_Functions.php';
	$db = new DB_Functions();
	
	$response = array();

	if (isset($_GET['Device_Type']) && isset($_GET['Mobile_Name']) && isset($_GET['Brand']) && isset($_GET['Mobile_Serial_Number']) && isset($_GET['Version']) && isset($_GET['Screen_Size']) && isset($_GET['Project'])) 
	{
		$Device_Type = $_GET['Device_Type'];
		$Mobile_Name = $_GET['Mobile_Name'];
		$Brand = $_GET['Brand'];
		$Mobile_Serial_Number = $_GET['Mobile_Serial_Number'];
		$Version = $_GET['Version'];		
		$Screen_Size = $_GET['Screen_Size'];
		$Project = $_GET['Project'];
	 
		// get the user by username and password
		$user = $db->insertDeviceInfo($Device_Type, $Mobile_Name, $Brand, $Mobile_Serial_Number, $Version, $Screen_Size, $Project);
	 
		if ($user != false) 
		{
			if(strpos($user,"Device Already Added") !== false)
			{
				$response["successMessage"] = "Device Already Added";
			}
			else
			{
				$response["successMessage"] = "New device added successfully";
			}
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