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

import java.util.concurrent.LinkedBlockingQueue;


public class ResendManage implements Runnable{
	
	boolean haveTask=false;
	Object signalOb=new Object();
	Thread mainThread;
	long vTime=0;
	long lastReSendTime;
	
	LinkedBlockingQueue<ResendItem> taskList=new LinkedBlockingQueue<ResendItem>();
	
	public ResendManage(){
		Route.es.execute(this);
	}
	
	public void addTask(final ConnectionUDP conn,final int sequence){
		ResendItem ri=new ResendItem(conn, sequence);
		ri.setResendTime(getNewResendTime(conn));
		taskList.add(ri);
	}
	
	long getNewResendTime(ConnectionUDP conn){
		int delayAdd=conn.clientControl.pingDelay+(int) ((float)conn.clientControl.pingDelay*RUDPConfig.reSendDelay);
		if(delayAdd<RUDPConfig.reSendDelay_min){
			delayAdd=RUDPConfig.reSendDelay_min;
		}
		long time=(long) (System.currentTimeMillis()+delayAdd);
		return time;
	}
	
	public void run() {
		while(true){
			try {
				final ResendItem ri=taskList.take();
				if(ri.conn.isConnected()){
					long sleepTime=ri.getResendTime()-System.currentTimeMillis();
					if(sleepTime>0){
						Thread.sleep(sleepTime);
					}
					ri.addCount();
					
					if(ri.conn.sender.getDataMessage(ri.sequence)!=null){

						if(!ri.conn.stopnow){
							//多线程重发容易内存溢出
//							Route.es.execute(new Runnable() {
//								
//								@Override
//								public void run() {
//									ri.conn.sender.reSend(ri.sequence,ri.getCount());
//								}
//								
//							});
							ri.conn.sender.reSend(ri.sequence,ri.getCount());
						}
					
					}
					if(ri.getCount()<RUDPConfig.reSendTryTimes){
						ri.setResendTime(getNewResendTime(ri.conn));
						taskList.add(ri);
					}
				}
				if(ri.conn.clientControl.closed){
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
