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
	 
		/**
		 * Storing new user
		 * returns user details
		 
		public function storeUser($name, $email, $password)
		{
			$uuid = uniqid('', true);
			$hash = $this->hashSSHA($password);
			$encrypted_password = $hash["encrypted"]; // encrypted password
			$salt = $hash["salt"]; // salt
	 
			$stmt = $this->conn->prepare("INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
			$stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt);
			$result = $stmt->execute();
			$stmt->close();
	 
			// check for successful store
			if ($result) {
				$stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
				$stmt->bind_param("s", $email);
				$stmt->execute();
				$user = $stmt->get_result()->fetch_assoc();
				$stmt->close();
	 
				return $user;
			} else {
				return false;
			}
		}
	 */
	 
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
			 $sql = "UPDATE login_info SET End_Time = '". $End_Time ."' WHERE Mobile_Serial_Number = ". $Mobile_Serial_Number ." AND UserName = " . $UserName. " AND End_Time =LOCKED";
			if ($this ->conn->query($sql) === TRUE)
			{
				return "Record updated successfully";
			}
			else 
			{
				return "Error: " . $sql . "<br>" . $this->conn->error;
			}
		 }
	 
	  public function insertDeviceInfo($Mobile_Name, $Brand, $Mobile_Serial_Number, $Version, $Screen_Size)
		 {
			 $sql = "INSERT INTO device_details (Mobile_Name, Brand, Mobile_Serial_Number, Version, Screen_Size) VALUES ('".$Mobile_Name."', '".$Brand."', '".$Mobile_Serial_Number."', '".$Version."', '".$Screen_Size."')";
		 
				if ($this ->conn->query($sql) === TRUE)
					{
						return "New record created successfully";
					}
					else 
					{
						return "Error: " . $sql . "<br>" . $conn->error;
					}
		 }
		/**
		 * Check user is existed or not
		 */
		public function isUserExisted($email) {
			$stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");
	 
			$stmt->bind_param($email);
	 
			$stmt->execute();
	 
			$stmt->store_result();
	 
			if ($stmt->num_rows > 0) {
				// user existed 
				$stmt->close();
				return true;
			} else {
				// user not existed
				$stmt->close();
				return false;
			}
		}
	}
?>