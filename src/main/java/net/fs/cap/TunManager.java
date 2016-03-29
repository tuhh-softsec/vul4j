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

package net.fs.cap;

import net.fs.rudp.CopiedIterator;
import net.fs.utils.MLog;

import java.util.HashMap;
import java.util.Iterator;

public class TunManager {
	
	HashMap<String, TCPTun> connTable=new HashMap<String, TCPTun>();
	
	static TunManager tunManager;
	
	{
		tunManager=this;
	}
	
	TCPTun defaultTcpTun;
	
	Thread scanThread;
	
	Object syn_scan=new Object();
	
	CapEnv capEnv;
	
	{
		scanThread=new Thread(){
			public void run(){
				while(true){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					 scan();
				}
			}
		};
		scanThread.start();
	}
	
	TunManager(CapEnv capEnv){
		this.capEnv=capEnv;
	}
	
	void scan(){
		Iterator<String> it=getConnTableIterator();
		while(it.hasNext()){
			String key=it.next();
			TCPTun tun=connTable.get(key);
			if(tun!=null){
				if(tun.preDataReady){
					//无数据超时
					long t=System.currentTimeMillis()-tun.lastReceiveDataTime;
					if(t>6000){
						connTable.remove(key);
						if(capEnv.client){
							defaultTcpTun=null;
							MLog.println("tcp隧道超时");
						}
					}
				}else{
					//连接中超时
					if(System.currentTimeMillis()-tun.createTime>5000){
						connTable.remove(key);
					}
				}
			}
		}
	}
	
	public void removeTun(TCPTun tun){
		connTable.remove(tun.key);
	}
	
	Iterator<String> getConnTableIterator(){
		Iterator<String> it=null;
		synchronized (syn_scan) {
			it=new CopiedIterator(connTable.keySet().iterator());
		}
		return it;
	}
	
	public static TunManager get(){
		return tunManager;
	}
	
	public TCPTun getTcpConnection_Client(String remoteAddress,short remotePort,short localPort){
		return connTable.get(remoteAddress+":"+remotePort+":"+localPort);
	}
	
	public void addConnection_Client(TCPTun conn) {
		synchronized (syn_scan) {
			String key=conn.remoteAddress.getHostAddress()+":"+conn.remotePort+":"+conn.localPort;
			//MLog.println("addConnection "+key);
			conn.setKey(key);
			connTable.put(key, conn);
		}
	}
	
	public TCPTun getTcpConnection_Server(String remoteAddress,short remotePort){
		return connTable.get(remoteAddress+":"+remotePort);
	}
	
	public void addConnection_Server(TCPTun conn) {
		synchronized (syn_scan) {
			String key=conn.remoteAddress.getHostAddress()+":"+conn.remotePort;
			//MLog.println("addConnection "+key);
			conn.setKey(key);
			connTable.put(key, conn);
		}
	}

	public TCPTun getDefaultTcpTun() {
		return defaultTcpTun;
	}

	public void setDefaultTcpTun(TCPTun defaultTcpTun) {
		this.defaultTcpTun = defaultTcpTun;
	}

}
