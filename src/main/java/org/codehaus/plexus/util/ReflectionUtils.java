package org.codehaus.plexus.util;

import java.lang.reflect.Field;

/**
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 *
 * @version $Id$
 */
public class ReflectionUtils
{
    public static Field getFieldByNameIncludingSuperclasses( String fieldName, Class clazz  )
    {
        Field retValue = null;

        try
        {
            retValue = clazz.getDeclaredField( fieldName );
        }
        catch ( NoSuchFieldException e )
        {
            Class superclass = clazz.getSuperclass();

            if ( !Object.class.equals(  superclass ) )
            {
                retValue = getFieldByNameIncludingSuperclasses( fieldName, superclass );
            }
        }

        return retValue;
    }
}
