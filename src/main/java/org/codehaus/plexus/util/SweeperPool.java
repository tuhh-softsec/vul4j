package org.codehaus.plexus.util;

import java.util.ArrayList;


/**
 * Pools a bunch of objects . Runs a sweeper periodically to
 * keep it down to size. The objects in the pool first get disposed first.
 *
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 */
public class SweeperPool
{
    /***/
    private static final boolean DEBUG = false;

    /** Sweeps the pool periodically to trim it's size */
    private transient Sweeper sweeper;

    /** Absolute maxiumuim size of the pool.*/
    private transient int maxSize;

    /** The size the pool gets trimmed down to */
    private transient int minSize;

    /** When the sweeper runs
     * and the pool is over this size, then the pool is trimmed */
    private int triggerSize;

    /** Holds the pooled objects */
    private ArrayList pooledObjects;

    /** Flag indicating this pool is shuting down */
    private boolean shuttingDown = false;

    //private Vector used;

    /**
     *
     * <p>There are a number of settings to control how the pool operates.
     * <ul>
     *  <li><code>minSize</code> - this is the size the pool is trimmed to</li>
     *  <li><code>triggerSize</code> - this determines if the pool is trimmed when
     * the sweeper runs. If the pool size is greater or equal than this value then
     * the pool is trimmed to <code>minSize</code>.</lie>
     * <li><code>maxSize</code> - if the pool has reached this size, any objects added
     * are immediatley disposed. If the pool is this size when the sweeper runs, then
     * the pool is also trimmed to <code>minSize</code> irrespective of the triggerSize.
     * </li>
     * <li><code>sweepInterval</code> - how often the sweeper runs. Is actually the
     * time since the sweeper last finished a pass. 0 if the sweeper should not run.
     * </li>
     * </ul>
     * </p>
     *
     * <p>Any value less than 0 is automatically converted to 0</p>
     */
    public SweeperPool( int maxSize, int minSize, int intialCapacity,
                        int sweepInterval, int triggerSize )
    {
        super();
        this.maxSize = saneConvert( maxSize );
        this.minSize = saneConvert( minSize );
        this.triggerSize = saneConvert( triggerSize );
        pooledObjects = new ArrayList( intialCapacity );

        //only run a sweeper if sweep interval is positive
        if ( sweepInterval > 0 )
        {
            sweeper = new Sweeper( this, sweepInterval );
            sweeper.start();
        }
    }

    private int saneConvert( int value )
    {
        if ( value < 0 )
        {
            return 0;
        }
        else
        {
            return value;
        }
    }

    /**
     * Return the pooled object
     */
    public synchronized Object get()
    {
        if ( ( pooledObjects.size() == 0 ) || shuttingDown )
        {
            return null;
        }
        else
        {
            Object obj = pooledObjects.remove( 0 );
            objectRetrieved( obj );

            //used.add(obj);
            return obj;
        }
    }

    /**
     * Add an object to the pool
     *
     * @param obj the object to pool. Can be null.
     *
     * @return true if the object was added to the pool, false if it was disposed or null
     *
     */
    public synchronized boolean put( Object obj )
    {
        objectAdded( obj );

        if ( ( obj != null ) && ( pooledObjects.size() < maxSize )
            && ( shuttingDown == false ) )
        {
            pooledObjects.add( obj );

            return true;
        }
        else if ( obj != null )
        {
            //no longer need the object, so dispose it
            objectDisposed( obj );
        }

        return false;
    }

    /**
     * Return the number of pooled objects. This is never
     * greater than t maximuim size of the pool
     *
     * @return the number of pooled objects
     */
    public synchronized int getSize()
    {
        return pooledObjects.size();
    }

    /**
     * Dispose of this pool. Stops the sweeper and disposes each object in the pool
     *
     */
    public void dispose()
    {
        shuttingDown = true;

        if ( sweeper != null )
        {
            sweeper.stop();
            try
            {
                sweeper.join();
            }
            catch ( InterruptedException e )
            {
                System.err.println( "Unexpected execption occurred: " );
                e.printStackTrace();
            }
        }

        synchronized ( this )
        {               
            // use an array here as objects may still be being put back in the pool
            // and we don't want to throw a ConcurrentModificationException
            Object[] objects = pooledObjects.toArray();
    
            for ( int i = 0; i < objects.length; i++ )
            {
                objectDisposed( objects[i] );
            }
    
            pooledObjects.clear();
        }        
    }

    /**
     * A pool has been disposed if has been shutdown and the sweeper has completed running.
     * 
     * @return true if the pool has been disposed, false otherwise
     */
    boolean isDisposed()
    {
        if ( !shuttingDown )
        {
            return false;
        }

        // A null sweeper means one was never started.
        if ( sweeper == null )
        {
            return true;
        }

        return sweeper.hasStopped();
    }

    /**
     * Trim the pool down to min size
     *
     */
    public synchronized void trim()
    {
        if ( ( ( triggerSize > 0 ) && ( pooledObjects.size() >= triggerSize ) )
            || ( ( maxSize > 0 ) && ( pooledObjects.size() >= maxSize ) ) )
        {
            while ( pooledObjects.size() > minSize )
            {
                objectDisposed( pooledObjects.remove( 0 ) );
            }
        }
    }

    /**
     * Override this to be notified of object disposal. Called
     * after the object has been removed. Occurs when the pool
     * is trimmed.
     *
     * @param obj
     */
    public void objectDisposed( Object obj )
    {
    }

    /**
     * Override this to be notified of object addition.
     * Called before object is to be added.
     *
     * @param obj
     */
    public void objectAdded( Object obj )
    {
    }

    /**
     * Override this to be notified of object retrieval.
     * Called after object removed from the pool, but
     * before returned to the client.
     *
     * @param obj
     */
    public void objectRetrieved( Object obj )
    {
    }

    /**
     * Periodically at <code>sweepInterval</code> goes through
     * and tests if the pool should be trimmed.
     *
     * @author bert
     *
     */
    private static class Sweeper implements Runnable
    {
        private final transient SweeperPool pool;
        private transient boolean service = false;
        private final transient int sweepInterval;
        private transient Thread t = null;

        /**
         *
         */
        public Sweeper( SweeperPool pool, int sweepInterval )
        {
            super();
            this.sweepInterval = sweepInterval;
            this.pool = pool;
        }

        /**
         * Run the seeper.
         *
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            debug( "started" );

            if ( sweepInterval > 0 )
            {
                synchronized ( this )
                {
                    while ( service )
                    {
                        try
                        {
                            //wait specified number of seconds
                            //before running next sweep
                            wait( sweepInterval * 1000 );
                        }
                        catch ( InterruptedException e )
                        {
                        }
                        runSweep();
                    }
                }
            }

            debug( "stopped" );
        }

        public void start()
        {
            if ( !service )
            {
                service = true;
                t = new Thread( this );
                t.setName( "Sweeper" );
                t.start();
            }
        }

        public synchronized void stop()
        {
            service = false;
            notifyAll();
        }

        void join()
            throws InterruptedException
        {
            t.join();
        }

        boolean hasStopped()
        {
            return !service && !t.isAlive();
        }

        private final void debug( String msg )
        {
            if ( DEBUG )
            {
                System.err.println( this + ":" + msg );
            }
        }

        private void runSweep()
        {
            debug( "runningSweep. time=" + System.currentTimeMillis() );
            pool.trim();
        }
    }
}
