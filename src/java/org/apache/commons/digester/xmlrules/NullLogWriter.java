/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.commons.digester.xmlrules;


import java.io.*;
import org.apache.commons.digester.Digester;


/**
 * Used to keep Digester from outputting log messages when exceptions
 * are thrown from rules. This class is part of a workaround for the
 * following problem: If a Digester Rule throws an exception, the
 * Digester will output the exception's stack trace to it's log
 * Writer, even if it's debug level is set to 0. The default log
 * writer is System.out.<P> If this class is registered with the
 * Digester, it enforces the contract that <strong>no</strong>
 * Digester log messages are output when the Digester.debug==0.
 * <P>Usage:<pre>
 *     Digester digester = new Digester();
 *     digester.setWriter(new NullLogWriter(digester));
 *     digester.setDebug(0);
 *     digester.parse(file);  // the digester will log nothing
 *     ...
 *     digester.setDebug(5);
 *     digester.parse(file2); // now the digester will log to stderr
 * </pre>
 * <P>
 * This class can also be used as a general null output writer, by passing a 
 * null reference for the Writer parameter in its constructor:<pre>
 *     Digester digester = new Digester();
 *     digester.setWriter(new NullLogWriter(digester, null));
 *     digester.setDebug(5);
 *     digester.parse(file);  // the digester will log nothing,
 *                            // regardless of the debug level.
 * </pre> 
 *
 * @author David H. Martin - Initial Contribution
 * @author Scott Sanders   - Added ASL, removed external dependencies
 */
public class NullLogWriter extends Writer {

    private Writer realWriter;

    private Digester digester;

    /**
     * Constructs a NullLogWriter associated with a given
     * Digester. The writer will output nothing unless
     * digester.getDebug() > 0, in which case all output is passed to
     * the given writer.
     * @param digester The Digester to which this writer is to be registered.
     * @param writer Another Writer, to which all output is delegated
     */
    public NullLogWriter(Digester digester, Writer writer) {
        realWriter = writer;
        this.digester = digester;
    }

    /**
     * Constructs a NullLogWriter associated with a given
     * Digester. The writer will output nothing unless
     * digester.getDebug() > 0, in which case all output is passed to
     * System.err.
     * @param digester The Digester to which this writer is to be registered.
     */
    public NullLogWriter(Digester digester) {
        this(digester, new PrintWriter(System.err));
    }

    private boolean canLog() {
        return digester.getDebug() > 0;
    }

    public void close() throws IOException {
        if (realWriter == null) return;
        realWriter.close();
    }

    public void flush() throws IOException {
        if (realWriter == null) return;
        realWriter.flush();
    }

    public void write(char[] cbuf) throws IOException {
        if (realWriter == null) return;
        if (canLog()) realWriter.write(cbuf);
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        if (realWriter == null) return;
        if (canLog()) realWriter.write(cbuf, off, len);
    }

    public void write(int c) throws IOException {
        if (realWriter == null) return;
        if (canLog()) realWriter.write(c);
    }

    public void write(String str) throws IOException {
        if (realWriter == null) return;
        if (canLog()) realWriter.write(str);
    }

    public void write(String str, int off, int len) throws IOException {
        if (realWriter == null) return;
        if (canLog()) realWriter.write(str, off, len);
    }
    
}
