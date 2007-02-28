package org.codehaus.plexus.util.cli;

import java.io.File;

public interface Arg
{
    void setValue( String value );

    void setLine( String line );

    void setFile( File value );

    String[] getParts();
}
