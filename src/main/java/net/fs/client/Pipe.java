// Copyright (c) 2015 D1SM.net
package net.fs.client;

import net.fs.rudp.ConnectionUDP;
import net.fs.rudp.UDPInputStream;
import net.fs.rudp.UDPOutputStream;
import net.fs.utils.MLog;

import java.io.InputStream;
import java.io.OutputStream;

public class Pipe {


	int lastTime=-1;

	
	boolean readed=false;
	
	public Pipe p2;
	
	byte[] pv;
	
	int pvl;
	
	int readedLength;
	
	String successMessage;
	
	int dstPort=-1;

	public void pipe(InputStream is,UDPOutputStream tos,int initSpeed,final Pipe p2) throws Exception{
		
		int len=0;
		byte[] buf=new byte[100*1024];
		boolean sendeda=false;
		while((len=is.read(buf))>0){
			readed=true;
			if(!sendeda){
				sendeda=true;
			}
			tos.write(buf, 0, len);
		}
	}
	

	
	void sendSleep(long startTime,int speed,int length){
		long needTime=(long) (1000f*length/speed);
		long usedTime=System.currentTimeMillis()-startTime;
		if(usedTime<needTime){
			try {
				Thread.sleep(needTime-usedTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public void pipe(UDPInputStream tis,OutputStream os,int maxSpeed,ConnectionUDP conn) throws Exception{
		int len=0;
		byte[] buf=new byte[1000];
		boolean sended=false;
		boolean sendedb=false;
		int n=0;
		boolean msged=false;
		while((len=tis.read(buf, 0, buf.length))>0){
			readedLength+=len;
			if(!sendedb){
				pv=buf;
				pvl=len;
				sendedb=true;
			}
			if(dstPort>0){
				if(Client.ui!=null){
					if(!msged){
						msged=true;
						String msg="端口"+dstPort+"连接成功";
						MLog.println(msg);
					}
					
				}
			}
			os.write(buf, 0, len);
			if(!sended){
				sended=true;
			}
		}
	}



	public int getReadedLength() {
		return readedLength;
	}



	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}

}
