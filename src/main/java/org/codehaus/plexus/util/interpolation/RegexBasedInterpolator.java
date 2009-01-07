package org.codehaus.plexus.util.interpolation;

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

import java.util.List;

/**
 *
 * @version $Id$
 * @deprecated Use plexus-interpolation APIs instead.
 */
public class RegexBasedInterpolator
    extends org.codehaus.plexus.interpolation.RegexBasedInterpolator
    implements Interpolator
{
    public RegexBasedInterpolator()
    {
        super();
    }

    public RegexBasedInterpolator( List valueSources )
    {
        super( valueSources );
    }

    public RegexBasedInterpolator( String startRegex,
                                   String endRegex,
                                   List valueSources )
    {
        super( startRegex, endRegex, valueSources );
    }

    public RegexBasedInterpolator( String startRegex,
                                   String endRegex )
    {
        super( startRegex, endRegex );
    }

    public void addValueSource( ValueSource valueSource )
    {
        super.addValueSource( valueSource );
    }

    public void removeValuesSource( ValueSource valueSource )
    {
        super.removeValuesSource( valueSource );
    }
}
