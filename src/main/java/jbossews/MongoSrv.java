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

	public static void main(String[] args) throws UnknownHostException {
		MongoSrv srv = new MongoSrv();
		//String[] strings = { "a", "b", "c" };
		//srv.addData("canOpt2", strings);
		List<String> res = srv.readGroup();
		for (String r:res) System.out.println("elelm="+r);
		//srv.readData("canOpt");

	}

	public void connect() throws UnknownHostException {
		// String userName="admin";
		// char [] password={};

		// MongoCredential credential =
		// MongoCredential.createCredential(userName, database, password);
		// MongoClient mongoClient = new MongoClient(new ServerAddress(),
		// Arrays.asList(credential));

	}

	public String addData(String grName, String[] lst) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error accessing database (addData)";
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
		DBObject res;
		List<String> rs2=null;
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
		List<String> res=new ArrayList<String>();
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		DB db = mongoClient.getDB(database);
		DBCollection coll = db.getCollection(collection);
		DBCursor cursor = coll.find();
		while (cursor.hasNext()) {
			res.add((String) cursor.next().get("name"));
		}
		return res;
	}
	
	public List<String> readData(String grName) {
		DBObject res;
		List<String> rs2=null;
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
