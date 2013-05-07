package nl.googlethursday.projectbackoffice.helper;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;


import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
@Startup
public class ConfigService {
	Properties properties = new Properties();
	Logger logger = LoggerFactory.getLogger(ConfigService.class);
	
	public ConfigService() {
		super();
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void startConfig() throws Exception{
		logger.debug("opstarten config-spul");
		System.out.println("opstarten config-spul");
		
		try {
			
		
		//	String props = "D://java//GIT//googlethursday//src//main//resources//props//rodofumi.properties"; 
			String props = Thread.currentThread().getContextClassLoader().getResource("props").getPath() + File.separator + "rodofumi.properties";
		
			properties.load(new FileInputStream(props));
		
			logger.debug("end config-spul, PORT = " + getProperty("PORT"));
			System.out.println("end config-spul, PORT = " + getProperty("PORT"));
		} 
		catch (Exception e) {
			logger.debug("handmatig zetten van properties");
			properties.setProperty("PORT", "27017");
			properties.setProperty("IP", "localhost");
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
