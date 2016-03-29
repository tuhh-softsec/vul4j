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

import net.fs.rudp.message.AckListMessage;

import java.util.ArrayList;
import java.util.HashSet;


public class AckListTask {
	ConnectionUDP conn;
	AckListMessage alm;
	int lastRead=0;
	ArrayList<Integer> ackList;
	@SuppressWarnings("unchecked")
	HashSet set;
	AckListTask(ConnectionUDP conn){
		this.conn=conn;
		ackList=new ArrayList();
		set=new HashSet();
	}
	
	synchronized void addAck(int sequence){
		////#MLog.println("sendACK "+sequence);
		if(!set.contains(sequence)){
			ackList.add(sequence);
			set.add(sequence);
		}
	}
	
	synchronized void run(){
		int offset=0;
		int packetLength=RUDPConfig.ackListSum;
		int length=ackList.size();
		////#MLog.println("ffffffffaaaaaaaaa "+length);
		int sum=(length/packetLength);
		if(length%packetLength!=0){
			sum+=1;
		}
		if(sum==0){
			sum=1;
		}
		int len=packetLength;
		if(length<=len){
			conn.sender.sendALMessage(ackList);
			conn.sender.sendALMessage(ackList);
		}else{
			for(int i=0;i<sum;i++){
				ArrayList<Integer> nl=copy(offset,len,ackList);
				conn.sender.sendALMessage(nl);
				conn.sender.sendALMessage(nl);
//				conn.sender.sendALMessage(nl);
//				conn.sender.sendALMessage(nl);
//				conn.sender.sendALMessage(nl);
				offset+=packetLength;
				////#MLog.println("fffffffffa "+nl.size());
				if(offset+len>length){
					len=length-(sum-1)*packetLength;
				}
			}
		}
	}
	
	ArrayList<Integer> copy(int offset,int length,ArrayList<Integer> ackList){
		ArrayList<Integer> nl= new ArrayList<Integer>();
		for(int i=0;i<length;i++){
			nl.add(ackList.get(offset+i));
		}
		return nl;
	}
}
