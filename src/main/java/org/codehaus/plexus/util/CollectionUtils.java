package org.codehaus.plexus.util;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @version $Id$
 */
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
    public static <K,V> Map<K,V> mergeMaps( Map<K,V> dominantMap, Map<K,V> recessiveMap )
    {

        if ( dominantMap == null && recessiveMap == null )
        {
            return null;
        }

        if ( dominantMap != null && recessiveMap == null )
        {
            return dominantMap;
        }

        if ( dominantMap == null)
        {
            return recessiveMap;
        }

        Map<K,V> result = new HashMap<K,V>();

        // Grab the keys from the dominant and recessive maps.
        Set<K> dominantMapKeys = dominantMap.keySet();
        Set<K> recessiveMapKeys = recessiveMap.keySet();

        // Create the set of keys that will be contributed by the
        // recessive Map by subtracting the intersection of keys
        // from the recessive Map's keys.
        Collection<K> contributingRecessiveKeys =
            CollectionUtils.subtract( recessiveMapKeys,
                                      CollectionUtils.intersection( dominantMapKeys, recessiveMapKeys ) );

        result.putAll( dominantMap );

        // Now take the keys we just found and extract the values from
        // the recessiveMap and put the key:value pairs into the dominantMap.
        for ( K key : contributingRecessiveKeys )
        {
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
    public static <K,V> Map<K,V> mergeMaps( Map<K,V>[] maps )
    {
        Map<K,V> result;

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
     * @param a The first collection
     * @param b The second collection
     * @see Collection#retainAll
     * @return  The intersection of a and b, never null
     */
    public static <E> Collection<E> intersection( final Collection<E> a, final Collection<E> b )
    {
        ArrayList<E> list = new ArrayList<E>();
        Map<E, Integer> mapa = getCardinalityMap( a );
        Map<E, Integer> mapb = getCardinalityMap( b );
        Set<E> elts = new HashSet<E>( a );
        elts.addAll( b );
        for ( E obj : elts )
        {
            for ( int i = 0, m = Math.min( getFreq( obj, mapa ), getFreq( obj, mapb ) ); i < m; i++ )
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
     * @param a The start collection
     * @param b The collection that will be subtracted
     * @see Collection#removeAll
     * @return The result of the subtraction
     */
    public static <T> Collection<T> subtract( final Collection<T> a, final Collection<T> b )
    {
        ArrayList<T> list = new ArrayList<T>( a );
        for ( T aB : b )
        {
            list.remove( aB );
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
     * @param col The collection to count cardinalities for
     * @return A map of counts, indexed on each element in the collection
     */
    public static <E> Map<E, Integer> getCardinalityMap( final Collection<E> col )
    {
        HashMap<E, Integer> count = new HashMap<E, Integer>();
        for ( E obj : col )
        {
            Integer c = count.get( obj );
            if ( null == c )
            {
                count.put( obj, 1 );
            }
            else
            {
                count.put( obj, c + 1 );
            }
        }
        return count;
    }

    public static <E> List<E> iteratorToList( Iterator<E> it )
    {
        if ( it == null )
        {
            throw new NullPointerException( "it cannot be null." );
        }

        List<E> list = new ArrayList<E>();

        while ( it.hasNext() )
        {
            list.add( it.next() );
        }

        return list;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private static <E> int getFreq( final E obj, final Map<E, Integer> freqMap )
    {
        try
        {
            Integer o = freqMap.get( obj );
            if ( o != null )  // minimize NullPointerExceptions
            {
                return o;
            }
        }
        catch ( NullPointerException ignore )
        {
        }
        catch ( NoSuchElementException ignore )
        {
        }
        return 0;
    }
}
