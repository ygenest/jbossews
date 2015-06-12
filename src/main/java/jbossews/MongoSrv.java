package jbossews;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoSrv {
	String database = "ccdb";
	String collection = "symlst";
	MongoClient mongoClient=null;

	public static void main(String[] args) throws UnknownHostException {
		MongoSrv srv = new MongoSrv();
		//String[] strings = { "a", "b", "c" };
		//srv.addData("canOpt2", strings);
		List<String> res = srv.readGroup();
		for (String r:res) System.out.println("elelm="+r);
		//srv.readData("canOpt");

	}

//	public MongoSrv() {
//		 String userName="admin";
//		 //char [] password={'J','3','E','7','B','k','h','d','s','z','C','z'};
//		 char [] password={'a'};
//		 //MongoCredential credential = MongoCredential.createCredential(userName, database, password);
//		 try {
//			//mongoClient = new MongoClient(new ServerAddress(),Arrays.asList(credential));
//			mongoClient = new MongoClient();
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public String addData(String grName, String[] lst) {
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB db = mongoClient.getDB(database);
		DBCollection coll = db.getCollection(collection);
		BasicDBObject symlst = new BasicDBObject();
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("name", grName);
		DBCursor cursor = coll.find(whereQuery);
		if (cursor.hasNext()) {
			return "This object already exists";
			
		}

		else {
			symlst.put("name", grName);
			symlst.put("symbols", lst);
			coll.insert(symlst);
			return null;
		}
	}
	
	public String deleteGroup(String grName) {
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DBObject res;
		List<String> rs2=null;

		DB db = mongoClient.getDB(database);	
		DBCollection coll = db.getCollection(collection);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("name", grName);
		DBCursor cursor = coll.find(whereQuery);
		if (cursor.hasNext()) {
			res=cursor.next();
			coll.remove(res);
			return null;
		} else
		{
			return "This objectdoes not exists";
		}

	}
	
	public List<String> readGroup() {
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> res=new ArrayList<String>();

		DB db = mongoClient.getDB(database);
		DBCollection coll = db.getCollection(collection);
		DBCursor cursor = coll.find();
		while (cursor.hasNext()) {
			res.add((String) cursor.next().get("name"));
		}
		return res;
	}
	
	public List<String> readData(String grName) {
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DBObject res;
		List<String> rs2=null;
		DB db = mongoClient.getDB(database);	
		DBCollection coll = db.getCollection(collection);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("name", grName);
		DBCursor cursor = coll.find(whereQuery);
		if (cursor.hasNext()) {
			res=cursor.next();
			rs2 = (List<String>) res.get("symbols");	
		}
		return rs2;
	}
}
