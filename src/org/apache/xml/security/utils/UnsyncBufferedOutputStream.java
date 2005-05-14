package org.apache.xml.security.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A class that buffers writte without synchronize its methods
 * @author raul
 *
 */
public class UnsyncBufferedOutputStream extends OutputStream {
	final OutputStream out;
	static final int size=8*1024;
	final byte[] buf=new byte[size];
	int pointer=0;
	/**
	 * Creates a buffered output stream without synchronization
	 * @param out the outputstream to buffer
	 */
	public UnsyncBufferedOutputStream(OutputStream out) {
		this.out=out;
	}
	
	/** @inheritDoc */
	public void write(byte[] arg0) throws IOException {
		write(arg0,0,arg0.length);
	}
	
	/** @inheritDoc */
	public void write(byte[] arg0, int arg1, int len) throws IOException {
		int newLen=pointer+len;
		if (newLen> size) {
			flushBuffer();		
			if (len>size) {
				out.write(arg0,arg1,len);
				return;
			}
			newLen=len;
		}
		System.arraycopy(arg0,arg1,buf,pointer,len);
		pointer=newLen;
	}
	
	private final void flushBuffer() throws IOException {
		if (pointer>0)
			out.write(buf,0,pointer);
		pointer=0;
		
	}
	
	/** @inheritDoc */
	public void write(int arg0) throws IOException {		
		if (pointer>= size) {
			flushBuffer();
		}
		buf[pointer++]=(byte)arg0;

	}
	
	/** @inheritDoc */	
	public void flush() throws IOException {
		flushBuffer();
		out.flush();
	}

	/** @inheritDoc */
	public void close() throws IOException {
		flush();		
	}

}
