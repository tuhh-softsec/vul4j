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

public class TrafficEvent {
	
	long eventId;
	
	int traffic;

	public static int type_downloadTraffic=10;
	
	public static int type_uploadTraffic=11;
	
	int type=type_downloadTraffic;
	
	String userId;
	
	TrafficEvent(long eventId,int traffic,int type){
		this(null,eventId,traffic,type);
	}
	
	public TrafficEvent(String userId,long eventId,int traffic,int type){
		this.userId=userId;
		this.eventId=eventId;
		this.traffic=traffic;
		this.type=type;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getType() {
		return type;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public int getTraffic() {
		return traffic;
	}

	public void setTraffic(int traffic) {
		this.traffic = traffic;
	}
	
	
	
}
