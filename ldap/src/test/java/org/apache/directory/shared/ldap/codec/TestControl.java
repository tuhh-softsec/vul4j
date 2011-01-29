package org.apache.directory.shared.ldap.codec;


class TestControl implements ITestControl
{
    int foo;
    
    
    /**
     * {@inheritDoc}
     */
    public int getFoo()
    {
        return foo;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void setFoo( int foo )
    {
        this.foo = foo;
    }
    
    public String getOid()
    {
        return OID;
    }

    public boolean isCritical()
    {
        return true;
    }

    public void setCritical( boolean isCritical )
    {
    }
}