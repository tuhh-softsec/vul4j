package org.jenkinsci.plugins.IBM_zOS_Connector;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.*;

/**
 * <h1>zOSJobSubmitter</h1>
 * Build step action for submitting JCL job.
 *
 * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
 *
 * @version 1.0
 */
public class zOSJobSubmitter extends Builder {
    /**
     * LPAR name or IP address.
     */
    private String server;
    /**
     * FTP port for connection
     */
    private int port;
    /**
     * UserID.
     */
    private String userID;
    /**
     * User password.
     */
    private String password;
    /**
     * Whether need to wait for the job completion.
     */
    private boolean wait;
    /**
     * Whether the job log is to be deleted upon job end.
     */
    private boolean deleteJobFromSpool;
    /**
     * Time to wait for the job to end. If set to <code>0</code> the buil will wait forever.
     */
    private int waitTime;
    /**
     * JCL of the job to be submitted.
     */
    private String job;

    /**
     * Constructor. Invoked when 'Apply' or 'Save' button is pressed on the project configuration page.
     *
     * @param server LPAR name or IP address.
     * @param port FTP port to connect to.
     * @param userID UserID.
     * @param password User password.
     * @param wait Whether we need to wait for the job completion.
     * @param waitTime Maximum wait time. If set to <code>0</code> will wait forever.
     * @param deleteJobFromSpool Whethe the job log will e deleted from the spool after end.
     * @param job JCL of the job to be submitted.
     */
    @DataBoundConstructor
    public zOSJobSubmitter (String server, int port, String userID, String password, boolean wait, int waitTime, boolean deleteJobFromSpool, String job)
    {
        // Copy values
        this.server = server.replaceAll("\\s","");
        this.port = port;
        this.userID = userID.replaceAll("\\s","");
        this.password = password.replaceAll("\\s","");
        this.wait = wait;
        this.waitTime = waitTime;
        this.deleteJobFromSpool = deleteJobFromSpool;
        this.job = job;
    }

    /**
     * Submit the job for execution.
     *
     * @param build Current build.
     * @param launcher Current launcher.
     * @param listener Current listener.
     *
     * @return Whether the job completed successfully.
     * <br> Always <code>true</code> if <b><code>wait</code></b> is <code>false</code>.
     *
     * @see zFTPConnector
     */
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener)
    {
        // Get connector.
        zFTPConnector zFTPConnector = new zFTPConnector(this.server,
                this.port,
                this.userID,
                this.password);
        // Read the JCL.
        InputStream inputStream = new ByteArrayInputStream(this.job.getBytes());
        // Prepare the output stream.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Submit the job.
        boolean result = zFTPConnector.submit(inputStream,this.wait,this.waitTime,outputStream,this.deleteJobFromSpool);

        // Get CC.
        String printableCC = zFTPConnector.getJobCC();
        if(printableCC != null)
            printableCC = printableCC.replaceAll("\\s+","");

        // If wait was requested try to save the job log.
        if(this.wait)
        {
            // Save the log.
            try
            {
                FilePath savedOutput = new FilePath(build.getWorkspace(),
                        String.format("[%s - %s] %s - %s [%s].log",
                                build.getParent().getDisplayName(),
                                build.getId(),
                                this.server,
                                zFTPConnector.getJobID(),
                                printableCC
                        ));
                outputStream.writeTo(savedOutput.write());
                outputStream.close();
                build.doArtifact();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (InterruptedException i)
            {
                i.printStackTrace();
            }
        }
        else
        {
            printableCC = "0000"; //set RC = 0
        }

        // Return whether the job succeeded or not.
        return result && "0000".equals(printableCC);
    }

    /**
     * Get LPAR name of IP address.
     *
     * @return <b><code>server</code></b>
     */
    public String getServer()
    {
        return this.server;
    }

    /**
     * Get FTP port to connect to.
     *
     * @return <b><code>port</code></b>
     */
    public int getPort()
    {
        return this.port;
    }

    /**
     * Get UserID.
     *
     * @return <b><code>userID</code></b>
     */
    public String getUserID()
    {
        return this.userID;
    }

    /**
     * Get User Password.
     *
     * @return <b><code>password</code></b>
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * Get wait.
     *
     * @return <b><code>wait</code></b>
     */
    public boolean getWait()
    {
        return this.wait;
    }

    /**
     * Get deleteJobFromSpool.
     *
     * @return <b><code>deleteJobFromSpool</code></b>
     */
    public boolean getDeleteJobFromSpool() { return this.deleteJobFromSpool; }

    /**
     * Get wait time.
     *
     * @return <b><code>waitTime</code></b>
     */
    public int getWaitTime()
    {
        return this.waitTime;
    }

    /**
     * Get Job.
     *
     * @return <b><code>job</code></b>
     */
    public String getJob()
    {
        return this.job;
    }

    /**
     * Get descriptor for this class.
     *
     * @return descriptor for this class.
     */
    @Override
    public zOSJobSubmitterDescriptor getDescriptor() {
        return (zOSJobSubmitterDescriptor)super.getDescriptor();
    }

    /**
     * <h1>zOSJobSubmitterDescriptor</h1>
     * Descriptor for zOSJobSubmitter.
     *
     * @author Alexander Shchrbakov (candiduslynx@gmail.com)
     *
     * @version 1.0
     */
    @Extension
    public static final class zOSJobSubmitterDescriptor extends BuildStepDescriptor<Builder>
    {
        /**
         * Primitive constructor.
         */
        public zOSJobSubmitterDescriptor() {
            load();
        }

        /**
         * Function for validation of 'Server' field on project configuration page
         *
         * @param value Current server.
         *
         * @return Whether server name looks OK.
         *
         * @throws IOException
         * @throws ServletException
         */
        public FormValidation doCheckServer(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a server");
            return FormValidation.ok();
        }

        /**
         * Function for validation of 'User ID' field on project configuration page
         *
         * @param value Current userID.
         *
         * @return Whether userID looks OK.
         *
         * @throws IOException
         * @throws ServletException
         */
        public FormValidation doCheckUsername(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a username");
            return FormValidation.ok();
        }

        /**
         * Function for validation of 'Password' field on project configuration page
         *
         * @param value Current password.
         *
         * @return Whether password looks OK.
         *
         * @throws IOException
         * @throws ServletException
         */
        public FormValidation doCheckPassword(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a password");
            return FormValidation.ok();
        }

        /**
         * Function for validation of 'Job' field on project configuration page
         *
         * @param value Current job.
         *
         * @return Whether job looks OK.
         *
         * @throws IOException
         * @throws ServletException
         */
        public FormValidation doCheckInput(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set an input");
            return FormValidation.ok();
        }

        /**
         * Function for validation of 'Wait Time' field on project configuration page
         *
         * @param value Current wait time.
         *
         * @return Whether wait time looks OK.
         *
         * @throws IOException
         * @throws ServletException
         */
        public FormValidation doCheckWaitTime(@QueryParameter String value)
                throws IOException, ServletException {
            if (!value.matches("\\d*"))
                return FormValidation.error("Value must be numeric");
            if(Integer.parseInt(value) < 0)
                return FormValidation.error("Value must not be negative");
            return FormValidation.ok();
        }

        /**
         * If this build step can be used with the project.
         *
         * @param aClass Project description class.
         *
         * @return Always <code>true</code>.
         */
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * Get printable name.
         *
         * @return Printable name for project configuration page.
         */
        public String getDisplayName() {
            return "Submit zOS Job";
        }
    }
}