package org.codehaus.plexus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class CollectionUtils
{
    // ----------------------------------------------------------------------
    // Static methods that can probably be moved to a real util class.
    // ----------------------------------------------------------------------

    /**
     * Take a dominant and recessive Map and merge the key:value
     * pairs where the recessive Map may add key:value pairs to the dominant
     * Map but may not override any existing key:value pairs.
     *
     * If we have two Maps, a dominant and recessive, and
     * their respective keys are as follows:
     *
     *  dominantMapKeys = { a, b, c, d, e, f }
     * recessiveMapKeys = { a, b, c, x, y, z }
     *
     * Then the result should be the following:
     *
     * resultantKeys = { a, b, c, d, e, f, x, y, z }
     *
     * @param dominantMap Dominant Map.
     * @param recessiveMap Recessive Map.
     * @return The result map with combined dominant and recessive values.
     */
    public static Map mergeMaps( Map dominantMap, Map recessiveMap )
    {
        
        if ( dominantMap == null && recessiveMap == null )
        {
            return null;
        }

        if ( dominantMap != null && recessiveMap == null )
        {
            return dominantMap;
        }

        if ( dominantMap == null && recessiveMap != null )
        {
            return recessiveMap;        
        }
        
        Map result = new HashMap();

        // Grab the keys from the dominant and recessive maps.
        Set dominantMapKeys = dominantMap.keySet();
        Set recessiveMapKeys = recessiveMap.keySet();

        // Create the set of keys that will be contributed by the
        // recessive Map by subtracting the intersection of keys
        // from the recessive Map's keys.
        Collection contributingRecessiveKeys =
            CollectionUtils.subtract( recessiveMapKeys,
                                      CollectionUtils.intersection( dominantMapKeys, recessiveMapKeys ) );

        result.putAll( dominantMap );

        // Now take the keys we just found and extract the values from
        // the recessiveMap and put the key:value pairs into the dominantMap.
        for ( Iterator i = contributingRecessiveKeys.iterator(); i.hasNext(); )
        {
            Object key = i.next();
            result.put( key, recessiveMap.get( key ) );
        }

        return result;
    }

    /**
     * Take a series of <code>Map</code>s and merge
     * them where the ordering of the array from 0..n
     * is the dominant order.
     *
     * @param maps An array of Maps to merge.
     * @return Map The result Map produced after the merging process.
     */
    public static Map mergeMaps( Map[] maps )
    {
        Map result = null;

        if ( maps.length == 0 )
        {
            result = null;
        }
        else if ( maps.length == 1 )
        {
            result = maps[0];
        }
        else
        {
            result = mergeMaps( maps[0], maps[1] );

            for ( int i = 2; i < maps.length; i++ )
            {
                result = mergeMaps( result, maps[i] );
            }
        }

        return result;
    }

    /**
     * Returns a {@link Collection} containing the intersection
     * of the given {@link Collection}s.
     * <p>
     * The cardinality of each element in the returned {@link Collection}
     * will be equal to the minimum of the cardinality of that element
     * in the two given {@link Collection}s.
     *
     * @see Collection#retainAll
     */
    public static Collection intersection( final Collection a, final Collection b )
    {
        ArrayList list = new ArrayList();
        Map mapa = getCardinalityMap( a );
        Map mapb = getCardinalityMap( b );
        Set elts = new HashSet( a );
        elts.addAll( b );
        Iterator it = elts.iterator();
        while ( it.hasNext() )
        {
            Object obj = it.next();
            for ( int i = 0,m = Math.min( getFreq( obj, mapa ), getFreq( obj, mapb ) ); i < m; i++ )
            {
                list.add( obj );
            }
        }
        return list;
    }

    /**
     * Returns a {@link Collection} containing <tt><i>a</i> - <i>b</i></tt>.
     * The cardinality of each element <i>e</i> in the returned {@link Collection}
     * will be the cardinality of <i>e</i> in <i>a</i> minus the cardinality
     * of <i>e</i> in <i>b</i>, or zero, whichever is greater.
     *
     * @see Collection#removeAll
     */
    public static Collection subtract( final Collection a, final Collection b )
    {
        ArrayList list = new ArrayList( a );
        Iterator it = b.iterator();
        while ( it.hasNext() )
        {
            list.remove( it.next() );
        }
        return list;
    }

    /**
     * Returns a {@link Map} mapping each unique element in
     * the given {@link Collection} to an {@link Integer}
     * representing the number of occurances of that element
     * in the {@link Collection}.
     * An entry that maps to <tt>null</tt> indicates that the
     * element does not appear in the given {@link Collection}.
     */
    public static Map getCardinalityMap( final Collection col )
    {
        HashMap count = new HashMap();
        Iterator it = col.iterator();
        while ( it.hasNext() )
        {
            Object obj = it.next();
            Integer c = (Integer) ( count.get( obj ) );
            if ( null == c )
            {
                count.put( obj, new Integer( 1 ) );
            }
            else
            {
                count.put( obj, new Integer( c.intValue() + 1 ) );
            }
        }
        return count;
    }

    public static List iteratorToList( Iterator it )
    {
        if ( it == null )
        {
            throw new NullPointerException( "it cannot be null." );
        }

        List list = new ArrayList();

        while ( it.hasNext() )
        {
            list.add( it.next() );
        }

        return list;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private static final int getFreq( final Object obj, final Map freqMap )
    {
        try
        {
            Object o = freqMap.get( obj );
            if ( o != null )  // minimize NullPointerExceptions
            {
                return ( (Integer) o ).intValue();
            }
        }
        catch ( NullPointerException e )
        {
            // ignored
        }
        catch ( NoSuchElementException e )
        {
            // ignored
        }
        return 0;
    }
}
