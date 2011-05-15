package org.apache.commons.digester3.binder;

import org.apache.commons.digester3.ObjectCreateRule;

public final class ObjectCreateBuilder
    extends AbstractBackToLinkedRuleBuilder<ObjectCreateRule>
{

    private final ClassLoader classLoader;

    private Class<?> type;

    private String attributeName;

    ObjectCreateBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder, LinkedRuleBuilder mainBuilder,
                         ClassLoader classLoader )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
        this.classLoader = classLoader;
    }

    /**
     * Construct an object with the specified class name.
     *
     * @param className Java class name of the object to be created
     * @return this builder instance
     */
    public ObjectCreateBuilder ofType( String className )
    {
        if ( className == null )
        {
            reportError( "createObject().ofType( String )", "NULL Java type not allowed" );
            return this;
        }

        try
        {
            return ofType( this.classLoader.loadClass( className ) );
        }
        catch ( ClassNotFoundException e )
        {
            reportError( "createObject().ofType( String )", String.format( "class '%s' cannot be load", className ) );
            return this;
        }
    }

    /**
     * Construct an object with the specified class.
     *
     * @param type Java class of the object to be created
     * @return this builder instance
     */
    public <T> ObjectCreateBuilder ofType( Class<T> type )
    {
        if ( type == null )
        {
            reportError( "createObject().ofType( Class<?> )", "NULL Java type not allowed" );
            return this;
        }

        this.type = type;

        return this;
    }

    /**
     * Allows specify the attribute containing an override class name if it is present.
     *
     * @param attributeName The attribute containing an override class name if it is present
     * @return this builder instance
     */
    public ObjectCreateBuilder ofTypeSpecifiedByAttribute( /* @Nullable */String attributeName )
    {
        this.attributeName = attributeName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectCreateRule createRule()
    {
        return new ObjectCreateRule( attributeName, type );
    }

}
