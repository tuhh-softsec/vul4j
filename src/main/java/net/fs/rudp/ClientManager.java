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

package net.fs.rudp;

import net.fs.utils.MLog;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;


public class ClientManager {
	
	HashMap<Integer, ClientControl> clientTable=new HashMap<Integer, ClientControl>();
	
	Thread mainThread;
	
	Route route;
	
	int receivePingTimeout=8*1000;
	
	int sendPingInterval=1*1000;
	
	Object syn_clientTable=new Object();
	
	ClientManager(Route route){
		this.route=route;
		mainThread=new Thread(){
			@Override
			public void run(){
				while(true){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					scanClientControl();
				}
			}
		};
		mainThread.start();
	}
	
	void scanClientControl(){
		Iterator<Integer> it=getClientTableIterator();
		long current=System.currentTimeMillis();
		//MLog.println("ffffffffffff "+clientTable.size());
		while(it.hasNext()){
			ClientControl cc=clientTable.get(it.next());
			if(cc!=null){
				if(current-cc.getLastReceivePingTime()<receivePingTimeout){
					if(current-cc.getLastSendPingTime()>sendPingInterval){
						cc.sendPingMessage();
					}
				}else {
					//超时关闭client
					MLog.println("超时关闭client "+cc.dstIp.getHostAddress()+":"+cc.dstPort+" "+new Date());
//					System.exit(0);
					synchronized (syn_clientTable) {
						cc.close();
					}
				}
			}
		}
	}
	
	void removeClient(int clientId){
		clientTable.remove(clientId);
	}
	
	Iterator<Integer> getClientTableIterator(){
		Iterator<Integer> it=null;
		synchronized (syn_clientTable) {
			it=new CopiedIterator(clientTable.keySet().iterator());
		}
		return it;
	}
	
	ClientControl getClientControl(int clientId,InetAddress dstIp,int dstPort){
		ClientControl c=clientTable.get(clientId);
		if(c==null){
			c=new ClientControl(route,clientId,dstIp,dstPort);
			synchronized (syn_clientTable) {
				clientTable.put(clientId, c);
			}
		}
		return c;
	}
	
}
