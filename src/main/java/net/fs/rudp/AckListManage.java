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

import java.util.HashMap;
import java.util.Iterator;

public class AckListManage implements Runnable{
	Thread mainThread;
	HashMap<Integer, AckListTask> taskTable;
	public AckListManage(){
		taskTable=new HashMap<Integer, AckListTask>();
		mainThread=new Thread(this);
		mainThread.start();
	}
	
	synchronized void addAck(ConnectionUDP conn,int sequence){
		if(!taskTable.containsKey(conn.connectId)){
			AckListTask at=new AckListTask(conn);
			taskTable.put(conn.connectId, at);
		}
		AckListTask at=taskTable.get(conn.connectId);
		at.addAck(sequence);
	}
	
	synchronized void addLastRead(ConnectionUDP conn){
		if(!taskTable.containsKey(conn.connectId)){
			AckListTask at=new AckListTask(conn);
			taskTable.put(conn.connectId, at);
		}
	}
	
	public void run(){
		while(true){
			synchronized (this){
				Iterator<Integer> it=taskTable.keySet().iterator();
				while(it.hasNext()){
					int id=it.next();
					AckListTask at=taskTable.get(id);
					at.run();
				}
				taskTable.clear();
				taskTable=null;
				taskTable=new HashMap<Integer, AckListTask>();
			}
			
			try {
				Thread.sleep(RUDPConfig.ackListDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
