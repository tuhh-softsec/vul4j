package org.apache.commons.digester3.substitution;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.digester3.Substitutor;
import org.xml.sax.Attributes;

/**
 * This Substitutor chains two Substitutors <code>a</code> and <code>b</code>.
 * All values to substitute are first handled by <code>a</code> and passed to
 * <code>b</code> afterwards.
 */
public class CompoundSubstitutor
    extends Substitutor
{

    /**
     * Substitutor a
     */
    private final Substitutor a;

    /**
     * Substitutor b
     */
    private final Substitutor b;

    /**
     * Creates a new CompoundSubstitutor instance. All values overgiven to <code>substitute()</code>
     * are first handled by <code>a</code> and passed to <code>b</code> afterwards.
     * Both Substitutor have to be not null.
     *
     * @param a Substitutor a
     * @param b Substitutor b
     */
    public CompoundSubstitutor( Substitutor a, Substitutor b )
    {
        if ( a == null )
        {
            throw new IllegalArgumentException( "First Substitutor must be not null" );
        }
        if ( b == null )
        {
            throw new IllegalArgumentException( "Second Substitutor must be not null" );
        }
        this.a = a;
        this.b = b;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attributes substitute( Attributes attributes )
    {
        return b.substitute( a.substitute( attributes ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String substitute( String bodyText )
    {
        return b.substitute( a.substitute( bodyText ) );
    }

}
