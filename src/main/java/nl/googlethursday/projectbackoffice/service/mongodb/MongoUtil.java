package nl.googlethursday.projectbackoffice.service.mongodb;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Maak een mongodb connectie
 * @author rodo
 *
 */
public class MongoUtil {

	private static final int port = 27017;
	private static final String host = "127.10.61.129";

	public final static String USERNAME = "admin";
	public final static String PWD = "Fe7WQ2cN2wp9";
	private final static String databasenaam = "rodofumi";
	
	private static Mongo mongo = null;

	public static Mongo getMongo() {

		if (mongo == null) {
			try {
				mongo = new Mongo(host, port);
			} catch (UnknownHostException e) {
				System.out.println(e);
			} catch (MongoException e) {
				System.out.println(e);
			}
		}
		return mongo;
	}
	
	public static DB getDB(Mongo mongodb){
		DB db = mongodb.getDB(databasenaam);
		if (!db.isAuthenticated()){
			db.authenticate(USERNAME,PWD.toCharArray());
		}
		return db;
		
		
	}

}
