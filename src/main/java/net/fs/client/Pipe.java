/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
				if(ClientUI.ui!=null){
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
