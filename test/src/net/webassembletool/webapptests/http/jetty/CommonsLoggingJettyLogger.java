package net.webassembletool.webapptests.http.jetty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.log.Logger;

/**
 * This is a jetty logger implementation that actually calls the jetty logger
 * 
 * @see org.mortbay.log.Logger
 * 
 * @author Omar BENAHMID
 */
public class CommonsLoggingJettyLogger implements Logger {
    private Log log;
    private static Log rootJettyLogger = LogFactory
	    .getLog(CommonsLoggingJettyLogger.class);

    public CommonsLoggingJettyLogger(String name) {
	log = LogFactory.getLog(name);
    }

    public CommonsLoggingJettyLogger() {
	log = rootJettyLogger;
    }

    public static Log getRootLogger() {
	return rootJettyLogger;
    }

    /* Comes from jetty !? no explanation of args ... */
    private String format(String msg, Object arg0, Object arg1) {
	int i0 = msg.indexOf("{}");
	int i1 = i0 < 0 ? -1 : msg.indexOf("{}", i0 + 2);

	if (arg1 != null && i1 >= 0)
	    msg = msg.substring(0, i1) + arg1 + msg.substring(i1 + 2);
	if (arg0 != null && i0 >= 0)
	    msg = msg.substring(0, i0) + arg0 + msg.substring(i0 + 2);
	return msg;
    }

    public void debug(String arg0, Throwable arg1) {
	log.debug(arg0, arg1);
    }

    public void debug(String arg0, Object arg1, Object arg2) {
	log.debug(format(arg0, arg1, arg2));
    }

    public Logger getLogger(String name) {
	return new CommonsLoggingJettyLogger(name);
    }

    public void info(String format, Object arg0, Object arg1) {
	log.info(format(format, arg0, arg1));

    }

    boolean debugEnabled = false;

    public boolean isDebugEnabled() {
	return debugEnabled;
    }

    public void setDebugEnabled(boolean enable) {
	debugEnabled = enable;
    }

    public void warn(String arg0, Throwable arg1) {
	log.warn(arg0, arg1);
    }

    public void warn(String format, Object arg0, Object arg1) {
	log.warn(format(format, arg0, arg1));
    }

}
