package net.webassembletool.webapptests;

import junit.framework.TestCase;
import junit.framework.TestResult;

/**
 * Decorator for junit test cases {@link ExtensibleTestCase}
 * 
 * @author omben
 * 
 */
public interface TestCaseFeature {
	/**
	 * Called before tests
	 * 
	 * @param c
	 *            the TestCase being run
	 * @param res
	 *            the result object
	 */
	public void beforeRun(TestCase c, TestResult res);

	/**
	 * Called after tests
	 * 
	 * @param c
	 *            the TestCase being run
	 * @param res
	 *            the result object
	 */
	public void afterRun(TestCase c, TestResult res);
}
