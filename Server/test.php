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
		$reply = array("state" => "0");
	} 
	// ������񣬱���Ѿ����˾Ϳ��Բ����ٽ�
	$db->exec("CREATE TABLE IF NOT EXISTS test(t varchar(32),c varchar(1024))");
	$t=$_POST['time'];
	$c=$_POST['content'];
	if($t==NULL||$c==NULL)
	{
		$reply = array("state" => "0");
	}else{
		//��������
		$insert_state=$db->exec("INSERT INTO test(t,c)VALUES('{$t}','{$c}')");
		if($insert_state){
			$reply = array("state" => "1");
		}
	}
	$db->close();
	$reply_json = json_encode($reply);
	echo $reply_json;
?>
