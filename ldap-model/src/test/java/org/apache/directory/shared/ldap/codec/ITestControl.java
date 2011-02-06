package org.apache.directory.shared.ldap.codec;


import org.apache.directory.shared.ldap.model.message.Control;


interface ITestControl extends Control
{

    public static final String OID = "1.2.3.4.5";


    public abstract int getFoo();


    public abstract void setFoo( int foo );

}