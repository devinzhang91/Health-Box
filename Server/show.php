<?php
	class MyDB extends SQLite3
	{
	  function __construct()
	   {
		$this->open('test.db');
	    }
	}
	$db = new MyDB();
	if(!$db){
		echo"�������ݿ�ʧ��";
	} 
	//������е�����
	$result=$db->query('SELECT*FROM test order by t desc LIMIT 10');
	while($row=$result->fetchArray(SQLITE3_ASSOC)){
	$reply=array("time"=>$row['t'],"content"=>$row['c']);
	$data=json_encode($reply);
	echo $data;
	}
	$db->close();
?>
