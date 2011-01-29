package org.apache.directory.shared.ldap.codec;


public class TestControlFactory implements IControlFactory<ITestControl,ITestCodecControl>
{
    public String getOid()
    {
        return ITestControl.OID;
    }

    public ITestControl newControl()
    {
        return new TestCodecControl();
    }

    public ITestCodecControl newCodecControl()
    {
        return new TestCodecControl();
    }
}