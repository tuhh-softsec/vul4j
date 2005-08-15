package org.codehaus.plexus.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 *
 * @version $Id$
 */
public final class ReflectionUtils
{
    private ReflectionUtils()
    {
        // requirement for utility class
    }
    
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

    /**
     * Finds a setter in the given class for the given field. It searches
     * interfaces and superclasses too.
     *
     * @param fieldName the name of the field (i.e. 'fooBar'); it will
     *   search for a method named 'setFooBar'.
     * @param object The class to find the method in.
     * @return null or the method found.
     */
    public static Method getSetter( String fieldName, Class clazz )
    {
        Method [] methods = clazz.getMethods();

        fieldName = "set" + StringUtils.capitalizeFirstLetter( fieldName );

        for ( int i = 0; i < methods.length; i++ )
        {
            if ( methods[i].getName().equals( fieldName ) &&
                methods[i].getReturnType().equals( Void.TYPE ) && // FIXME: needed /required?
                !Modifier.isStatic( methods[i].getModifiers() ) &&
                methods[i].getParameterTypes().length == 1
            )
            {
                return methods[i];
            }
        }

        return null;
    }

}
