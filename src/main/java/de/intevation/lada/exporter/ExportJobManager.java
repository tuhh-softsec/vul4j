/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */

package de.intevation.lada.exporter;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.laf.LafExportJob;

/**
 * Singleton class creating and managing ExportJobs
 * @author <a href="mailto:awoestmann@intevation.de">Alexander Woestmann</a>
 */
@ApplicationScoped
public class ExportJobManager {

    private static ExportJobManager instance;

    private static JobIdentifier identifier = new ExportJobManager.JobIdentifier();

    @Inject private Logger logger;

    private Map<String, ExportJob> activeJobs = new HashMap<String, ExportJob>();

    private ExportJobManager() {
        logger.debug("Creating ExportJobManager");
    };

    /**
     * Get the singleton instance
     * @return The ExportManager instance
     */
    public static ExportJobManager instance() {
        if (instance == null) {
            instance = new ExportJobManager();
        }
        return instance;
    }

    /**
     * Creates a new export job using the given format and parameters
     * @param format Export format
     * @param params Export parameters as JsonObject
     * @return The new ExportJob's id
     * @throws IllegalArgumentException if an invalid export format is specified
     */
    public String createExportJob(String format, JsonObject params) throws IllegalArgumentException {
        String id = getNextIdentifier();
        ExportJob newJob;

        switch (format) {
            case "laf":
                newJob = new LafExportJob(id);
                break;
            default:
                logger.error(String.format("Unkown export format: %s", format));
                throw new IllegalArgumentException(String.format("%s is not a valid export format", format));
        }

        String downloadFileName = params.getString("Filename");
        if (downloadFileName != null && !downloadFileName.equals("")) {
            newJob.setDownloadFileName(params.getString("filename"));
        }
        newJob.setExportParameter(params);
        newJob.run();
        activeJobs.put(id, newJob);

        return id;
    }

    /**
     * Get the status of a job by identifier
     * @param identifier Id to look for
     * @return
     */
    public JobStatus getJobStatus(String identifier) throws JobNotFoundException {
        ExportJob job = activeJobs.get(identifier);
        if (job == null) {
            throw new JobNotFoundException();
        }
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
        private static final long INITIAL_VALUE = 1l;

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