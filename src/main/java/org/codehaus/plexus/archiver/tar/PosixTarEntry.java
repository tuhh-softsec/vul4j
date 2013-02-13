package org.codehaus.plexus.archiver.tar;

/*
 * Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import java.io.File;

/**
 * @see TarEntry
 * 
 * <p/>
 * <p/>
 * The C structure for a USTar Entry's header is:
 * <pre>
 * struct posix_header
 * {
 *   char name[100];             //File name.  Null-terminated if room.
 *   char mode[8];               //Permissions as octal string.
 *   char uid[8];                //User ID as octal string.
 *   char gid[8];                //Group ID as octal string.
 *   char size[12];              //File size in bytes as octal string.
 *   char mtime[12];             //Modification time in seconds
 *                               //   from Jan 1, 1970, as octal string.
 *   char chksum[8];             //Sum of octets in header as octal string.
 *   char typeflag;              //An enum ustar_type value.
 *   char linkname[100];         //Name of link target.
 *                               //   Null-terminated if room.
 *   char magic[6];              //"ustar\0"
 *   char version[2];            //"00"
 *   char uname[32];             //User name, always null-terminated.
 *   char gname[32];             //Group name, always null-terminated.
 *   char devmajor[8];           //Device major number as octal string.
 *   char devminor[8];           //Device minor number as octal string.
 *   char prefix[155];           //Prefix to file name.
 *                               //   Null-terminated if room.
 * };
 * </pre>
 * 
 * Actually the posix_header structure is the same with an exception of padding -
 * not present in posix_header.
 *
 * @author <a href="mailto:trog@swmud.pl">Tomasz 'Trog' Welman</a>
 * @version $Revision: 7140 $ $Date: 2008-01-06 12:50:12 +0100 (Sun, 06 Jan 2008) $
 *          from org.apache.ant.tools.tar.TarEntry v1.22
 */

public class PosixTarEntry extends TarEntry
{
	/**
	 * A prefix fo the file name.
	 * Actually it's the abstract path that precedes the file name.
	 */
	protected StringBuffer prefix;

	/**
	 * A version of the ustar.
	 */
	protected StringBuffer version;
	
    /**
     * @see TarEntry
     * Also, ustar extra header fields are initialized here.
     */
	public PosixTarEntry( String name )
	{
		super(name);
		initPOSIXTar();
	}
	
    /**
     * @see TarEntry
     * Also, ustar extra header fields are initialized here.
     */
	public PosixTarEntry( File file )
	{
		super(file);
		initPOSIXTar();
	}
	
    /**
     * @see TarEntry
     * Also, ustar extra header fields are initialized here.
     */
    public PosixTarEntry( String name, byte linkFlag )
    {
        super(name,linkFlag);
		initPOSIXTar();
    }

    /**
     * Initializes the ustar extra header fields.
     */
	protected void initPOSIXTar() {
		this.magic = new StringBuffer( POSIX_TMAGIC );
		splitPath();
		this.version = new StringBuffer( POSIX_VERSION );
	}

	/**
     * Write an entry's header information to a header buffer.
     *
     * @param outbuf The tar entry header buffer to fill in.
     */
    public void writeEntryHeader( byte[] outbuf )
    {
        int offset = 0;

        offset = TarUtils.getNameBytes( this.name, outbuf, offset, NAMELEN );
        offset = TarUtils.getOctalBytes( this.mode, outbuf, offset, MODELEN );
        offset = TarUtils.getOctalBytes( this.userId, outbuf, offset, UIDLEN );
        offset = TarUtils.getOctalBytes( this.groupId, outbuf, offset, GIDLEN );
        offset = TarUtils.getLongOctalBytes( this.size, outbuf, offset, SIZELEN );
        offset = TarUtils.getLongOctalBytes( this.modTime, outbuf, offset, MODTIMELEN );

        int csOffset = offset;

        for ( int c = 0; c < CHKSUMLEN; ++c )
        {
            outbuf[ offset++ ] = (byte) ' ';
        }

        outbuf[ offset++ ] = this.linkFlag;
        offset = TarUtils.getNameBytes( this.linkName, outbuf, offset, NAMELEN );
        offset = TarUtils.getNameBytes( this.magic, outbuf, offset, POSIX_MAGICLEN );
        offset = TarUtils.getNameBytes( this.version, outbuf, offset, POSIX_VERSIONLEN );
        offset = TarUtils.getNameBytes( this.userName, outbuf, offset, UNAMELEN );
        offset = TarUtils.getNameBytes( this.groupName, outbuf, offset, GNAMELEN );
        offset = TarUtils.getOctalBytes( this.devMajor, outbuf, offset, DEVLEN );
        offset = TarUtils.getOctalBytes( this.devMinor, outbuf, offset, DEVLEN );
        offset = TarUtils.getNameBytes( this.prefix, outbuf, offset, POSIX_PREFIXLEN );

        while ( offset < outbuf.length )
        {
            outbuf[ offset++ ] = 0;
        }

        long checkSum = TarUtils.computeCheckSum( outbuf );

        TarUtils.getCheckSumOctalBytes( checkSum, outbuf, csOffset, CHKSUMLEN );
    }

    /**
     * Parse an entry's header information from a header buffer.
     *
     * @param header The tar entry header buffer to get information from.
     */
    public void parseTarHeader( byte[] header )
    {
        int offset = 0;

        this.name = TarUtils.parseName( header, offset, NAMELEN );
        offset += NAMELEN;
        this.mode = (int) TarUtils.parseOctal( header, offset, MODELEN );
        offset += MODELEN;
        this.userId = (int) TarUtils.parseOctal( header, offset, UIDLEN );
        offset += UIDLEN;
        this.groupId = (int) TarUtils.parseOctal( header, offset, GIDLEN );
        offset += GIDLEN;
        this.size = TarUtils.parseOctal( header, offset, SIZELEN );
        offset += SIZELEN;
        this.modTime = TarUtils.parseOctal( header, offset, MODTIMELEN );
        offset += MODTIMELEN;
        this.checkSum = (int) TarUtils.parseOctal( header, offset, CHKSUMLEN );
        offset += CHKSUMLEN;
        this.linkFlag = header[ offset++ ];
        this.linkName = TarUtils.parseName( header, offset, NAMELEN );
        offset += NAMELEN;
        this.magic = TarUtils.parseName( header, offset, POSIX_MAGICLEN );
        offset += POSIX_MAGICLEN;
        this.version = TarUtils.parseName( header, offset, POSIX_VERSIONLEN );
        offset += POSIX_VERSIONLEN;
        this.userName = TarUtils.parseName( header, offset, UNAMELEN );
        offset += UNAMELEN;
        this.groupName = TarUtils.parseName( header, offset, GNAMELEN );
        offset += GNAMELEN;
        this.devMajor = (int) TarUtils.parseOctal( header, offset, DEVLEN );
        offset += DEVLEN;
        this.devMinor = (int) TarUtils.parseOctal( header, offset, DEVLEN );
        offset += DEVLEN;
        this.prefix = TarUtils.parseName( header, offset, POSIX_PREFIXLEN );
    }
    
    /**
     * Splits file path into name and prefix in a optimal way, which is:
     * put as many parts of the path to the name field (for best compatibility
     * with old POSIX tar, that misses prefix field) and the rest to prefix.
     */
    private void splitPath()
    {
    	if ( this.name.length() >= NAMELEN )
    	{
	    	int firstNameIndex = 0; /* actually: first-1 */
			for ( int j = 0, i = this.name.length() - 1; i >= 0 && j < NAMELEN; i--, j++ )
			{
				if ( this.name.charAt(i) == '/' )
				{
					firstNameIndex = i;
				}
			}
			
			if (firstNameIndex > 0) {
				this.prefix = new StringBuffer( this.name.substring(0,firstNameIndex) );/* without trailing slash */
				this.name =  new StringBuffer( this.name.substring(firstNameIndex+1) ); /* without leading slash */
			}
			else {
				this.prefix = new StringBuffer( "" );
			}
    	}
		else {
			this.prefix = new StringBuffer( "" );
		}
    }
}
