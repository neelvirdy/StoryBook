package com.storybook;

import java.net.UnknownHostException;
import java.util.Calendar;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class HPStoryBookMongoLib {

	private MongoClient mongo = null;
	private boolean auth;
	private DB db = null;
	private DBCollection collection;

	public boolean initConnection(String ip, int port) {

		try {
			mongo = new MongoClient(ip, port);
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public boolean initDatabase(String name, String username, String password) {
		db = mongo.getDB(name);
		auth = db.authenticate(username, password.toCharArray());
		return auth;
	}

	public void initCollection(String name) {
		if (auth) {
			collection = db.getCollection(name);
		}
	}

	public void createNewUser(String facebookID, String name) {
		Calendar created = Calendar.getInstance();
		System.out.println(Calendar.JANUARY);
		BasicDBObject doc = new BasicDBObject("FacebookID", facebookID)
				.append("Name", name)
				.append("DateCreated",
						new BasicDBObject("Year", created.get(Calendar.YEAR)).append(
								"Month", created.get(Calendar.MONTH)+1).append("Day", created.get(Calendar.DATE)))
				.append("NumberOfAlbums", 0);
		collection.insert(doc);
	}
	
	public void createNewAlbum(){
		
	}
	
	public void createNewPicture(){
		
	}
	
}