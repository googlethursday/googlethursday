package nl.googlethursday.projectbackoffice.interceptors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import nl.googlethursday.projectbackoffice.sessioncontext.SessionContext;

import org.apache.commons.lang.time.StopWatch;
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
	final String SPLITTER = ",";

	@AroundInvoke
	public Object logMethod(InvocationContext ic) throws Exception {

		String id = SessionContext.getCurrentId().get();

		// String method = SessionContext.getCurrentMethod().get();

		String calledMethod = ic.getMethod().getName();
		String calledClass = ic.getClass().getName();

		String sleutel = SessionContext.getSleutel().get();

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		try {
			return ic.proceed();
		} finally {
			stopWatch.stop();
			stopWatch.getTime();
			Calendar cal = Calendar.getInstance();
			Date date = cal.getTime();
			StringBuilder sb = new StringBuilder();
			sb = sb.append(dateFormat.format(date)).append(SPLITTER).append(calledClass).append(SPLITTER)
					.append(calledMethod).append(SPLITTER).append(id).append(SPLITTER).append(sleutel).append(SPLITTER)
					.append(stopWatch);
			logger.debug(sb.toString());
		}
	}
}
