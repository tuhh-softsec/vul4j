
package org.apache.directory.shared.ldap.model.message.controls;

public class ManageDsaITImpl extends BasicControl implements ManageDsaIT
{
    /**
     * Default constructor.
     */
    public ManageDsaITImpl()
    {
        super( OID );
    }


    public void setValue( byte [] value )
    {
    }


    public boolean hasValue()
    {
        return false;
    }
}