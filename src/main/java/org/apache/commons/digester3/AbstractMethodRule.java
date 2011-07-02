package org.apache.commons.digester3;

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

import static java.lang.String.format;
import static org.apache.commons.beanutils.MethodUtils.invokeExactMethod;
import static org.apache.commons.beanutils.MethodUtils.invokeMethod;

import org.xml.sax.Attributes;

/**
 * Abstract implementation for {@link org.apache.commons.digester3.SetNextRule},
 * {@link org.apache.commons.digester3.SetRootRule} and {@link org.apache.commons.digester3.SetTopRule} rules.
 *
 * @since 3.0
 */
public abstract class AbstractMethodRule
    extends Rule
{

    /**
     * The method name to call on the parent object.
     */
    protected String methodName = null;

    /**
     * The Java class name of the parameter type expected by the method.
     */
    protected String paramTypeName = null;

    /**
     * The Java class name of the parameter type expected by the method.
     */
    protected Class<?> paramType;

    /**
     * Should we use exact matching. Default is no.
     */
    protected boolean useExactMatch = false;

    /**
     * Should this rule be invoked when {@link #begin(String, String, Attributes)} (true)
     * or {@link #end(String, String)} (false) methods are invoked, false by default.
     */
    protected boolean fireOnBegin = false;

    /**
     * Construct a "set next" rule with the specified method name. The method's argument type is assumed to be the class
     * of the child object.
     * 
     * @param methodName Method name of the parent method to call
     */
    public AbstractMethodRule( String methodName )
    {
        this( methodName, (String) null );
    }

    /**
     * Construct a "set next" rule with the specified method name.
     * 
     * @param methodName Method name of the parent method to call
     * @param paramType Java class of the parent method's argument (if you wish to use a primitive type, specify the
     *            corresonding Java wrapper class instead, such as <code>java.lang.Boolean</code> for a
     *            <code>boolean</code> parameter)
     */
    public AbstractMethodRule( String methodName, Class<?> paramType )
    {
        this( methodName, paramType.getName() );
        this.paramType = paramType;
    }

    /**
     * Construct a "set next" rule with the specified method name.
     * 
     * @param methodName Method name of the parent method to call
     * @param paramTypeName Java class of the parent method's argument (if you wish to use a primitive type, specify the
     *            corresonding Java wrapper class instead, such as <code>java.lang.Boolean</code> for a
     *            <code>boolean</code> parameter)
     */
    public AbstractMethodRule( String methodName, String paramTypeName )
    {
        this.methodName = methodName;
        this.paramTypeName = paramTypeName;
    }

    /**
     * <p>
     * Is exact matching being used.
     * </p>
     * <p>
     * This rule uses <code>org.apache.commons.beanutils.MethodUtils</code> to introspect the relevent objects so that
     * the right method can be called. Originally, <code>MethodUtils.invokeExactMethod</code> was used. This matches
     * methods very strictly and so may not find a matching method when one exists. This is still the behaviour when
     * exact matching is enabled.
     * </p>
     * <p>
     * When exact matching is disabled, <code>MethodUtils.invokeMethod</code> is used. This method finds more methods
     * but is less precise when there are several methods with correct signatures. So, if you want to choose an exact
     * signature you might need to enable this property.
     * </p>
     * <p>
     * The default setting is to disable exact matches.
     * </p>
     * 
     * @return true if exact matching is enabled
     * @since Digester Release 1.1.1
     */
    public boolean isExactMatch()
    {
        return useExactMatch;
    }

    /**
     * Sets this rule be invoked when {@link #begin(String, String, Attributes)} (true)
     * or {@link #end(String, String)} (false) methods are invoked, false by default.
     *
     * @param fireOnBegin flag to mark this rule be invoked when {@link #begin(String, String, Attributes)} (true)
     *                    or {@link #end(String, String)} (false) methods are invoked, false by default.
     */
    public void setFireOnBegin( boolean fireOnBegin )
    {
        this.fireOnBegin = fireOnBegin;
    }

    /**
     * Returns the flag this rule be invoked when {@link #begin(String, String, Attributes)} (true)
     * or {@link #end(String, String)} (false) methods are invoked, false by default.
     *
     * @return the flag this rule be invoked when {@link #begin(String, String, Attributes)} (true)
     * or {@link #end(String, String)} (false) methods are invoked, false by default.
     */
    public boolean isFireOnBegin()
    {
        return fireOnBegin;
    }

    /**
     * <p>
     * Set whether exact matching is enabled.
     * </p>
     * <p>
     * See {@link #isExactMatch()}.
     * </p>
     * 
     * @param useExactMatch should this rule use exact method matching
     * @since Digester Release 1.1.1
     */
    public void setExactMatch( boolean useExactMatch )
    {
        this.useExactMatch = useExactMatch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin( String namespace, String name, Attributes attributes )
        throws Exception
    {
        if ( fireOnBegin )
        {
            invoke();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end( String namespace, String name )
        throws Exception
    {
        if ( !fireOnBegin )
        {
            invoke();
        }
    }

    /**
     * Just performs the method execution.
     *
     * @throws Exception if any error occurs.
     */
    private void invoke()
        throws Exception
    {
        // Identify the objects to be used
        Object child = getChild();
        Object parent = getParent();
        if ( getDigester().getLogger().isDebugEnabled() )
        {
            if ( parent == null )
            {
                getDigester().getLogger().debug( format( "[%s]{%s} Call [NULL PARENT].%s(%s)",
                                                         getClass().getSimpleName(),
                                                         getDigester().getMatch(),
                                                         methodName,
                                                         child ) );
            }
            else
            {
                getDigester().getLogger().debug( format( "[%s]{%s} Call %s.%s(%s)",
                                                         getClass().getSimpleName(),
                                                         getDigester().getMatch(),
                                                         parent.getClass().getName(),
                                                         methodName,
                                                         child ) );
            }
        }

        // Call the specified method
        Class<?> paramTypes[] = new Class<?>[1];
        if ( paramType != null )
        {
            paramTypes[0] = getDigester().getClassLoader().loadClass( paramTypeName );
        }
        else
        {
            paramTypes[0] = child.getClass();
        }

        if ( useExactMatch )
        {
            invokeExactMethod( parent, methodName, new Object[] { child }, paramTypes );
        }
        else
        {
            invokeMethod( parent, methodName, new Object[] { child }, paramTypes );
        }
    }

    /**
     * Returns the argument object of method has to be invoked.
     *
     * @return the argument object of method has to be invoked.
     */
    protected abstract Object getChild();

    /**
     * Returns the target object of method has to be invoked.
     *
     * @return the target object of method has to be invoked.
     */
    protected abstract Object getParent();

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString()
    {
        return format( "%s[methodName=%s, paramType=%s, paramTypeName=%s, useExactMatch=%s, fireOnBegin=%s]",
                       getClass().getSimpleName(), methodName, paramType, paramTypeName, useExactMatch, fireOnBegin );
    }

}
