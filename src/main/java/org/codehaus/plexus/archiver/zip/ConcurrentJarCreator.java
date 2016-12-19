/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.codehaus.plexus.archiver.zip;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.archivers.zip.ScatterZipOutputStream;
import org.apache.commons.compress.archivers.zip.StreamCompressor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequestSupplier;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;
import org.codehaus.plexus.util.IOUtil;

import static org.apache.commons.compress.archivers.zip.ZipArchiveEntryRequest.createZipArchiveEntryRequest;

public class ConcurrentJarCreator
{

    private final boolean compressAddedZips;

    private final ScatterZipOutputStream directories;

    private final ScatterZipOutputStream metaInfDir;

    private final ScatterZipOutputStream manifest;

    private final ScatterZipOutputStream synchronousEntries;

    private final ParallelScatterZipCreator parallelScatterZipCreator;

    private long zipCloseElapsed;

    private static class DeferredSupplier
        implements ScatterGatherBackingStoreSupplier
    {

        private int threshold;

        DeferredSupplier( int threshold )
        {
            this.threshold = threshold;
        }

        public ScatterGatherBackingStore get()
            throws IOException
        {
            return new DeferredScatterOutputStream( threshold );
        }

    }

    public static ScatterZipOutputStream createDeferred(
        ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier )
        throws IOException
    {
        ScatterGatherBackingStore bs = scatterGatherBackingStoreSupplier.get();
        StreamCompressor sc = StreamCompressor.create( Deflater.DEFAULT_COMPRESSION, bs );
        return new ScatterZipOutputStream( bs, sc );
    }

    /**
     * Creates a new {@code ConcurrentJarCreator} instance.
     * <p/>
     * {@code ConcurrentJarCreator} creates zip files using several concurrent threads.
     * <p/>
     * This constructor has the same effect as
     * {@link #ConcurrentJarCreator(boolean, int) ConcurrentJarCreator(true, nThreads) }
     *
     * @param nThreads The number of concurrent thread used to create the archive
     *
     * @throws IOException
     */
    public ConcurrentJarCreator( int nThreads ) throws IOException
    {
        this( true, nThreads );
    }

    /**
     * Creates a new {@code ConcurrentJarCreator} instance.
     * <p/>
     * {@code ConcurrentJarCreator} creates zip files using several concurrent threads.
     * Entries that are already zip file could be just stored or compressed again.
     *
     * @param compressAddedZips Indicates if entries that are zip files should be compressed.
     *                          If set to {@code false} entries that are zip files will be added using
     *                          {@link ZipEntry#STORED} method.
     *                          If set to {@code true} entries that are zip files will be added using
     *                          the compression method indicated by the {@code ZipArchiveEntry} passed
     *                          to {@link #addArchiveEntry(ZipArchiveEntry, InputStreamSupplier, boolean)}.
     *                          The compression method for all entries that are not zip files will not be changed
     *                          regardless of the value of this parameter
     * @param nThreads The number of concurrent thread used to create the archive
     *
     * @throws IOException
     */
    public ConcurrentJarCreator( boolean compressAddedZips, int nThreads ) throws IOException
    {
        this.compressAddedZips = compressAddedZips;
        ScatterGatherBackingStoreSupplier defaultSupplier = new DeferredSupplier( 100000000 / nThreads );
        directories = createDeferred( defaultSupplier );
        manifest = createDeferred( defaultSupplier );
        metaInfDir = createDeferred( defaultSupplier );
        synchronousEntries = createDeferred( defaultSupplier );
        parallelScatterZipCreator = new ParallelScatterZipCreator( Executors.newFixedThreadPool( nThreads ),
                                                                   defaultSupplier );

    }

    /**
     * Adds an archive entry to this archive.
     * <p/>
     * This method is expected to be called from a single client thread
     *
     * @param zipArchiveEntry The entry to add. Compression method
     * @param source The source input stream supplier
     * @param addInParallel Indicates if the entry should be add in parallel.
     * If set to {@code false} the entry is added synchronously.
     *
     * @throws java.io.IOException
     */
    public void addArchiveEntry( final ZipArchiveEntry zipArchiveEntry, final InputStreamSupplier source,
                                 final boolean addInParallel ) throws IOException
    {
        final int method = zipArchiveEntry.getMethod();
        if ( method == -1 )
        {
            throw new IllegalArgumentException( "Method must be set on the supplied zipArchiveEntry" );
        }
        if ( zipArchiveEntry.isDirectory() && !zipArchiveEntry.isUnixSymlink() )
        {
            final ByteArrayInputStream payload = new ByteArrayInputStream( new byte[]
            {
            } );

            directories.addArchiveEntry( createZipArchiveEntryRequest( zipArchiveEntry, createInputStreamSupplier(
                                                                       payload ) ) );

            payload.close();
        }
        else if ( "META-INF".equals( zipArchiveEntry.getName() ) || "META-INF/".equals( zipArchiveEntry.getName() ) )
        {
            InputStream payload = source.get();
            if ( zipArchiveEntry.isDirectory() )
            {
                zipArchiveEntry.setMethod( ZipEntry.STORED );
            }
            metaInfDir.addArchiveEntry( createZipArchiveEntryRequest( zipArchiveEntry,
                                                                      createInputStreamSupplier( payload ) ) );

            payload.close();
        }
        else if ( "META-INF/MANIFEST.MF".equals( zipArchiveEntry.getName() ) )
        {
            InputStream payload = source.get();
            if ( zipArchiveEntry.isDirectory() )
            {
                zipArchiveEntry.setMethod( ZipEntry.STORED );
            }
            manifest.addArchiveEntry( createZipArchiveEntryRequest( zipArchiveEntry,
                                                                    createInputStreamSupplier( payload ) ) );

            payload.close();
        }
        else if ( addInParallel )
        {
            parallelScatterZipCreator.addArchiveEntry( createEntrySupplier( zipArchiveEntry, source ) );
        }
        else
        {
            synchronousEntries.addArchiveEntry( createEntry( zipArchiveEntry, source ) );
        }
    }

    private InputStreamSupplier createInputStreamSupplier( final InputStream payload )
    {
        return new InputStreamSupplier()
        {

            @Override
            public InputStream get()
            {
                return payload;
            }

        };
    }

    public void writeTo( ZipArchiveOutputStream targetStream ) throws IOException, ExecutionException,
                                                                      InterruptedException
    {
        metaInfDir.writeTo( targetStream );
        manifest.writeTo( targetStream );
        directories.writeTo( targetStream );
        synchronousEntries.writeTo( targetStream );
        parallelScatterZipCreator.writeTo( targetStream );
        long startAt = System.currentTimeMillis();
        targetStream.close();
        zipCloseElapsed = System.currentTimeMillis() - startAt;
        metaInfDir.close();
        manifest.close();
        directories.close();
        synchronousEntries.close();
    }

    /**
     * Returns a message describing the overall statistics of the compression run
     *
     * @return A string
     */
    public String getStatisticsMessage()
    {
        return parallelScatterZipCreator.getStatisticsMessage() + " Zip Close: " + zipCloseElapsed + "ms";
    }

    private ZipArchiveEntryRequestSupplier createEntrySupplier( final ZipArchiveEntry zipArchiveEntry,
                                                                final InputStreamSupplier inputStreamSupplier )
    {

        return new ZipArchiveEntryRequestSupplier()
        {

            @Override
            public ZipArchiveEntryRequest get()
            {
                try
                {
                    return createEntry( zipArchiveEntry, inputStreamSupplier );
                }
                catch ( IOException e )
                {
                    throw new RuntimeException( e );
                }
            }

        };
    }

    private ZipArchiveEntryRequest createEntry( final ZipArchiveEntry zipArchiveEntry,
                                                final InputStreamSupplier inputStreamSupplier ) throws IOException
    {
        // if we re-compress the zip files there is no need to look at the input stream

        if ( compressAddedZips )
        {
            return createZipArchiveEntryRequest( zipArchiveEntry, inputStreamSupplier );
        }

        // otherwise we should inspect the first four bites to see if the input stream is zip file or not

        InputStream is = inputStreamSupplier.get();
        byte[] header = new byte[4];
        try
        {
            int read = is.read( header );
            int compressionMethod = zipArchiveEntry.getMethod();
            if ( isZipHeader( header ) ) {
                compressionMethod = ZipEntry.STORED;
            }

            zipArchiveEntry.setMethod( compressionMethod );

            return createZipArchiveEntryRequest( zipArchiveEntry, prependBytesToStream( header, read, is ) );
        }
        catch ( IOException e )
        {
            IOUtil.close( is );
            throw e;
        }
    }

    private boolean isZipHeader( byte[] header )
    {
        return header[0] == 0x50 && header[1] == 0x4b && header[2] == 3 && header[3] == 4;
    }

    private InputStreamSupplier prependBytesToStream( final byte[] bytes, final int len, final InputStream stream )
    {
        return new InputStreamSupplier() {

            @Override
            public InputStream get()
            {
                return len > 0
                            ? new SequenceInputStream( new ByteArrayInputStream( bytes, 0, len ), stream )
                            : stream;
            }

        };

    }

}
