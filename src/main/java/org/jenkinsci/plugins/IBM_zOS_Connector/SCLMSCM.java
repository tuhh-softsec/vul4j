package org.jenkinsci.plugins.IBM_zOS_Connector;

import com.google.common.base.Joiner;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;

/**
 * <h1>SCLMSCM</h1>
 * Class implementing SCM functionality for SCLM.s
 *
 * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
 *
 * @version 1.0
 */
public class SCLMSCM  extends SCM {
    /**
     * LPAR name or IP address.
     */
    private String server = "server";
    /**
     * FTP port to connect to.
     */
    private int port;
    /**
     * User ID.
     */
    private String userID;
    /**
     * User password.
     */
    private String password;

    // SCLM project information (project, alternate, group, types to monitor)
    /**
     * SCLM Project Name.
     */
    private String project;
    /**
     * SCLM Alternate Project Definition.
     */
    private String alternate;
    /**
     * SCLM Group.
     */
    private String group;
    /**
     * SCLM file types under interest.
     */
    private LinkedList<String> types;
    /**
     * Step for invoking FLMCMD.
     */
    private String JobStep = SCLMSCMDescriptor.SCLMJobHeader;
    /**
     * Job header.
     */
    private String JobHeader = SCLMSCMDescriptor.SCLMJobHeader;
    /**
     * Use custom job header.
     */
    private boolean custJobHeader = false;
    /**
     * Use custom FLMCMD job step.
     */
    private boolean custJobStep = false;
    /**
     * Current revision state.
     */
    private SCLMSCMRevisionState currentRevision;

    /**
     * Constructor that is invoked from project configuration page.
     *
     * @param server LPAR name of IP address.
     * @param port FTP port to connect to.
     * @param userID User ID.
     * @param password User password.
     * @param project SCLM Project Name.
     * @param alternate SCLM Alternate Project Definition.
     * @param group SCLM Group.
     * @param types Types under interest (separated by comma).
     * @param custJobStep Whether user defines own FLMCMD job step.
     * @param JobStep User-supplies FLMCMD job step.
     * @param custJobHeader Whether user supplied own job header.
     * @param JobHeader User-supplied job header.
     */
    @DataBoundConstructor
    public SCLMSCM(String server,
                   int port,
                   String userID,
                   String password,
                   String project,
                   String alternate,
                   String group,
                   String types,
                   boolean custJobStep,
                   String JobStep,
                   boolean custJobHeader,
                   String JobHeader)
    {
        this.server = server.replaceAll("\\s", "");
        this.port = port;
        this.userID = userID.replaceAll("\\s","");
        this.password = password.replaceAll("\\s","");

        this.project = project.replaceAll("\\s","");
        this.alternate = alternate.replaceAll("\\s","");
        this.group = group.replaceAll("\\s","");

        this.types = new LinkedList<String>();
        for(String temp: types.split(","))
        {
            temp = temp.replaceAll("\\s", "");
            if(!temp.isEmpty())
                this.types.add(temp);
        }
        this.custJobStep = custJobStep;
        if(this.custJobStep)
        {
            this.JobStep = JobStep;
        }
        else
        {
            this.JobStep = SCLMSCMDescriptor.SCLMJobStep;
        }
        this.custJobHeader = custJobHeader;
        if(this.custJobHeader)
        {
            this.JobHeader = JobHeader;
        }
        else
        {
            this.JobHeader = SCLMSCMDescriptor.SCLMJobHeader;
        }
    }

    /**
     * Dummy constructor
     */
    public  SCLMSCM() {
        this.custJobHeader = false;
        this.custJobStep = false;
        this.JobStep = SCLMSCMDescriptor.SCLMJobStep;
        this.JobHeader = SCLMSCMDescriptor.SCLMJobHeader;
    }

    /**
     * Get custJobHeader.
     *
     * @return <b><code>custJobHeader</code></b>
     */
    public boolean getCustJobHeader()
    {
        return this.custJobHeader;
    }

    /**
     * Get custJobStep.
     *
     * @return <b><code>custJobStep</code></b>
     */
    public boolean  getCustJobStep()
    {
        return this.custJobStep;
    }

    /**
     * Get LPAR name or IP address.
     *
     * @return <b><code>server</code></b>
     */
    public String   getServer() {
        return this.server;
    }

    /**
     * Get FTP port to connect to.
     *
     * @return <b><code>port</code></b>
     */
    public int      getPort() {
        return this.port;
    }

    /**
     * Get User ID.
     *
     * @return <b><code>userID</code></b>
     */
    public String   getUserID() {
        return this.userID;
    }

    /**
     * Get User password.
     *
     * @return <b><code>password</code></b>
     */
    public String   getPassword() {
        return this.password;
    }

    /**
     * Get SCLM Project Name.
     *
     * @return <b><code>project</code></b>
     */
    public String   getProject() {
        return this.project;
    }

    /**
     * Get SCM Alternate Project Definition.
     *
     * @return <b><code>alternate</code></b>
     */
    public String   getAlternate() {
        return this.alternate;
    }

    /**
     * Get SCLM Group.
     *
     * @return <b><code>group</code></b>
     */
    public String   getGroup() {
        return this.group;
    }

    /**
     * Get Job header.
     *
     * @return <b><code>JobHeader</code></b>
     */
    public String   getJobHeader()
    {
        return this.JobHeader;
    }

    /**
     * Get Job step.
     *
     * @return <b><code>JobStep</code></b>
     */
    public String   getJobStep ()
    {
        return this.JobStep;
    }

    /**
     * Get SCLM file types under interest.
     *
     * @return <b><code>types</code></b>
     */
    public String   getTypes() {
        return Joiner.on(",").join(this.types);
    }

    /**
     * Fetch new remote revision.
     *
     * @param baseline Last revision.
     *
     * @return New remote revision.
     *
     * @see zFTPConnector
     */
    private SCLMSCMRevisionState getNewRevision(SCLMSCMRevisionState baseline)
    {
        // Construct connector.
        zFTPConnector zFTPConnector = new zFTPConnector(this.server,this.port,this.userID,this.password);

        // Fetch revision.
        return new SCLMSCMRevisionState(this.project, this.alternate, this.group,this.types,this.JobHeader + "\n" + this.JobStep, zFTPConnector, baseline);
    }

    /**
     * Whether SCM supports polling.
     *
     * @return <code>true</code>
     */
    @Override
    public boolean supportsPolling() {
        return true;
    }

    /**
     * Whether SCM requires workspace for polling process.
     *
     * @return <code>false</code>
     */
    @Override
    public boolean requiresWorkspaceForPolling() {
        return true;
    }

    /**
     * Compare remote revision with old one.
     *
     * @param project Current project.
     * @param launcher Current launcher.
     * @param workspace Current workspace.
     * @param listener Current listener.
     * @param _baseline Old revision.
     *
     * @return PollingResult with comparison.
     *
     * @throws IOException
     * @throws InterruptedException
     *
     * @see PollingResult
     * @see SCLMSCMRevisionState
     * @see SCLMSCM#getNewRevision(SCLMSCMRevisionState)
     */
    @Override
    public PollingResult compareRemoteRevisionWith(@Nonnull Job<?,?> project, Launcher launcher, FilePath workspace, @Nonnull TaskListener listener, @Nonnull SCMRevisionState _baseline) throws IOException, InterruptedException
    {
        // Get new revision.
        SCLMSCMRevisionState baseline = (SCLMSCMRevisionState)_baseline;
        SCLMSCMRevisionState tempRevision = this.getNewRevision(baseline);

        // Compare cached state with latest polled state.
        boolean changes = !tempRevision.getChangedOnly().isEmpty();

        // Return a PollingResult to tell Jenkins whether to checkout and build or not.
        return new PollingResult(baseline, tempRevision, changes ? PollingResult.Change.SIGNIFICANT : PollingResult.Change.NONE);
    }

    /**
     * Checkout remote changes to the workspace.
     * <br>As the build itself is performed via SCLM, the checkout's main task is generation of revision.
     *
     * @param build Current build.
     * @param launcher Current launcher.
     * @param workspace Current workspace.
     * @param listener Current listener.
     * @param changelogFile Current changeLogFile.
     * @param baseline Last revision.
     *
     * @throws IOException
     * @throws InterruptedException
     *
     * @see SCLMSCMRevisionState
     * @see SCLMSCM#getNewRevision(SCLMSCMRevisionState)
     */
    @Override
    public void checkout(@Nonnull Run<?, ?> build, @Nonnull Launcher launcher, @Nonnull FilePath workspace, @Nonnull TaskListener listener, File changelogFile, SCMRevisionState baseline) throws IOException, InterruptedException
    {
        // Get new revision.
        this.currentRevision = this.getNewRevision((SCLMSCMRevisionState)baseline);
        if(changelogFile != null) {
            // Need to write changelog.xml.
            // Narrow file list and write it.
            List<SCLMFileState> temp = this.currentRevision.getChangedOnly();
            if (!temp.isEmpty())
            {
                Collections.sort(temp, SCLMFileState.ChangeComparator);

                PrintWriter writer = new PrintWriter(new FileWriter(changelogFile));
                writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                writer.println("<changelog>");
                for (SCLMFileState file : temp) {
                    String editType;
                    if(file.editType == null) {
                        editType = "SAME";
                    } else {
                        if (file.editType == EditType.EDIT) {
                            editType = "EDIT";
                        } else {
                            if (file.editType == EditType.DELETE) {
                                editType = "DELETE";
                            } else {
                                editType = "ADD";
                            }
                        }
                    }
                    writer.println("\t<entry>");
                    writer.println(String.format("\t\t<date>%s</date>", SCLMFileState.DateFormat.format(file.changeDate)));
                    writer.println(String.format("\t\t<project>%s</project>", file.project));
                    writer.println(String.format("\t\t<alternate>%s</alternate>", file.alternate));
                    writer.println(String.format("\t\t<group>%s</group>", file.group));
                    writer.println(String.format("\t\t<type>%s</type>", file.type));
                    writer.println(String.format("\t\t<name>%s</name>", file.name));
                    writer.println(String.format("\t\t<version>%d</version>", file.version));
                    writer.println(String.format("\t\t<userID>%s</userID>", file.changeUserID));
                    writer.println(String.format("\t\t<changeGroup>%s</changeGroup>", file.changeGroup));
                    writer.println(String.format("\t\t<editType>%s</editType>", editType));
                    writer.println("\t</entry>");
                }
                writer.println("</changelog>");
                writer.close();
	            build.doArtifact();
            } else
            {
                this.createEmptyChangeLog(changelogFile,listener,"changelog");
            }
        }
    }

    /**
     * Calculate revision from build. Dummy.
     *
     * @param build Current build.
     * @param workspace Current workspace.
     * @param launcher Current launcher.
     * @param listener Current listener.
     *
     * @return Actual revision.
     *
     * @throws IOException
     * @throws InterruptedException
     *
     * @see SCLMSCMRevisionState
     */
    @Override
    public SCMRevisionState calcRevisionsFromBuild(@Nonnull Run<?,?> build, FilePath workspace, Launcher launcher, @Nonnull TaskListener listener) throws IOException, InterruptedException
    {
        // Remove 'DELETED' files from revision.
        this.currentRevision.removeDeleted();
        return this.currentRevision;
    }

    /**
     * Get parser for changelog.xml
     * .
     * @return SCLMChangeLogParser instance.
     */
    @Override
    public ChangeLogParser createChangeLogParser() {
        return new SCLMChangeLogParser();
    }

    /**
     * Get descriptor.
     *
     * @return Descriptor for SCLMSCM.
     */
    @Override
    public SCLMSCMDescriptor getDescriptor() {
        return (SCLMSCMDescriptor)super.getDescriptor();
    }

    /**
     * <h1>SCLMSCMDescriptor</h1>
     * Descriptor for SCLMSCM.
     *
     * @author Alexander Shcherbakov (candiduslynx@gmail.com)
     * @version 1.0
     */
    @Extension
    public static final class SCLMSCMDescriptor extends SCMDescriptor<SCLMSCM> {
        /**
         * Default job header.
         */
        private static final String DefaultSCLMJobHeader =
                "//JENKINS  JOB (ACCOUNT),'JENKINS',                             \n" +
                "// MSGCLASS=A,CLASS=A,NOTIFY=&SYSUID";
        /**
         * Default step for FLMCMD invocation.
         */
        private static final String DefaultSCLMJobStep =
                "//SCLMEX   EXEC PGM=IKJEFT01,REGION=4096K,TIME=1439,DYNAMNBR=200\n" +
                "//STEPLIB  DD DSN=ISP.SISPLPA,DISP=SHR                          \n" +
                "//         DD DSN=ISP.SISPLOAD,DISP=SHR                         \n" +
                "//ISPMLIB  DD DSN=ISP.SISPMENU,DISP=SHR                         \n" +
                "//ISPSLIB  DD DSN=ISP.SISPSENU,DISP=SHR                         \n" +
                "//         DD DSN=ISP.SISPSLIB,DISP=SHR                         \n" +
                "//ISPPLIB  DD DSN=ISP.SISPPENU,DISP=SHR                         \n" +
                "//ISPTLIB  DD UNIT=@TEMP0,DISP=(NEW,PASS),SPACE=(CYL,(1,1,5)),  \n" +
                "//            DCB=(LRECL=80,BLKSIZE=19040,DSORG=PO,RECFM=FB),   \n" +
                "//            DSN=                                              \n" +
                "//         DD DSN=ISP.SISPTENU,DISP=SHR                         \n" +
                "//ISPTABL  DD UNIT=@TEMP0,DISP=(NEW,PASS),SPACE=(CYL,(1,1,5)),  \n" +
                "//            DCB=(LRECL=80,BLKSIZE=19040,DSORG=PO,RECFM=FB),   \n" +
                "//            DSN=                                              \n" +
                "//ISPPROF  DD UNIT=@TEMP0,DISP=(NEW,PASS),SPACE=(CYL,(1,1,5)),  \n" +
                "//            DCB=(LRECL=80,BLKSIZE=19040,DSORG=PO,RECFM=FB),   \n" +
                "//            DSN=                                              \n" +
                "//ISPLOG   DD SYSOUT=*,                                         \n" +
                "//            DCB=(LRECL=120,BLKSIZE=2400,DSORG=PS,RECFM=FB)    \n" +
                "//ISPCTL1  DD DISP=NEW,UNIT=@TEMP0,SPACE=(CYL,(1,1)),           \n" +
                "//            DCB=(LRECL=80,BLKSIZE=800,RECFM=FB)               \n" +
                "//SYSTERM  DD SYSOUT=*                                          \n" +
                "//SYSPROC  DD DSN=ISP.SISPCLIB,DISP=SHR                         \n" +
                "//FLMMSGS  DD SYSOUT=(*)                                        \n" +
                "//PASCERR  DD SYSOUT=(*)                                        \n" +
                "//ZFLMDD   DD  *                                                \n" +
                "   ZFLMNLST=FLMNLENU    ZFLMTRMT=ISR3278    ZDATEF=YY/MM/DD     \n" +
                "/*                                                              \n" +
                "//SYSPRINT DD SYSOUT=(*)                                        \n" +
                "//SYSTSPRT DD SYSOUT=(*)";
        /**
         * Globally configured default job header.
         */
        private static String SCLMJobHeader = DefaultSCLMJobHeader;
        /**
         * Globally configured FLMCMD job step.
         */
        private static String SCLMJobStep = DefaultSCLMJobStep;

        /**
         * Dummy constructor.
         */
        public SCLMSCMDescriptor() {
            super(SCLMSCM.class, null);
        }

        /**
         * Get globally configured job header.
         *
         * @return <b><code>SCLMJobHeader</code></b>.
         */
        public String getSCLMJobHeader() {
            if (SCLMJobHeader.isEmpty())
                SCLMJobHeader = SCLMSCMDescriptor.DefaultSCLMJobHeader;
            return SCLMJobHeader;
        }

        /**
         * Get globally configured FLMCMD job step.
         *
         * @return <b><code>SCLMJobStep</code></b>.
         */
        public String getSCLMJobStep()
        {
            if(SCLMJobStep.isEmpty())
                SCLMJobStep = SCLMSCMDescriptor.DefaultSCLMJobStep;
            return SCLMJobStep;
        }

        /**
         * Get printable name.
         *
         * @return "SCLM".
         */
        @Override
        public String getDisplayName() {
            return "SCLM";
        }

        /**
         * Configure action that is invoked from global settings.
         *
         * @param req Request.
         * @param json Parameters.
         *
         * @return Whether everything was setup OK.
         *
         * @throws FormException
         */
        @Override
        public boolean configure(org.kohsuke.stapler.StaplerRequest req,
                                 net.sf.json.JSONObject json)
            throws FormException
        {
            SCLMJobHeader = Util.fixEmpty(req.getParameter("SCLMJobHeader").trim());
            if (SCLMJobHeader == null)
                SCLMJobHeader = SCLMSCMDescriptor.DefaultSCLMJobHeader;
            SCLMJobStep = Util.fixEmpty(req.getParameter("SCLMJobStep").trim());
            if (SCLMJobStep == null)
                SCLMJobStep = SCLMSCMDescriptor.DefaultSCLMJobStep;
            save();
            return true;
        }
    }
}
