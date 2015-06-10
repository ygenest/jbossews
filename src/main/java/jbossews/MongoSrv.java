package jbossews;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;



import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;


public class MongoSrv {

	public static void main(String[] args) throws UnknownHostException {
		MongoSrv srv=new MongoSrv();
		srv.connect();

	}

	public void connect() throws UnknownHostException {
		String userName="admin";
		char [] password={};
		String database="ccdb";
		//MongoCredential credential = MongoCredential.createCredential(userName, database, password);
		//MongoClient mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		MongoClient mongoClient=new MongoClient();
		DB db = mongoClient.getDB(database);
		DBCollection coll=db.getCollection("symlst");
		BasicDBObject symlst=new BasicDBObject();
		symlst.put("name", "canOpt");
		String [] lst={"aaa","bbb"};
		symlst.put("symbols", lst);
		coll.insert(symlst);
	}
	
	public void addData() {


		
	}
}
