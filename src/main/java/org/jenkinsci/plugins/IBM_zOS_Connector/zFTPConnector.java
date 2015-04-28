package org.jenkinsci.plugins.IBM_zOS_Connector;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <h1>zFTPConnector</h1>
 * FTP-based communication with z/OS-like systems.
 * Used for submitting jobs, fetching job log and extraction of MaxCC.
 *
 * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
 *
 * @version 1.0
 */
public class zFTPConnector
{
    // Server info.
    /**
     * LPAR name or IP to connect to.
     */
    private String server;
    /**
     * FTP port for connection
     */
    private int port;

    // Credentials.
    /**
     * UserID.
     */
    private String userID;
    /**
     * User password.
     */
    private String password;

    // Wait parameters.
    /**
     * Time to wait before giving up in milliseconds. If set to <code>0</code> will wait forever.
     */
    private long waitTime;

    // Job info from JES-like system.
    /**
     * JobID in JES.
     */
    private String jobID;
    /**
     * Job's MaxCC.
     */
    private String jobCC;

    // Work elements.
    /**
     * Will ask LPAR once in 10 seconds.
     */
    private static final long waitInterval = 10*1000;
    /**
     * FTPClient from <i>Apache Commons-Net</i>. Used for FTP communication.
     */
    private FTPClient FTPClient;
    /**
     * Pattern for search of jobName
     */
    private static final Pattern JesJobName = Pattern.compile("250-It is known to JES as (.*)");
    /**
     * Pattern for check of job status.
     */
    private static final Pattern JobNotFinished = Pattern.compile(".*No spool files available for.*");


    /**
     * Basic constructor with minimal parameters required.
     *
     * @param server LPAR name or IP address to connect to.
     * @param port LPAR password.
     * @param userID UserID.
     * @param password User password.
     */
    public zFTPConnector (String server, int port, String userID, String password)
    {
        // Copy values
        this.server = server;
        this.port = port;
        this.userID = userID;
        this.password = password;

        // Create FTPClient
        this.FTPClient = new FTPClient();
        // Make password invisible from log
        this.FTPClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
    }

    /**
     * Try to connect to the <b><code>server</code></b> using the parameters passed to the constructor.
     *
     * @return Whether the connection was established using the parameters passed to the constructor.
     *
     * @see zFTPConnector#zFTPConnector(java.lang.String, int, java.lang.String, java.lang.String)
     */
    private boolean connect()
    {
        // Perform the connection.
        try
        {
            int reply; // Temp value to contain server response.

            // Try to connect.
            this.FTPClient.connect(this.server, this.port);

            // After connection attempt, check the reply code to verify success.
            reply = this.FTPClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                // Bad reply code.
                this.FTPClient.disconnect(); // Disconnect from LPAR.
                System.err.println("FTP server refused connection."); // Print error.
                return false; // Finish with failure.
            }
        }
        // IOException handling
        catch (IOException e)
        {
            // Clos the connection if it's still opened.
            if (this.FTPClient.isConnected())
            {
                try
                {
                    this.FTPClient.disconnect();
                }
                catch (IOException f)
                {
                    // Do nothing
                }
            }
            System.err.println("Could not connect to server.");
            e.printStackTrace();
            return false;
        }
        // Finall, return with success.
        return true;
    }

    /**
     * Try to logon to the <b><code>server</code></b> using the parameters passed to the constructor.
     * Also, <code>SITE FILETYPE=JES JESJOBNAME=*</code> command is invoked.
     *
     * @return Whether the credentials supplied are valid and the connection was established.
     *
     * @see zFTPConnector#zFTPConnector(java.lang.String, int, java.lang.String, java.lang.String)
     * @see zFTPConnector#connect()
     */
    private boolean logon()
    {
        // Check whether we are already connected. If not, try to reconnect.
        if (!this.FTPClient.isConnected())
            if(!this.connect())
                return false; // Couldn't connect to the server. Can't check the credentials.

        // Perform the login process.
        try {
            int reply; // Temp value for server reply code.

            // Try to login.
            if (!this.FTPClient.login(this.userID, this.password)) {
                // If couldn't login, we should logout and return failure.
                this.FTPClient.logout();
                return false;
            }

            // Try to set filetype and jesjobname.
            if (!this.FTPClient.doCommand("site filetype=jes jesjobname=*", "")) {
                this.FTPClient.disconnect();
                System.err.println("Couldn't set FileType and JESJobName");
                return false;
            }
            // Check reply.
            reply = this.FTPClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.FTPClient.disconnect();
                System.err.println("FTP server refused to change FileType and JESJobName.");
                return false;
            }
        } catch (IOException e) {
            if (this.FTPClient.isConnected()) {
                try {
                    this.FTPClient.disconnect();
                } catch (IOException f) {
                    // do nothing
                }
            }
            System.err.println("Could not connect to server.");
            e.printStackTrace();
            return false;
        }

        // If go here, everything went fine.
        return true;
    }

    /**
     * Submit job for execution.
     *
     * @param inputStream JCL text of the job.
     * @param wait Whether we need for the job to complete.
     * @param waitTime Maximum wait time in minutes. If set to <code>0</code>, will wait forever.
     * @param outputStream Stream to put job log. Can be <code>Null</code>.
     * @param deleteLogFromSpool Whether the job log should be deleted fro spool upon job end.
     *
     * @return Whether the job was successfully submitted and the job log was fetched.
     * <br><b><code>jobCC</code></b> holds the response of the operation (including errors).
     *
     * @see zFTPConnector#connect()
     * @see zFTPConnector#logon()
     * @see zFTPConnector#waitForCompletion(OutputStream)
     * @see zFTPConnector#deleteJobLog()
     */
    public boolean submit(InputStream inputStream, boolean wait, int waitTime, OutputStream outputStream, boolean deleteLogFromSpool)
    {
        this.waitTime = ((long)waitTime) * 60 * 1000; // Minutes to milliseconds.

        // Verify connection.
        if(!this.FTPClient.isConnected())
            if(!this.logon())
            {
                this.jobCC = "COULD_NOT_CONNECT";
                return false;
            }

        this.FTPClient.enterLocalPassiveMode();

        try
        {
            // Submit the job.
            this.FTPClient.storeFile("jenkins.sub", inputStream);

            // Scan reply from server to get JobID.
            for (String s : this.FTPClient.getReplyStrings()) {
                Matcher matcher = JesJobName.matcher(s);
                if(matcher.matches())
                {
                    // Set jobID
                    this.jobID = matcher.group(1);
                    break;
                }
            }
            inputStream.close();
        }
        catch (FTPConnectionClosedException e)
        {
            System.err.println("Server closed connection.");
            e.printStackTrace();
            this.jobCC = "SERVER_CLOSED_CONNECTION";
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            this.jobCC = "IO_ERROR";
            return false;
        }

        if (wait)
        {
            // Wait for completion.
            if(this.waitForCompletion(outputStream))
            {
                if (deleteLogFromSpool)
                    // Delete job log from spool.
                    this.deleteJobLog();
                return true;
            }
            else
            {
                if (this.jobCC == null)
                    this.jobCC = "JOB_DID_NOT_FINISH_IN_TIME";
                return false;
            }
        }

        // If we are here, everything went fine.
        return true;
    }

    /**
     * Wait for he completion of the job.
     *
     * @param outputStream Stream to hold job log.
     *
     * @return Whether the job finished in time.
     *
     * @see zFTPConnector#submit(InputStream, boolean, int, OutputStream, boolean)
     * @see zFTPConnector#fetchJobLog(OutputStream)
     */
    private boolean waitForCompletion(OutputStream outputStream)
    {
        // Initialize current time and estimated time.
        long curr = System.currentTimeMillis();
        long jobEndTime = System.currentTimeMillis() + this.waitTime;
        boolean eternal = (waitTime == 0);

        // Perform wait
        while (eternal || (curr <= jobEndTime))
        {
            // Try to fetch job log.
            if (this.fetchJobLog(outputStream))
                return true;
            else
            {
                // Couldn't fetch the job log. Need to wait.
                try
                {
                    Thread.sleep(waitInterval);
                    curr = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    System.err.println("Interrupted.");
                    this.jobCC = "WAIT_INTERRUPTED";
                    return false;
                }
            }
        }

        // Exit with wait error.
        this.jobCC = "WAIT_ERROR";
        return false;
    }

    /**
     * Fetch job log from spool.
     *
     * @param outputStream Stream to hold the job log.
     *
     * @return Whether the job log was fetched from the LPAR.
     *
     * @see zFTPConnector#waitForCompletion(OutputStream)
     */
    private boolean fetchJobLog(OutputStream outputStream)
    {
        // Initialize temp variables.
        InputStreamReader tempInputStreamReader = null;
        OutputStreamWriter tempOutputStreamWriter = null;
        BufferedReader tempReader = null;
        BufferedWriter tempWriter = null;

        // Verify connection.
        if(!this.FTPClient.isConnected())
            if(!this.logon())
            {
                this.jobCC = "FETCH_LOG_ERROR_LOGIN";
                return false;
            }

        this.FTPClient.enterLocalPassiveMode();

        // Try fetching.
        try
        {
            // Temp variables.
            int reply;
            boolean foundRC = false;
            File tempFile;

            // Create temp file to hold job log. Need this to scan for MaxCC.
            try
            {
                tempFile = File.createTempFile("Jenkins", "tmp");
                tempFile.deleteOnExit();
            }
            catch(Exception e){
                // if any error occurs
                e.printStackTrace();
                this.jobCC = "ERROR_CREATING_TEMP_FILE";
                return false;
            }
            FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile,false);

            // Try fetching the log.
            if(!this.FTPClient.retrieveFile(this.jobID,tempFileOutputStream))
            {
                this.jobCC = "RETR_ERR_JOB_NOT_FINISHED_OR_NOT_FOUND";
                return false;
            }

            Pattern JobRC = Pattern.compile(".*?\\d{2}\\.\\d{2}\\.\\d{2} "+jobID+"  .{8} RC (.*?) ET .*");
            reply = this.FTPClient.getReplyCode();

            if (FTPReply.isPositiveCompletion(reply))
            {
                // If job hasn't finished we need to exit.
                for (String s : this.FTPClient.getReplyStrings()) {
                    Matcher matcher = JobNotFinished.matcher(s);
                    if (matcher.matches()) {
                        this.jobCC = "JOB_NOT_FINISHED_OR_NOT_FOUND";
                        return false;
                    }
                }

                // Prepare to scan for MaxCC and copy job log.
                String tempLine;
                FileInputStream tempInpStream = new FileInputStream(tempFile);
                tempInputStreamReader = new InputStreamReader(tempInpStream);
                tempReader = new BufferedReader(tempInputStreamReader);

                if(outputStream != null)
                {
                    tempOutputStreamWriter = new OutputStreamWriter(outputStream);
                    tempWriter = new BufferedWriter(tempOutputStreamWriter);
                }

                // Scan
                while ((tempLine = tempReader.readLine()) != null)
                {
                    // Chack line
                    if(!foundRC)
                    {
                        Matcher matcher = JobRC.matcher(tempLine);
                        if (matcher.matches()) {
                            jobCC = matcher.group(1);
                            foundRC = true;
                        }
                    }

                    // If need output - copy the line.
                    if(outputStream != null)
                    {
                        tempWriter.write(tempLine);
                        tempWriter.newLine();
                    }
                }

                // Close everything.
                tempInputStreamReader.close();
                tempReader.close();
                if(tempWriter != null)
                    tempWriter.close();
                if(tempOutputStreamWriter != null)
                    tempOutputStreamWriter.close();
                if(outputStream != null)
                    outputStream.close();

                // Finish with success.
                return true;
            }

            // Close everything and return failure.
            if(outputStream != null)
                outputStream.close();
            this.jobCC = "FETCH_LOG_FETCH_ERROR";
            return false;
        }
        catch (IOException e)
        {
            try {
                if(tempInputStreamReader != null)
                    tempInputStreamReader.close();
                if(tempReader != null)
                    tempReader.close();
                if(tempWriter != null)
                    tempWriter.close();
                if(tempOutputStreamWriter != null)
                    tempOutputStreamWriter.close();
                if(outputStream != null)
                    outputStream.close();
            }
            catch (IOException ignored)
            {}
            this.jobCC = "FETCH_LOG_IO_ERROR";
            return false;
        }
    }

    /**
     * Delete job log from spool. Job is distinguished by previously obtained <code>jobID</code>.
     *
     * @see zFTPConnector#submit(InputStream, boolean, int, OutputStream, boolean)
     */
    private void deleteJobLog ()
    {
        // Verify connection.
        if(!this.FTPClient.isConnected())
            if(!this.logon())
            {
                return;
            }

        this.FTPClient.enterLocalPassiveMode();

        // Delete log.
        try
        {
            this.FTPClient.deleteFile(this.jobID);
        }
        catch (IOException e)
        {
            // Do nothing.
        }
    }

    /**
     * Get JobID.
     *
     * @return Current <b><code>jobID</code></b>.
     */
    public String getJobID() {
        return this.jobID;
    }
    /**
     * Get JobCC.
     *
     * @return Current <b><code>jobCC</code></b>.
     */
    public String getJobCC() {
        return this.jobCC;
    }
}

