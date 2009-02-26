/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */

/*
 *  Copyright 1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.sun.grizzly.http.algorithms;

import com.sun.grizzly.http.Constants;
import com.sun.grizzly.util.Interceptor;
import com.sun.grizzly.http.SelectorThread;

import java.nio.ByteBuffer;
import java.nio.BufferUnderflowException;
import java.util.logging.Level;

import com.sun.grizzly.util.buf.Ascii;

/**
 * Predict if the NIO channel has been fully read or not. This lagorithm will 
 * first search for the content-length header, and use that value to determine if
 * the bytes has been fully read or not. If the content-length isn't included,
 * it will search for the end of the HTTP stream, which is a '\r\n' without 
 * buffering the body.
 *
 *
 * @author Jean-Francois Arcand
 */
public final class SeekHeaderAlgorithm extends ContentLengthAlgorithm{


     // ---------------------------------------------- Constructor ----------/
    
    
    public SeekHeaderAlgorithm() {
        super();
    }
    
 
    /**
     * Parse the <code>ByteBuffer</code> and try to determine if the bytes
     * stream has been fully read from the <code>SocketChannel</code>.
     *    
     * Drain the <code>SocketChannel</code> and determine if the request bytes
     * has been fully read. For POST method, parse the bytes and seek for the
     * content-type header to determine the length of the request bytes.
     * @return true if we need to call back the <code>SelectorThread</code>
     *              This occurs when the stream doesn't contains all the 
     *              request bytes.
     *         false if the stream contains all request bytes.    
     *
     * @paran byteBuffer the bytes read.
     * @return true if the algorithm determines the end of the stream.
     */
    public boolean parse(ByteBuffer byteBuffer){  
        isFound = false;

        curLimit = byteBuffer.limit();
        curPosition = byteBuffer.position();

        // Rule a - if we know the content length, verify all bytes are read
        //          Return true only if all bytes has been read.
        if ( contentLength != -1 ){
            byteBuffer.flip();
            return isFound;
        }
       
        try{
            // Rule b - If nothing, return to the Selector.
            if (byteBuffer.position() == 0)
                return false;
                    
            byteBuffer.flip();
            lastValid = byteBuffer.limit();

            // Rule c - Parse the request line
            if ( !requestLineParsed ) {
                requestLineParsed = parseRequestLine(byteBuffer);
                if ( !requestLineParsed ) {
                    return false;
                }
            }

            // Rule d -- Parse the headers, looking for content-length
            while (parseHeader(byteBuffer));
           
            return isFound;
        } catch (BufferUnderflowException bue) {
            SelectorThread.logger().log(Level.SEVERE,
                    "readTask.bufferunderflow", bue);
            return false;
        } finally {       
            byteBuffer.limit(curLimit);
            byteBuffer.position(curPosition);
            
            if (isFound){
                byteBuffer.flip();
            }
        }         
    }
    
}
