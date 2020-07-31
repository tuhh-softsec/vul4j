/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.inject.Inject;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

/**
 * Abstract class for an export job.
 */
public abstract class ExportJob extends Thread{

    /**
     * Parameters used for the export
     */
    protected JsonObject exportParameters;

    /**
     * The export format
     */
    protected String format;

    @Inject
    protected Logger logger;

    /**
     * Message String, used in case of an error
     */
    protected String message;

    /**
     * Filename set by the users request
     */
    protected String downloadFileName;

    /**
     * Temporary output file's name
     */
    protected String outputFileName;

    /**
     * Output file's location
     */
    protected String outputFileLocation;

    /**
     * Complete path to the output file
     */
    protected Path outputFilePath;
    /**
     * Id of this export job
     */
    protected String jobId;

    /**
     * Possible status values for export jobs
     */
    enum status {waiting, running, finished, error}

    /**
     * The current job status
     */
    protected status currentStatus;

    /**
     * Create a new job with the given id
     * @param jobId Job identifier
     */
    public ExportJob (String jobId) {
        this.jobId = jobId;
        this.currentStatus = status.waiting;
        this.outputFileLocation = "/tmp/lada-server/";
        if (!outputFileLocation.endsWith("/")) {
            outputFileLocation += "/";
        }
        this.outputFileName = jobId;
        this.outputFilePath = Paths.get(outputFileLocation + outputFileName);

        this.message = "";
    }

    /**
     * Set this job to failed state
     * @param message Optional message
     */
    protected void fail(String message) {
        this.currentStatus = status.error;
        this.message = message != null ? message: "";
    }

    /**
     * Get the export format as String.
     * @return Export format as String
     */
    public String getFormat() {
        return format;
    }

    /**
     * Return the job identifier.
     * @return Identifier as String
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Return the message String.
     * @return message as String
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the output file name.
     * @return Output file name String
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * Return the current job status.
     * @return Job status
     */
    public status getStatus() {
        return currentStatus;
    }
    /**
     * Return the current status as String.
     * @return Status as String
     */
    public String getStatusName() {
        return currentStatus.name();
    }

    /**
     * Run the ExportJob.
     * Should be overwritten in child classes.
     */
    public void run() {
        currentStatus = status.running;
    }

    /**
     * Set the filename used for downloading the result file
     * @param downloadFileName File name
     */
    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    /**
     * Set parameters used for the export
     * @param exportParameters Parameters as JsonObject
     */
    public void setExportParameter(JsonObject exportParameters) {
        this.exportParameters = exportParameters;
    }

    /**
     * Write the export result to a file
     * @param result Result string to export
     * @return True if written successfully, else false
     */
    protected boolean writeResultToFile(String result) {
        Path tmpPath = Paths.get(outputFileLocation);
        logger.debug(String.format("Jobid %s: Writing result to file %s", jobId, outputFilePath));

        //Create dir
        if (!Files.exists(tmpPath)) {
            try {
                Files.createDirectories(tmpPath);
            } catch (IOException ioe) {
                logger.error(String.format("Jobid %s: Cannot create export folder. IOException: %s", jobId, ioe.getStackTrace()));
                return false;
            } catch (SecurityException se) {
                logger.error(String.format("Jobid %s: Security Exception during directory creation %s", jobId, se.getStackTrace()));
                return false;
            }
        }

        //Create file
        try {
            Files.createFile(outputFilePath);
        } catch (FileAlreadyExistsException faee) {
            logger.error(String.format("Jobid %s: Cannot create export file. File already exists", jobId));
            return false;
        } catch (IOException ioe) {
            logger.error(String.format("Jobid %s: Cannot create export file. IOException: %s", jobId, ioe.getStackTrace()));
            return false;
        } catch (SecurityException se) {
            logger.error(String.format("Jobid %s: Security Exception during file creation %s", jobId, se.getStackTrace()));
            return false;
        }

        //Write to file
        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, StandardOpenOption.WRITE)) {
            writer.write(result);
        } catch (IOException ioe) {
            logger.error(String.format("Jobid %s: Cannot write to export file. IOException: %s", jobId, ioe.getStackTrace()));
            return false;
        }

        return true;
    }
}