/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */

package de.intevation.lada.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.ExportJob.JobNotFinishedException;
import de.intevation.lada.exporter.laf.LafExportJob;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;

/**
 * Class creating and managing ExportJobs
 * @author <a href="mailto:awoestmann@intevation.de">Alexander Woestmann</a>
 */
@ApplicationScoped
public class ExportJobManager {

    private static JobIdentifier identifier = new ExportJobManager.JobIdentifier();

    private Logger logger;

    private Map<String, ExportJob> activeJobs = new HashMap<String, ExportJob>();

    /**
     * The laf exporter.
     */
    @Inject
    @ExportConfig(format=ExportFormat.LAF)
    private Exporter lafExporter;

    /**
     * The data repository granting read-only access.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    public ExportJobManager() {
        logger = Logger.getLogger("ExportJobManager");
        logger.debug("Creating ExportJobManager");
    };

    /**
     * Creates a new export job using the given format and parameters
     * @param format Export format
     * @param encoding Result encoding
     * @param params Export parameters as JsonObject
     * @param userInfo UserInfo
     * @return The new ExportJob's id
     * @throws IllegalArgumentException if an invalid export format is specified
     */
    public String createExportJob(String format, String encoding, JsonObject params, UserInfo userInfo) throws IllegalArgumentException {
        String id = getNextIdentifier();
        ExportJob newJob;
        logger.debug(String.format("Creating new job: %s", id));

        switch (format) {
            case "laf":
                newJob = new LafExportJob(id);
                newJob.setExporter(lafExporter);
                newJob.setRepository(repository);
                break;
            default:
                logger.error(String.format("Unkown export format: %s", format));
                throw new IllegalArgumentException(String.format("%s is not a valid export format", format));
        }

        String downloadFileName = params.containsKey("filename")? params.getString("filename"): String.format("export.%s", format);

        newJob.setDownloadFileName(downloadFileName);
        newJob.setEncoding(encoding);
        newJob.setExportParameter(params);
        newJob.setUserInfo(userInfo);
        newJob.run();
        activeJobs.put(id, newJob);

        return id;
    }

    /**
     * Get Exportjob by id
     * @param identifier Id to look for
     * @throws JobNotFoundException Thrown if a job with the given can not be found
     */
    private ExportJob getJobById (String identifier) throws JobNotFoundException {
        ExportJob job = activeJobs.get(identifier);
        if (job == null) {
            throw new JobNotFoundException();
        }
        return job;
    }

    /**
     * Get the encoding of an export job by id
     * @param identifier Id to check
     * @return Encoding as String
     * @throws JobNotFoundException Thrown if a job with the given can not be found
     */
    public String getJobEncoding(String identifier) throws JobNotFoundException {
        ExportJob job = getJobById(identifier);
        return job.getEncoding();
    }

    /**
     * Get the filename used for downloading by the given job id
     * @param identifier Job id
     * @return Filename as String
     * @throws JobNotFoundException Thrown if a job with the given can not be found
     */
    public String getJobDownloadFilename(String identifier) throws JobNotFoundException {
        ExportJob job = getJobById(identifier);
        return job.getDownloadFileName();
    }

    /**
     * Get the status of a job by identifier
     * @param identifier Id to look for
     * @return Job status
     * @throws JobNotFoundException Thrown if a job with the given can not be found
     */
    public JobStatus getJobStatus(String identifier) throws JobNotFoundException {
        ExportJob job = getJobById(identifier);
        String jobStatus = job.getStatusName();
        String message = job.getMessage();
        return new JobStatus(jobStatus, message);
    }

    /**
     * Calculates and returns the next job identifier.
     * 
     * The new identifier will be stored in lastIdentifier.
     * @return New identifier as String
     */
    private synchronized String getNextIdentifier() {
        identifier.increase();
        return identifier.toString();
    }

    /**
     * Get the result file of the export job with the given id as stream
     * @param id ExportJob id
     * @return Result file as stream
     * @throws JobNotFoundException Thrown if a job with the given can not be found
     */
    public ByteArrayInputStream getResultFileAsStream(String id) throws JobNotFoundException {
        ExportJob job = activeJobs.get(id);
        if (job == null) {
            throw new JobNotFoundException();
        }
        Path filePath = job.getOutputFilePath();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Files.copy(filePath, outputStream);
            removeExportJob(job);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ioe) {
            logger.error(String.format("Error on reading result file: %s", ioe.getMessage()));
            return null;
        }
    }

    /**
     * Remove the given job from the active job list and trigger its cleanup function
     * @param job Job to remove
     */
    private void removeExportJob(ExportJob job) {
        try {
            job.cleanup();
        } catch (JobNotFinishedException jfe) {
            logger.warn(String.format("Tried to remove unfinished job %s", job.getJobId()));
        }
        activeJobs.remove(job.getJobId());
    }

    /**
     * Class modeling a job status.
     * Stores job status and message
     */
    public static class JobStatus {
        private String status;
        private String message;

        public JobStatus(String status, String message) {
            this.status = status;
            this.message = message;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class JobNotFoundException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Utility class that stores a long value and returns it as a hexadecimal String.
     */
    private static class JobIdentifier {

        /**
         * Initial value
         */
        private static final long INITIAL_VALUE = 0l;

        /**
         * Format string for the hexadecimal representation
         */
        private final String HEX_FORMAT;

        /**
         * Current value
         */
        private long value;

        /**
         * Create an identifier
         */
        public JobIdentifier () {
            this(INITIAL_VALUE);
        }

        /**
         * Create the identifier with an initial value
         * @param initValue
         */
        public JobIdentifier (long initValue) {
            value = initValue;
            String maxValueHex = Long.toHexString(Long.MAX_VALUE);
            int hexWidth = maxValueHex.length();
            StringBuilder formatBuilder = new StringBuilder("%1$0");
            formatBuilder.append(hexWidth);
            formatBuilder.append("x");
            HEX_FORMAT = formatBuilder.toString();
        }

        /**
         * Increase this identifier by one.
         * If the long overflows, it is set to 1
         */
        public void increase() {
            if (value == Long.MAX_VALUE) {
                value = 1l;
            } else {
                value++;
            }
        }

        /**
         * Return the hexadecimal string representation of this identifier.
         * The string will include padding zeroes.
         */
        public String toString() {
            return String.format(HEX_FORMAT, value);
        }
    }
}