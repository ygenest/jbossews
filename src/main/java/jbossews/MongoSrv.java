package jbossews;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	String database = null;
	String collection = "symlst";
	MongoClient mongoClient=null;

	public static void main(String[] args) throws UnknownHostException {
		MongoSrv srv = new MongoSrv();
		//String[] strings = { "a", "b", "c" };
		//srv.addData("canOpt2", strings);
		List<String> res = srv.readGroup();
		//srv.readData("canOpt");

	}

	public MongoSrv() {
		ServerAddress adr=null;
		String host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
		String sport = System.getenv("OPENSHIFT_MONGODB_DB_PORT");
		int port=Integer.decode(sport);
		try {
			adr=new ServerAddress(host, port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		database = "jbossews";		
		String userName=System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
		//String userName="ygenest";
		String password=System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
		// String password="admsys";
		 MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());
		mongoClient = new MongoClient(adr,Arrays.asList(credential));
		//mongoClient = new MongoClient();
	}

	public String addData(String grName, String[] lst) {
		List<String> lst2=new ArrayList<String>();
		for (int i=0;i<lst.length;i++) {
			if (!lst[i].trim().isEmpty()) {
				lst2.add(lst[i]);
			}
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
			symlst.put("symbols", lst2);
			coll.insert(symlst);
			return null;
		}
	}
	
	public String deleteGroup(String grName) {
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
