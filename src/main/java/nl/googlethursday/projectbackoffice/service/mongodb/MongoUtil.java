package nl.googlethursday.projectbackoffice.service.mongodb;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import nl.googlethursday.projectbackoffice.helper.ConfigService;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Maak een mongodb connectie
 * 
 * @author rodo
 * 
 */
@Singleton
@DependsOn("ConfigService")
@Startup
public class MongoUtil {

	@EJB
	ConfigService config;

	// default openshift poort & ip adres +
	private static int port;
	private static String host;
	public final static String USERNAME = "admin";
	public final static String PWD = "Fe7WQ2cN2wp9";
	private final static String databasenaam = "rodofumi";

	private static Mongo mongo = null;

	public MongoUtil() {
		super();
		System.out.println("MongoUtil constructor");
	}

	@PostConstruct
	public void setConfig() {
		port = new Integer(config.getProperty("PORT")).intValue();
		System.out.println("poort:" + port);
		host = config.getProperty("IP");
	}

	public Mongo getMongo() {

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

	public static DB getDB(Mongo mongodb) {
		DB db = mongodb.getDB(databasenaam);
		if (!db.isAuthenticated()) {
			db.authenticate(USERNAME, PWD.toCharArray());
		}
		return db;

	}

}
