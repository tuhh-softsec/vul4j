package org.codehaus.plexus.util;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * A thread which is registered with a ThreadRegistry and notifies it when it has completed
 * running. Collects any errors and makes it available for analysis.
 *
 * <p>Created on 1/07/2003</p>
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Revision$
 */
public abstract class AbstractTestThread implements Runnable
{
    //~ Instance fields ----------------------------------------------------------------------------
    private String name;

    public static final boolean DEBUG = true;

    private boolean isRunning = false;

    /** Error msg provided by implementing class (of why the test failed) */
    private String errorMsg = null;

    /** The registry to notify on completion */
    private TestThreadManager registry;

    /** The error thrown when running the test. Not neccesarily a test failuer as some tests
     * may test for an exception */
    private Throwable error;

    /** If the thread has been run */
    private boolean hasRun = false;

    /** Flag indicating if the test has passed. Some test might require an
     * exception so using the error to determine if the test has passed is
     * not sufficient.
     */
    private boolean passed = false;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Constructor
     *
     * <p>Remember to call <code>setThreadRegistry(ThreadRegistry)</code>
     */
    public AbstractTestThread()
    {
        super();
    }

    public AbstractTestThread( TestThreadManager registry )
    {
        super();
        setThreadRegistry( registry );
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * @return
     */
    public Throwable getError()
    {
        return error;
    }

    /**
     * Resets the test back to it's state before starting. If the test
     * is currently running this method will block until the test has
     * finished running. Subclasses should call this method if
     * overriding it.
     *
     * */
    public void reset()
    {
        //shouldn't reset until the test has finished running
        synchronized ( this )
        {
            while ( isRunning )
            {
                try
                {
                    wait();
                }
                catch ( InterruptedException e )
                {

                }
            }
            errorMsg = null;
            error = null;
            hasRun = false;
            passed = false;
        }
    }

    /**
     * Start this TestThread running. If the test is currently running then
     * this method does nothing.
     *
     */
    public final void start()
    {
        //shouldn't have multiple threads running this test at the same time
        synchronized ( this )
        {
            if ( isRunning == false )
            {
                isRunning = true;
                Thread t = new Thread( this );
                t.start();
            }
        }
    }

    /**
     * @return
     */
    public String getErrorMsg()
    {
        return errorMsg;
    }

    /**
     * @return
     */
    public boolean hasFailed()
    {
        return !passed;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean hasPassed()
    {
        return passed;
    }

    /**
     * Don't override this. Calls <code>doRun()</code>
     *
     * @see java.lang.Runnable#run()
     */
    public final void run()
    {
        if ( registry == null )
        {
            throw new IllegalArgumentException( "The ThreadRegistry is null. Ensure this is set before running this thread" );
        }
        passed = false;
        try
        {
            doRun();
        }
        catch ( Throwable t )
        {
            error = t;
        }

        registry.completed( this );
        hasRun = true;
        isRunning = false;
        //notify objects with blocked methods which are waiting
        //on this test to complete running
        synchronized ( this )
        {
            notifyAll();
        }
    }

    /**
     * Override this to run your custom test
     *
     * @throws Throwable
     */
    public abstract void doRun() throws Throwable;

    /**
     * Set the registry this thread should notify when it has completed running
     *
     * @param registry
     */
    public void setThreadRegistry( TestThreadManager registry )

    {
        this.registry = registry;
    }

    /**
     * Test if the test has run
     *
     * @return
     */
    public boolean hasRun()
    {
        return hasRun;
    }

    /**
     * @param throwable
     */
    public void setError( Throwable throwable )
    {
        error = throwable;
    }

    /**
     * @param string
     */
    public void setErrorMsg( String string )
    {
        errorMsg = string;
    }

    /**
     * @param b
     */
    public void setPassed( boolean b )
    {
        passed = b;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param string
     */
    public void setName( String string )
    {
        name = string;
    }

    private final void debug( String msg )
    {
        if ( DEBUG )
        {
            System.out.println( this + ":" + msg );
        }
    }
}
