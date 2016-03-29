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

import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPInputStream {
	
	DatagramSocket ds;
	InetAddress dstIp;
	int dstPort;
	Receiver receiver;
	
	boolean streamClosed=false;
	
	ConnectionUDP conn;
	
	UDPInputStream(ConnectionUDP conn){
		this.conn=conn;
		receiver=conn.receiver;
	}
	
	public int read(byte[] b, int off, int len) throws ConnectException, InterruptedException {
		byte[] b2=null;
		b2 = read2();
		if(len<b2.length){
			throw new ConnectException("error5");
		}else{
			System.arraycopy(b2, 0, b, off, b2.length);
			return b2.length;
		}
	}
	
	public byte[] read2() throws ConnectException, InterruptedException{
		return receiver.receive();
	}
	
	public void closeStream_Local(){
		if(!streamClosed){
			receiver.closeStream_Local();
		}
	}


}
