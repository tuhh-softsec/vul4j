package net.webassembletool.webapptests;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * <p>
 * This class represents an extensible testcase. That is objects implementing
 * the {@link TestCaseFeature} interface can be added to the is object through
 * the {@link ExtensibleTestCase#addFeature(TestCaseFeature)}
 * </p>
 * <p>
 * {@link TestCaseFeature#beforeRun(TestCase, TestResult)} is executed on each
 * feature object before test is run and
 * {@link TestCaseFeature#afterRun(TestCase, TestResult)} is executed on each
 * feature after test is run. beforeRun is lanched in the feature addition order
 * and after run in reverse order.
 * </p>
 * <p>
 * Exemple: <code>
 * public class MyWebappTest extends {@link ExtensibleTestCase} {
 * 		public MyWebappTest() {
 * 			super();
 * 			addFeature(new MySQLFeature("DBNAME"));
 * 			addFeature(new JettyWebappContainerFeature("webapps/root/dir"));
 *  	}
 * }
 * 
 * </code> when MyWebappTest is run, MySQLStarterDecorator is first called, the
 * 
 * </p>
 * 
 * @author omben
 * 
 */
public class ExtensibleTestCase extends TestCase {
    protected List<TestCaseFeature> features = new LinkedList<TestCaseFeature>();

    /**
     * Adds a tcd as a decortar at the top of decorator stack
     * 
     * @param tcd the decorator instance to start.
     */
    public void addFeature(TestCaseFeature tcd) {
	features.add(tcd);
    }

    @Override
    public void run(TestResult result) {
	ListIterator<TestCaseFeature> iter = features.listIterator();
	// start feature chain
	while (iter.hasNext()) {
	    TestCaseFeature feature = iter.next();
	    feature.beforeRun(this, result);
	}

	try {
	    super.run(result);
	} finally {
	    // shut down feature chain
	    while (iter.hasPrevious()) {
		TestCaseFeature decorator = iter.previous();
		decorator.afterRun(this, result);
	    }
	}
    }
}
