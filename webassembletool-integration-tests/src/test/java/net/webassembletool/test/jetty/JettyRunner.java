package net.webassembletool.test.jetty;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.deployer.ContextDeployer;
import org.mortbay.jetty.deployer.WebAppDeployer;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.thread.QueuedThreadPool;

/**
 * This is a TestCase class that must be subclassed to create WebApp tests. Tests are guarenteed to have a running Server (Jetty) with specified webapps on specified port during session. This can be
 * used in two ways : 1) subclassing it, in which case, jetty is started and all tests are run 2) using it as a wrapper test that is : <code>
 * TestCase myTestCase = XXXXXXX;
 * ....
 * new WebAppTestCase("path/to/webapps",myTestCase).run(); //myTestCase will be run after jetty is started
 * </code> wat.jetty.webapproot wat.jetty.home wat.jetty.webapp.classpath
 * 
 * @author Omar BENHAMID
 */
public class JettyRunner {
    private static final Log log = LogFactory.getLog(JettyRunner.class);
    private static Server server = null;
    private static int serverPort = 8080;
    private static String webappsRoot = null;

    private JettyRunner() {
        // Do not use
    }

    /**
     * Starts the jettyEngine
     * 
     * @throws Exception
     *             If a problem occurs
     */
    public final static void startJetty() throws Exception {
        if (JettyRunner.webappsRoot == null) {
            URL url = JettyRunner.class.getResource("/");
            JettyRunner.webappsRoot = url.getPath() + ".." + File.separatorChar + "webapps" + File.separatorChar;
        }
        JettyRunner.log.info("Starting jetty");
        JettyRunner.server = new Server(JettyRunner.serverPort);
        JettyRunner.server.setThreadPool(new QueuedThreadPool());
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        handlers.addHandler(contexts);
        handlers.addHandler(new DefaultHandler());
        JettyRunner.server.setHandler(handlers);
        WebAppDeployer webAppDeployer = new WebAppDeployer();
        webAppDeployer.setContexts(contexts);
        webAppDeployer.setWebAppDir(JettyRunner.webappsRoot);
        JettyRunner.server.addLifeCycle(webAppDeployer);
        JettyRunner.server.start();
    }

    /**
     * Stops running jetty engine.
     * 
     * @throws Exception
     *             If a problem occurs
     */
    public final static void stopJetty() throws Exception {
        JettyRunner.log.info("Stopping jetty");
        JettyRunner.server.stop();
        JettyRunner.server = null;
    }

    /**
     * Run jetty as if it were in test
     * 
     * @param args
     *            command line arguments : 1 unique argument : path to webapps.
     * @throws Exception
     *             If a problem occurs
     */
    public final static void main(String[] args) throws Exception {
        if (args.length != 2)
            throw new Exception("Needs 2 argument : webapps root and server port");
        File webappsRootFolder = new File(args[0]);
        if (!webappsRootFolder.isDirectory())
            throw new Exception(args[0] + " is not a valid directory");
        JettyRunner.webappsRoot = webappsRootFolder.getAbsolutePath();
        JettyRunner.serverPort = Integer.parseInt(args[1]);
        LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in));
        boolean shutdown = false;
        do {
            JettyRunner.startJetty();
            System.out.println("Jetty started serving webapps in " + JettyRunner.webappsRoot);
            while (true) {
                System.out.println("Type q[uit] to shutdown server, r[estart] to restart it :");
                String ln = in.readLine().toLowerCase().trim();
                if (ln.length() == 0)
                    continue;
                if ("quit".startsWith(ln)) {
                    shutdown = true;
                    break;
                }
                if ("restart".startsWith(ln))
                    break;
                System.err.println("I don't understand !");
            }
            JettyRunner.stopJetty();
            Thread.sleep(500);
        } while (!shutdown);
        System.out.println("Jetty shutdown.");
    }
}