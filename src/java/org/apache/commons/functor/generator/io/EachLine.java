/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//functor/src/java/org/apache/commons/functor/generator/io/Attic/EachLine.java,v 1.1 2003/06/30 11:00:18 rwaldhoff Exp $
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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

package org.apache.commons.functor.generator.io;

import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generator.GeneratorException;
import org.apache.commons.functor.generator.Generator;

import java.io.*;

/**
 * Generator for each line of a file or a Reader. The reader passed in or constructed
 * can optionally be closed after {@link #run}. If the reader is not closed, you may
 * re-use this generator.
 *
 * @since 1.0
 * @version $Revision: 1.1 $ $Date: 2003/06/30 11:00:18 $
 * @author  Jason Horman (jason@jhorman.org)
 */

public class EachLine extends Generator {

    /** The file to read lines from. */
    private File file = null;

    /** The encoding of the file. */
    private String encoding = null;

    /** The reader to read lines from. */
    private BufferedReader reader = null;

    /** Should the reader be closed at the end of execute? */
    private boolean closeAfterRun = false;

    /***************************************************
     *  Constructors
     ***************************************************/

    /**
     * Read from the file passed in one line at a time. The file WILL BE closed
     * at the end of the {@link #run} method. Each call to run will re-open the file.
     */
    public EachLine(File file) {
        this(file, true);
    }

    /**
     * Read from the file passed in one line at a time. The file WILL BE closed
     * at the end of the {@link #run} method. Each call to run will re-open the file.
     */
    public EachLine(File file, String encoding) {
        this(file, encoding, true);
    }

    /**
     * If the reader passed in was not a buffered reader a buffered reader will
     * be created to wrap the reader. If it was a buffered reader it will be
     * used directly. The reader WILL NOT be closed at the end of the {@link
     * #run} method. This means that {@link #run} can be used to read from the
     * same reader multiple times.
     */
    public EachLine(Reader reader) {
        this(reader, false);
    }

    /**
     * Read from the file passed in one line at a time.
     *
     * @param closeAfterRun If *true* the reader created in the {@link #run} method will
     *                   be closed at the end of run. The next call to run will create
     *                   another reader for the same file. If *false* the reader will
     *                   not be closed, and you may call run again to continue reading
     *                   from where the generator left off.
     */
    public EachLine(File file, boolean closeAfterRun) {
        this.file = file;
        this.closeAfterRun = closeAfterRun;
    }

    /**
     * Read from the file passed in one line at a time. The file WILL BE closed
     * at the end of the {@link #run} method.
     *
     * @param closeAfterRun If *true* the reader created in the {@link #run} method will
     *                   be closed at the end of run. The next call to run will create
     *                   another reader for the same file. If *false* the reader will
     *                   not be closed, and you may call run again to continue reading
     *                   from where the generator left off.
     */
    public EachLine(File file, String encoding,  boolean closeAfterRun) {
        this(file, closeAfterRun);
        this.encoding = encoding;
    }

    /**
     * If the reader passed in was not a buffered reader a buffered reader will
     * be created to wrap the reader. If it was a buffered reader it will be
     * used directly. The reader WILL NOT be closed at the end of the {@link
     * #run} method.
     *
     * @param closeAfterRun If *true* the reader will be closed at the end of run.
     *                   The next call to run would throw an exception.
     */
    public EachLine(Reader reader, boolean closeAfterRun) {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader)reader;
        } else {
            this.reader = new BufferedReader(reader);
        }

        this.closeAfterRun = closeAfterRun;
    }

    /***************************************************
     *  Instance methods
     ***************************************************/

    public File getFile() {
        return file;
    }

    public String getEncoding() {
        return encoding;
    }

    /**
     * Get the reader. Either returns the {@link #reader} passed into the
     * constructor or creates a new reader over the {@link #file} passed into
     * the constructor.
     */
    public BufferedReader getReader() throws IOException {
        if (reader == null && file != null) {
            if (encoding == null) {
                reader = new BufferedReader(new FileReader(file));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            }
        }

        return reader;
    }

    /**
     * Executes the function passed in for each line of the file/reader. If
     * a file was used to construct this Generator, the reader for that file
     * will be closed at the end of the run.
     */
    public void run(UnaryProcedure proc) {
        BufferedReader lineReader = null;

        try {
            lineReader = getReader();
            String line = null;
            while(!isStopped() && ((line = lineReader.readLine()) != null)) {
                proc.run(line);
            }
        } catch (IOException e) {
            throw new GeneratorException(e);
        } finally {
            if (closeAfterRun) {
                try {
                    lineReader.close();
                } catch (IOException e) {
                    throw new GeneratorException(e);
                } finally {
                    reader = null;
                }
            }
        }
    }

    public String toString() {
        if (file != null) {
            return "EachLine(" + file + ")";
        } else {
            return "EachLine(" + reader + ")";
        }
    }

    /***************************************************
     *  Class methods
     ***************************************************/

    /**
     * Convenience static so you can easily say:
     * <pre>
     *  EachLine.from(new File("file")).apply(function);
     *  instead of
     *  (new EachLine(new File("file"))).apply(function);
     * </pre>
     */
    public static final EachLine from(File file) {
        return new EachLine(file);
    }

    /**
     * Convenience static so you can easily say:
     * <pre>
     *  EachLine.from(new File("file"), encoding).apply(function);
     *  instead of
     *  (new EachLine(new File("file"), encoding)).apply(function);
     * </pre>
     */
    public static final EachLine from(File file, String encoding) {
        return new EachLine(file, encoding);
    }

    /**
     * Convenience static so you can easily say:
     * <pre>
     *  EachLine.from(reader).apply(function);
     *  instead of
     *  (new EachLine(reader)).apply(function);
     * </pre>
     */
    public static final EachLine from(Reader reader) {
        return new EachLine(reader);
    }

    /**
     * Convenience static so you can easily say:
     * <pre>
     *  EachLine.from(new File("file")).apply(function);
     *  instead of
     *  (new EachLine(new File("file"))).apply(function);
     * </pre>
     */
    public static final EachLine from(File file, boolean closeAfterRun) {
        return new EachLine(file, closeAfterRun);
    }

    /**
     * Convenience static so you can easily say:
     * <pre>
     *  EachLine.from(new File("file"), encoding).apply(function);
     *  instead of
     *  (new EachLine(new File("file"), encoding)).apply(function);
     * </pre>
     */
    public static final EachLine from(File file, String encoding, boolean closeAfterRun) {
        return new EachLine(file, encoding, closeAfterRun);
    }

    /**
     * Convenience static so you can easily say:
     * <pre>
     *  EachLine.from(reader).apply(function);
     *  instead of
     *  (new EachLine(reader)).apply(function);
     * </pre>
     */
    public static final EachLine from(Reader reader, boolean closeAfterRun) {
        return new EachLine(reader, closeAfterRun);
    }
}