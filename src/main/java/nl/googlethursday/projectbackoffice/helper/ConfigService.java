package nl.googlethursday.projectbackoffice.helper;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;


@Singleton
public class ConfigService {
	Properties properties = new Properties();

	@PostConstruct
	public void startConfig() throws Exception{
		System.out.println("opstarten config-spul");
		//String props = "D://java//GIT//googlethursday//src//main//resources//props//rodofumi.properties"; 
		String props = Thread.currentThread().getContextClassLoader().getResource("props").getPath() + File.separator + "rodofumi.properties";
		properties.load(new FileInputStream(props));
		System.out.println("end config-spul, PORT = " + getProperty("PORT"));
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
