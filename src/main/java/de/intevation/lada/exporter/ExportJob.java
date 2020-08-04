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
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import javax.json.JsonObject;

import org.apache.log4j.Logger;

import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.Repository;

/**
 * Abstract class for an export job.
 */
public abstract class ExportJob extends Thread{

    /**
     * True if job has finished and will not change it's status anymore
     */
    private boolean done;
    /**
     * Result encoding
     */
    protected String encoding;

    /**
     * Exporter instance
     */
    protected Exporter exporter;

    /**
     * Parameters used for the export
     */
    protected JsonObject exportParameters;

    /**
     * The export format
     */
    protected String format;

    /**
     * Logger instance
     */
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
     * Repository used for loading data
     */
    protected Repository repository;

    /**
     * UserInfo
     */
    protected UserInfo userInfo;

    /**
     * Possible status values for export jobs
     */
    public enum status {waiting, running, finished, error}

    /**
     * The current job status
     */
    private status currentStatus;

    /**
     * Create a new job with the given id
     * @param jobId Job identifier
     */
    public ExportJob (String jobId) {
        this.done = false;
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
     * Clean up after the export has finished.
     *
     * Removes the result file
     * @throws JobNotFinishedException Thrown if job is still running
     */
    public void cleanup() throws JobNotFinishedException {
        if (!isDone()) {
            throw new JobNotFinishedException();
        }
        removeResultFile();
    }


    /**
     * Set this job to failed state
     * @param message Optional message
     */
    protected void fail(String message) {
        try {
            this.setCurrentStatus(status.error);
            this.setDone(true);
            this.message = message != null ? message: "";
        } catch (IllegalStatusTransitionException iste) {
            this.currentStatus = status.error;
            this.message = "Internal server errror";
            this.done = true;
        }
        finally {
            logger.error(String.format("Export failed with message: %s", message));
        }
    }


    /**
     * Set this job to finished state
     */
    protected void finish() {
        try {
            this.setCurrentStatus(status.finished);
            this.setDone(true);
        } catch (IllegalStatusTransitionException iste) {
            this.currentStatus = status.error;
            this.message = "Internal server errror";
            this.done = true;
        }
    }


    /**
     * Set this job to a running state
     */
    protected void runnning() {
        try {
            this.setCurrentStatus(status.running);
        } catch (IllegalStatusTransitionException iste) {
            this.currentStatus = status.error;
            this.message = "Internal server errror";
            this.done = true;
        }
    }

    /**
     * Get the filename used for downloading
     * @return Filename as String
     */
    public String getDownloadFileName() {
        return downloadFileName;
    }

    /**
     * Get the encoding
     * @return Encoding as String
     */
    public String getEncoding() {
        return this.encoding;
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
     * Get the export file's path
     * @return File path
     */
    public Path getOutputFilePath() {
        return outputFilePath;
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
     * Check if job is done and will no longer change its status
     * @return True if done, else false
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Checks if given charset is valid.
     *
     * Note that charset names are not case sensitive.
     * See https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/charset/Charset.html for further information.
     * @return True if charset is valid, else false
     */
    protected boolean isEncodingValid() {
        return Charset.isSupported(this.encoding);
    }

    /**
     * Run the ExportJob.
     * Should be overwritten in child classes.
     */
    public void run() {
        currentStatus = status.running;
    }

    /**
     * Set the current status
     *
     * @param status New status
     * @throws IllegalStatusTransitionException Thrown if job is already done
     */
    private void setCurrentStatus(status status) throws IllegalStatusTransitionException {
        if (isDone()) {
            throw new IllegalStatusTransitionException("Invalid job status transition: Job is already done");
        }
        this.currentStatus = status;
    }

    /**
     * Set the done state
     * @param done New done status
     * @throws IllegalArgumentException Thrown if argument is false and job is already done
     */
    protected void setDone(boolean done) throws IllegalArgumentException {
        if (!done && this.done) {
            throw new IllegalArgumentException("Job is already done, can not reset done to false");
        }
        this.done = done;
    }

    /**
     * Set the filename used for downloading the result file
     * @param downloadFileName File name
     */
    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }

    /**
     * Set the export encoding
     * @param encoding Encoding as String
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Set the exporter instance.
     * @param exporter The exporter instance
     */
    public void setExporter(Exporter exporter) {
        this.exporter = exporter;
    }

    /**
     * Set parameters used for the export
     * @param exportParameters Parameters as JsonObject
     */
    public void setExportParameter(JsonObject exportParameters) {
        this.exportParameters = exportParameters;
    }

    /**
     * Set the repository.
     * @param repository Repository instance
     */
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    /**
     * Set user info
     * @param userInfo New userInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * Remove the export's result file if present
     */
    protected void removeResultFile() {
        try {
            Files.delete(outputFilePath);
        } catch (NoSuchFileException nsfe) {
            logger.debug("Can not remove result file: File not found");
        } catch (IOException ioe) {
            logger.error(String.format("Cannot delete result file. IOException: %s", ioe.getStackTrace().toString()));
        }
    }

    /**
     * Write the export result to a file
     * @param result Result string to export
     * @return True if written successfully, else false
     */
    protected boolean writeResultToFile(String result) {
        Path tmpPath = Paths.get(outputFileLocation);
        logger.debug(String.format("Writing result to file %s", outputFilePath));

        //Create dir
        if (!Files.exists(tmpPath)) {
            try {
                Files.createDirectories(tmpPath);
            } catch (IOException ioe) {
                logger.error(String.format("JCannot create export folder. IOException: %s", ioe.getStackTrace().toString()));
                return false;
            } catch (SecurityException se) {
                logger.error(String.format("Security Exception during directory creation %s", se.getStackTrace().toString()));
                return false;
            }
        }

        //Create file
        try {
            Files.createFile(outputFilePath);
        } catch (FileAlreadyExistsException faee) {
            logger.error("Cannot create export file. File already exists");
            return false;
        } catch (IOException ioe) {
            logger.error(String.format("Cannot create export file. IOException: %s", ioe.getStackTrace().toString()));
            return false;
        } catch (SecurityException se) {
            logger.error(String.format("Security Exception during file creation %s", se.getStackTrace().toString()));
            return false;
        }

        //Write to file
        try (BufferedWriter writer = Files.newBufferedWriter(outputFilePath, StandardOpenOption.WRITE)) {
            writer.write(result);
        } catch (IOException ioe) {
            logger.error(String.format("Cannot write to export file. IOException: %s", ioe.getStackTrace().toString()));
            return false;
        }

        return true;
    }

    /**
     * Exception thrown if an unfished ExportJob is about to be removed while still runnning
     */
    public static class JobNotFinishedException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static class IllegalStatusTransitionException extends Exception {
        private static final long serialVersionUID = 2L;
        public IllegalStatusTransitionException(String msg) {
            super(msg);
        }
    }
}