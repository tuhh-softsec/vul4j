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

package net.fs.rudp.message;

import net.fs.utils.ByteIntConvert;
import net.fs.utils.ByteShortConvert;

import java.net.DatagramPacket;


public class PingMessage extends Message{
	
	public short sType=net.fs.rudp.message.MessageType.sType_PingMessage;
	
	byte[] dpData=new byte[20];
	
	int pingId;
	int downloadSpeed,uploadSpeed;
	
	public PingMessage(int connectId,int clientId,int pingId,int downloadSpeed,int uploadSpeed){
		ByteShortConvert.toByteArray(ver, dpData, 0);  //add: ver
		ByteShortConvert.toByteArray(sType, dpData, 2);  //add: service type
		ByteIntConvert.toByteArray(connectId, dpData, 4); //add: sequence
		ByteIntConvert.toByteArray(clientId, dpData, 8); //add: sequence
		ByteIntConvert.toByteArray(pingId, dpData, 12); //add: sequence
		ByteShortConvert.toByteArray((short) (downloadSpeed/1024), dpData, 16);
		ByteShortConvert.toByteArray((short) (uploadSpeed/1024), dpData, 18);
		dp=new DatagramPacket(dpData,dpData.length);
	}
	
	public PingMessage(DatagramPacket dp){
		this.dp=dp;
		dpData=dp.getData();
		ver=ByteShortConvert.toShort(dpData, 0);
		sType=ByteShortConvert.toShort(dpData, 2);
		connectId=ByteIntConvert.toInt(dpData, 4);
		clientId=ByteIntConvert.toInt(dpData, 8);
		pingId=ByteIntConvert.toInt(dpData, 12);
		downloadSpeed=ByteShortConvert.toShort(dpData, 16);
		uploadSpeed=ByteShortConvert.toShort(dpData, 18);
	}

	public int getPingId() {
		return pingId;
	}

	public void setPingId(int pingId) {
		this.pingId = pingId;
	}

	public int getDownloadSpeed() {
		return downloadSpeed;
	}

	public void setDownloadSpeed(int downloadSpeed) {
		this.downloadSpeed = downloadSpeed;
	}

	public int getUploadSpeed() {
		return uploadSpeed;
	}

	public void setUploadSpeed(int uploadSpeed) {
		this.uploadSpeed = uploadSpeed;
	}

}
