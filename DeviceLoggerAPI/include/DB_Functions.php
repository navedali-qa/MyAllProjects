<?php 
	class DB_Functions 
	{ 
		private $conn; 
	   // constructor
		function __construct() 
		{
			require_once 'DB_Connect.php';
			// connecting to database
			$db = new Db_Connect();
			$this->conn = $db->connect();
		}
	 
		// destructor
		function __destruct() 
		{
		}
	 	 
		public function getUserByusernameAndPassword($username, $password) 
		{
			$stmt = $this->conn->prepare("SELECT * FROM users WHERE Username = ? AND Password = ?");
	 
			$stmt->bind_param("ss", $username, $password);
	 
			if ($stmt->execute()) 
			{
				$user = $stmt->get_result()->fetch_assoc();
				$stmt->close();
				return $user;
			}
			else 
			{
				return NULL;
			}
		}
		
		public function getAdminByusernameAndPassword($username, $password) 
		{
			$stmt = $this->conn->prepare("SELECT * FROM admin WHERE Admin_UserName = ? AND Admin_Password = ?");
			$stmt->bind_param("ss", $username, $password);
	 
			if ($stmt->execute()) 
			{
				$user = $stmt->get_result()->fetch_assoc();
				$stmt->close();
				return $user;
			}
			else 
			{
				return NULL;
			}
		}		
		
		public function getLogedInUser($Mobile_Serial_Number) 
		{
			$stmt = $this->conn->prepare("SELECT * FROM users WHERE UserName = ( SELECT UserName FROM login_info WHERE End_Time = 'LOCKED' AND Mobile_Serial_Number=?)");
	 
			$stmt->bind_param("s", $Mobile_Serial_Number);
	 
			if ($stmt->execute()) 
			{
				$user = $stmt->get_result()->fetch_assoc();
				$stmt->close();
				return $user;
			}
			else 
			{
				return NULL;
			}
		}
	 
		public function getProject($Mobile_Name, $Brand, $Mobile_Serial_Number, $Version, $Screen_Size)
		{
			$stmt = $this->conn->prepare("SELECT Project FROM device_details WHERE  Mobile_Name = ? AND Brand = ? AND Mobile_Serial_Number = ? AND Version = ? AND Screen_Size = ?");
			$stmt->bind_param("sssss", $Mobile_Name, $Brand, $Mobile_Serial_Number, $Version, $Screen_Size);
			if ($stmt->execute()) 
			{
				$user = $stmt->get_result()->fetch_assoc();
				$stmt->close();
				return $user;
			}
			else 
			{
				return NULL;
			}
		 }
		
		public function insertDataInLoginInfo($UserName, $Mobile_Serial_Number, $Start_Time, $End_Time, $Brand, $Mobile_Name, $Version, $Screen_Size)
		{
			$sql = "INSERT INTO Login_Info (UserName, Mobile_Serial_Number, Start_Time, End_Time, Brand, Mobile_Name, Version, Screen_Size) VALUES ('".$UserName."', '".$Mobile_Serial_Number."', '".$Start_Time."', '".$End_Time."', '".$Brand."', '".$Mobile_Name."', '".$Version."', '".$Screen_Size."')";
		
			if ($this ->conn->query($sql) === TRUE)
			{
				return "New record created successfully";
			}
			else 
			{
				return "Error: " . $sql . "<br>" . $conn->error;
			}
		}
	 
		public function updateLoginInfo($End_Time, $Mobile_Serial_Number, $UserName)
		{
			$sql = "UPDATE login_info SET End_Time = '". $End_Time ."' WHERE Mobile_Serial_Number = '". $Mobile_Serial_Number ."' AND UserName = '" . $UserName. "' AND End_Time = 'LOCKED'";
			echo "SQL : ".$sql."\n";
			
			if ($this ->conn->query($sql) === TRUE)
			{
				return "Record updated successfully";
			}
			else 
			{
				return "Error: " . $sql . "<br>" . $this->conn->error;
			}
		}
	 
		public function insertDeviceInfo($Mobile_Name, $Brand, $Mobile_Serial_Number, $Version, $Screen_Size, $Project)
		{
			$sql = "INSERT INTO device_details (Mobile_Name, Brand, Mobile_Serial_Number, Version, Screen_Size, Project) VALUES ('".$Mobile_Name."', '".$Brand."', '".$Mobile_Serial_Number."', '".$Version."', '".$Screen_Size."', '".$Project."')";
		 
			if ($this ->conn->query($sql) === TRUE)
			{
				return "New record created successfully";
			}
			else 
			{
				return "Error: " . $sql . "<br>" . $conn->error;
			}
		 }
	
		public function getRecentUsers($Mobile_Serial_Number) 
		{
			$query = "SELECT UserName, Start_Time, End_Time FROM login_info WHERE Mobile_Serial_Number = '".$Mobile_Serial_Number."' ORDER BY Login_Index DESC LIMIT 5";
			$result = mysqli_query($this->conn,$query);
			$resultSet = array();
			while(null != ($query = mysqli_fetch_assoc($result)))
			{
				$resultSet[] = $query;
			}
			return $resultSet;			
		}
		
		public function getRecentUserName($Username)
		{
			$stmt = $this->conn->prepare("SELECT FirstName, LastName FROM users WHERE  Username = ?");
			$stmt->bind_param("s", $Username);
			if ($stmt->execute()) 
			{
				$user = $stmt->get_result()->fetch_assoc();
				$stmt->close();
				return $user;
			}
			else 
			{
				return NULL;
			}
		}
	}
?>