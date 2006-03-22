package org.codehaus.plexus.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.AccessibleObject;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @author <a href="mailto:jesse@codehaus.org">Jesse McConnell</a>
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
     * @param clazz The class to find the method in.
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


    /**
     * attempts to set the value to the variable in the object passed in
     *
     * @param object
     * @param variable
     * @param value
     * @throws IllegalAccessException
     */
    public static void setVariableValueInObject( Object object, String variable, Object value )
        throws IllegalAccessException
    {
        Field field = getFieldByNameIncludingSuperclasses( variable, object.getClass() );

        field.setAccessible( true );

        field.set(object, value );
    }


    /**
     * Generates a map of the fields and values on a given object,
     * also pulls from superclasses
     *
     * @param object the object to generate the list of fields from
     * @return map containing the fields and their values
     */
    public static Object getValueIncludingSuperclasses( String variable, Object object )
        throws IllegalAccessException
    {

        Field field = getFieldByNameIncludingSuperclasses( variable, object.getClass() );

        field.setAccessible( true );

        return field.get( object );
    }

    /**
     * Generates a map of the fields and values on a given object,
     * also pulls from superclasses
     *
     * @param object the object to generate the list of fields from
     * @return map containing the fields and their values
     */
    public static Map getVariablesAndValuesIncludingSuperclasses( Object object )
        throws IllegalAccessException
    {
        HashMap map = new HashMap ();

        gatherVariablesAndValuesIncludingSuperclasses(object, map);

        return map;
    }


    /**
     * populates a map of the fields and values on a given object,
     * also pulls from superclasses
     *
     * @param object the object to generate the list of fields from
     * @param map to populate
     */
    private static void gatherVariablesAndValuesIncludingSuperclasses( Object object, Map map )
        throws IllegalAccessException
    {

        Class clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();

        AccessibleObject.setAccessible( fields, true);

        for (int i = 0; i < fields.length; ++i)
        {
            Field field = fields[i];

            map.put( field.getName(), field.get( object ) );

        }

        Class superclass = clazz.getSuperclass();

        if ( !Object.class.equals(  superclass ) )
        {
            gatherVariablesAndValuesIncludingSuperclasses( superclass, map );
        }
    }


}
