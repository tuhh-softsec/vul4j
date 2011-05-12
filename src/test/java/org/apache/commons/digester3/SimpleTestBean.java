/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.digester3;

/**
 * <p>
 * As it's name suggests just a simple bean used for testing.
 */
public class SimpleTestBean
{

    private String alpha;

    private String beta;

    private String gamma;

    private String delta;

    public String getAlpha()
    {
        return alpha;
    }

    public void setAlpha( String alpha )
    {
        this.alpha = alpha;
    }

    public String getBeta()
    {
        return beta;
    }

    public void setBeta( String beta )
    {
        this.beta = beta;
    }

    public String getGamma()
    {
        return gamma;
    }

    public void setGamma( String gamma )
    {
        this.gamma = gamma;
    }

    public String getDeltaValue()
    { // Retrieves "write only" value
        return delta;
    }

    public void setDelta( String delta )
    { // "delta" is a write-only property
        this.delta = delta;
    }

    public void setAlphaBeta( String alpha, String beta )
    {
        setAlpha( alpha );
        setBeta( beta );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder( "[SimpleTestBean]" );
        sb.append( " alpha=" );
        sb.append( alpha );
        sb.append( " beta=" );
        sb.append( beta );
        sb.append( " gamma=" );
        sb.append( gamma );
        sb.append( " delta=" );
        sb.append( delta );

        return sb.toString();
    }
}
