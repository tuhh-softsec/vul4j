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

public class ResendItem {
	
	int count;
	
	ConnectionUDP conn;
	
	int sequence;
	
	long resendTime;
	
	ResendItem(ConnectionUDP conn,int sequence){
		this.conn=conn;
		this.sequence=sequence;
	}
	
	void addCount(){
		count++;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ConnectionUDP getConn() {
		return conn;
	}

	public void setConn(ConnectionUDP conn) {
		this.conn = conn;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public long getResendTime() {
		return resendTime;
	}

	public void setResendTime(long resendTime) {
		this.resendTime = resendTime;
	}

}
