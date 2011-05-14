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
package org.apache.commons.digester3.binder;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import org.apache.commons.digester3.RuleSet;

/**
 * The Digester EDSL.
 *
 * @since 3.0
 */
public final class RulesBinder
{

    /**
     * The default head when reporting an errors list.
     */
    private static final String HEADING = "Digester creation errors:%n%n";

    /**
     * Errors that can occur during binding time or rules creation.
     */
    private final List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

    /**
     * 
     */
    private final FromBinderRuleSet fromBinderRuleSet = new FromBinderRuleSet();

    /**
     * 
     */
    private ClassLoader classLoader;

    /**
     * 
     *
     * @param classLoader
     */
    void initialize( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
        fromBinderRuleSet.clear();
        errors.clear();
    }

    /**
     * 
     *
     * @return
     */
    public ClassLoader getContextClassLoader()
    {
        return this.classLoader;
    }

    /**
     * Records an error message which will be presented to the user at a later time. Unlike throwing an exception, this
     * enable us to continue configuring the Digester and discover more errors. Uses
     * {@link String#format(String, Object[])} to insert the arguments into the message.
     *
     * @param messagePattern The message string pattern
     * @param arguments Arguments referenced by the format specifiers in the format string
     */
    public void addError( String messagePattern, Object... arguments )
    {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        StackTraceElement element = null;

        int stackIndex = stackTrace.length - 1;
        while ( element == null && stackIndex > 0 ) // O(n) there's no better way
        {
            Class<?> moduleClass = null;
            try
            {
                // check if the set ClassLoader resolves the Class in the StackTrace
                moduleClass = Class.forName( stackTrace[stackIndex].getClassName(), false, this.classLoader );
            }
            catch ( ClassNotFoundException e )
            {
                try
                {
                    // try otherwise with current ClassLoader
                    moduleClass =
                        Class.forName( stackTrace[stackIndex].getClassName(), false, this.getClass().getClassLoader() );
                }
                catch ( ClassNotFoundException e1 )
                {
                    // Class in the StackTrace can't be found, don't write the file name:line number detail in the
                    // message
                }
            }

            if ( moduleClass != null )
            {
                if ( RulesModule.class.isAssignableFrom( moduleClass ) )
                {
                    element = stackTrace[stackIndex];
                }
            }

            stackIndex--;
        }

        if ( element != null )
        {
            messagePattern = format( "%s (%s:%s)", messagePattern, element.getFileName(), element.getLineNumber() );
        }
        addError( new ErrorMessage( messagePattern, arguments ) );
    }

    /**
     * Records an exception, the full details of which will be logged, and the message of which will be presented to the
     * user at a later time. If your Module calls something that you worry may fail, you should catch the exception and
     * pass it into this.
     *
     * @param t The exception has to be recorded.
     */
    public void addError( Throwable t )
    {
        String message = "An exception was caught and reported. Message: " + t.getMessage();
        addError( new ErrorMessage( message, t ) );
    }

    /**
     * 
     *
     * @param errorMessage
     */
    private void addError(ErrorMessage errorMessage) {
        this.errors.add(errorMessage);
    }

    /**
     * Allows sub-modules inclusion while binding rules.
     *
     * @param rulesModule the sub-module has to be included.
     */
    public void install( RulesModule rulesModule )
    {
        rulesModule.configure( this );
    }

    /**
     * Allows to associate the given pattern to one or more Digester rules.
     *
     * @param pattern The pattern that this rule should match
     * @return The Digester rules builder
     */
    public LinkedRuleBuilder forPattern( String pattern )
    {
        final String keyPattern;

        if ( pattern == null || pattern.length() == 0 )
        {
            addError( "Null or empty pattern is not valid" );
            keyPattern = null;
        }
        else
        {
            if ( pattern.endsWith( "/" ) )
            {
                // to help users who accidently add '/' to the end of their patterns
                keyPattern = pattern.substring( 0, pattern.length() - 1 );
            }
            else
            {
                keyPattern = pattern;
            }
        }

        return new LinkedRuleBuilder( this, fromBinderRuleSet, classLoader, keyPattern );
    }

    /**
     * 
     * @return
     */
    RuleSet buildRuleSet()
    {
        if ( !this.errors.isEmpty() )
        {
            Formatter fmt = new Formatter().format( HEADING );
            int index = 1;

            for ( ErrorMessage errorMessage : this.errors )
            {
                fmt.format( "%s) %s%n", index++, errorMessage.getMessage() );

                Throwable cause = errorMessage.getCause();
                if ( cause != null )
                {
                    StringWriter writer = new StringWriter();
                    cause.printStackTrace( new PrintWriter( writer ) );
                    fmt.format( "Caused by: %s", writer.getBuffer() );
                }

                fmt.format( "%n" );
            }

            if ( this.errors.size() == 1 )
            {
                fmt.format( "1 error" );
            }
            else
            {
                fmt.format( "%s errors", this.errors.size() );
            }

            throw new DigesterLoadingException( fmt.toString() );
        }

        return fromBinderRuleSet;
    }

}
