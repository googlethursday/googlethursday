package nl.googlethursday.projectbackoffice.interceptors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import nl.googlethursday.projectbackoffice.sessioncontext.SessionContext;

import org.apache.log4j.Logger;

/**
 * LoggingInterceptor JEE6 interceptor om alle methodes te loggen
 * 
 * @author rodo
 * 
 */
@Interceptor
public class LoggingInterceptor {


	private Logger logger = Logger.getLogger("nl.googlethursday.eventlogger");

	@AroundInvoke
	public Object logMethod(InvocationContext ic) throws Exception {

		String id = SessionContext.getCurrentId().get();
		
		//String method = SessionContext.getCurrentMethod().get();
		
		String method = ic.getMethod().getName().toString();
		
		String sleutel = SessionContext.getSleutel().get();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

		
		Calendar cal = Calendar.getInstance();
		long start = new Date().getTime();
		
		try {
			return ic.proceed();
		} finally {
			Date end = cal.getTime();
			long tijd = new Date().getTime() - start;
			System.out.println(tijd);
			logger.debug(cal.getTime() + "," + method + "," + id + "," + sleutel + "," + tijd);
		}
	}
}
