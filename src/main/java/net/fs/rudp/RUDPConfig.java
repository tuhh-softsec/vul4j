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

public class RUDPConfig {

	public static short protocal_ver=0;

	public static int packageSize=1000;
	
	public static boolean twice_udp=false;
	
	public static boolean twice_tcp=false;
		
	public static int maxWin = 5*1024;
	
	public static int ackListDelay = 5;
	public static int ackListSum = 300;
	
	public static boolean double_send_start = true;
	
	public static int reSendDelay_min = 100;
	public static float reSendDelay = 0.37f;
	public static int reSendTryTimes = 10;

}
