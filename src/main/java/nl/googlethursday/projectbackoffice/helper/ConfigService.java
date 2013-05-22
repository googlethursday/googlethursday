package nl.googlethursday.projectbackoffice.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;


import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;





@Singleton
@Startup
public class ConfigService {
	Properties properties = new Properties();
	
	Logger logger = Logger.getLogger(ConfigService.class);
	
	public ConfigService() {
		super();
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void startConfig() throws Exception{
		//logger.debug("opstarten config-spul");
		//System.out.println("opstarten config-spul");
		
		try {
			
		
		//	String props = "D://java//GIT//googlethursday//src//main//resources//props//rodofumi.properties"; 
			String props = Thread.currentThread().getContextClassLoader().getResource("props").getPath() + File.separator + "rodofumi.properties";
		
			properties.load(new FileInputStream(props));
		
			//logger.debug("end config-spul, PORT = " + getProperty("PORT"));
			//System.out.println("end config-spul, PORT = " + getProperty("PORT"));
		} 
		catch (FileNotFoundException e) {
			//logger.debug("handmatig zetten van properties");
			//System.out.println("handmatig zetten van properties");
			properties.setProperty("PORT", "27017");
			properties.setProperty("IP", "127.10.61.129");
		}
	}
	
	public String getProperty(String property){
//		System.out.println("getProperty " + property);
//		if (properties.size() == 0){ 
//			try {
//				startConfig();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	
		return this.properties.getProperty(property);
	}
}
