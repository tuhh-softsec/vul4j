package org.apache.xml.security.utils;

import java.io.ByteArrayOutputStream;

/**
 * A simple Unsynced ByteArryOutputStream
 * @author raul
 *
 */
public class UnsyncByteArrayOutputStream extends ByteArrayOutputStream {
	int size=4*1024;
	byte []buf=new byte[size];
	int pos;
	/** @inheritDoc */
	public void write(byte[] arg0) {
		int newPos=pos+arg0.length;
		if (newPos>size) {
			expandSize();
		}
		System.arraycopy(arg0,0,buf,pos,arg0.length);
		pos=newPos;
	}
	/** @inheritDoc */
	public void write(byte[] arg0, int arg1, int arg2) {
		int newPos=pos+arg2;
		if (newPos>size) {
			expandSize();
		}
		System.arraycopy(arg0,arg1,buf,pos,arg2);
		pos=newPos;
	}
	/** @inheritDoc */
	public void write(int arg0) {		
		if (pos>=size) {
			expandSize();
		}
		buf[pos++]=(byte)arg0;		
	}
	/** @inheritDoc */
	public byte[] toByteArray() {
		byte result[]=new byte[pos];
		System.arraycopy(buf,0,result,0,pos);
		return result;
	}
	
	/** @inheritDoc */
	public void reset() {
		pos=0;
	}
	
	/** @inheritDoc */
	void expandSize() {
		int newSize=size<<2;
		byte newBuf[]=new byte[newSize];
		System.arraycopy(buf,0,newBuf,0,pos);
		buf=newBuf;
		size=newSize;
		
	}
}
