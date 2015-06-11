package jbossews;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

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
		String[] strings = { "a", "b", "c" };
		srv.addData("canOpt2", strings);

	}

	public void connect() throws UnknownHostException {
		// String userName="admin";
		// char [] password={};

		// MongoCredential credential =
		// MongoCredential.createCredential(userName, database, password);
		// MongoClient mongoClient = new MongoClient(new ServerAddress(),
		// Arrays.asList(credential));

	}

	public void addData(String grName, String[] lst) {
		MongoClient mongoClient = null;
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
			System.out.println("This object already exist " + grName);
		}

		else {
			symlst.put("name", grName);
			symlst.put("symbols", lst);
			coll.insert(symlst);
		}
	}
	
	public String [] readData(String grName) {
		MongoClient mongoClient = null;
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
		while (cursor.hasNext()) {
			
		}
		return null;
	}
}
