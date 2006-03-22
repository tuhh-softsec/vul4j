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


    /**
     * Generates a map of the fields and values on a given object,
     * also pulls from superclasses
     *
     * @param object the object to generate the list of fields from
     * @return map containing the fields and their values
     */
    public static Object getValueIncludingSuperclasses( String variable, Object object ) throws IllegalArgumentException
    {
        try
        {
            Class clazz = object.getClass();

            Field field = clazz.getDeclaredField(variable);

            field.setAccessible( true );

            return field.get( object );
        }
        catch ( NoSuchFieldException nsfe )
        {
            throw new IllegalArgumentException( variable + " does not exist on " + object.getClass().getName() );
        }
        catch ( IllegalAccessException iae )
        {
            throw new IllegalArgumentException( variable + " could not be accessed on " + object.getClass().getName() );
        }
    }

    /**
     * Generates a map of the fields and values on a given object,
     * also pulls from superclasses
     *
     * @param object the object to generate the list of fields from
     * @return map containing the fields and their values
     */
    public static Map getVariablesAndValuesIncludingSuperclasses( Object object )
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
    {

        Class clazz = object.getClass();

        Field[] fields = clazz.getDeclaredFields();

        AccessibleObject.setAccessible( fields, true);

        for (int i = 0; i < fields.length; ++i)
        {
            Field field = fields[i];
            try
            {
                map.put( field.getName(), field.get( object ) );
            }
            catch (IllegalAccessException iae) {
                map.put( field.getName(), null );
            }

        }

        Class superclass = clazz.getSuperclass();

        if ( !Object.class.equals(  superclass ) )
        {
            gatherVariablesAndValuesIncludingSuperclasses( superclass, map );
        }
    }


}
