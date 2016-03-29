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

package net.fs.utils;


public class ByteIntConvert {
    
    public static int toInt(byte[] b,int offset) { 
    	return b[offset + 3] & 0xff | (b[offset + 2] & 0xff) << 8
        | (b[offset + 1] & 0xff) << 16 | (b[offset] & 0xff) << 24;
    }
    
    public static void toByteArray(int n,byte[] buf,int offset) {
    	buf[offset] = (byte) (n >> 24);
    	   buf[offset + 1] = (byte) (n >> 16);
    	   buf[offset + 2] = (byte) (n >> 8);
    	   buf[offset + 3] = (byte) n;
    }

}







