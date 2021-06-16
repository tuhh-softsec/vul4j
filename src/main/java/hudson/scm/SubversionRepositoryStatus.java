package hudson.scm;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.WARNING;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractModelObject;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.scm.SubversionSCM.ModuleLocation;
import hudson.scm.SubversionSCM.SvnInfo;
import hudson.triggers.SCMTrigger;
import hudson.util.QueryParameterMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import jenkins.model.Jenkins;
import jenkins.triggers.SCMTriggerItem;
import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.SVNException;

/**
 * Per repository status.
 * <p>
 * Receives post-commit hook notifications.
 *
 * @author Kohsuke Kawaguchi
 * @see SubversionStatus
 */
public class SubversionRepositoryStatus extends AbstractModelObject {
    public final UUID uuid;

    public SubversionRepositoryStatus(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return uuid.toString();
    }

    public String getSearchUrl() {
        return uuid.toString();
    }
    
    static interface JobProvider {
        @SuppressWarnings("rawtypes")
        List<Job> getAllJobs();
    }

    /**
     * An extension point to allow things other than jobs to listen for repository status updates.
     */
    public static abstract class Listener implements ExtensionPoint {

        /**
         * Called when a post-commit hook notification has been received.
         * @param uuid the UUID of the repository against which the hook was received.
         * @param revision the revision (if known) or {@code -1} if unknown.
         * @return {@code true} if a match for the UUID was found and something was scheduled as a result.
         */
        public abstract boolean onNotify(UUID uuid, long revision, Set<String> affectedPaths);
    }
    
    private static Method IS_IGNORE_POST_COMMIT_HOOKS_METHOD;
    
    /**
     * Notify the commit to this repository.
     *
     * <p>
     * Because this URL is not guarded, we can't really trust the data that's sent to us. But we intentionally
     * don't protect this URL to simplify <tt>post-commit</tt> script set up.
     */
    public void doNotifyCommit(StaplerRequest req, StaplerResponse rsp) throws ServletException, IOException {
        requirePOST();

        // compute the affected paths
        Set<String> affectedPath = new HashSet<String>();
        String line;
        BufferedReader r = new BufferedReader(req.getReader());
        
        try {
	        while((line=r.readLine())!=null) {
	        	if (LOGGER.isLoggable(FINER)) {
	        		LOGGER.finer("Reading line: "+line);
	        	}
	            affectedPath.add(line.substring(4));
	            if (line.startsWith("svnlook changed --revision ")) {
	                String msg = "Expecting the output from the svnlook command but instead you just sent me the svnlook invocation command line: " + line;
	                LOGGER.warning(msg);
	                throw new IllegalArgumentException(msg);
	            }
	        }
        } finally {
        	IOUtils.closeQuietly(r);
        }

        if(LOGGER.isLoggable(FINE))
            LOGGER.fine("Change reported to Subversion repository "+uuid+" on "+affectedPath);

        // we can't reliably use req.getParameter() as it can try to parse the payload, which we've already consumed above.
        // servlet container relies on Content-type to decide if it wants to parse the payload or not, and at least
        // in case of Jetty, it doesn't check if the payload is
        QueryParameterMap query = new QueryParameterMap(req);
        String revParam = query.get("rev");
        if (revParam == null) {
            revParam = req.getHeader("X-Hudson-Subversion-Revision");
        }

        long rev = -1;
        if (revParam != null) {
            rev = Long.parseLong(revParam);
        }

        boolean listenerDidSomething = false;
        for (Listener listener : ExtensionList.lookup(Listener.class)) {
            try {
                if (listener.onNotify(uuid, rev, affectedPath)) {
                    listenerDidSomething = true;
                }
            } catch (Throwable t) {
                LOGGER.log(WARNING, "Listener " + listener.getClass().getName() + " threw an uncaught exception", t);
            }
        }

        if (!listenerDidSomething) LOGGER.log(Level.WARNING, "No interest in change to repository UUID {0} found", uuid);

        rsp.setStatus(SC_OK);
    }

    private static class SubversionRepoUUIDAndRootPath {
        public final UUID uuid;
        public final String rootPath;

        public SubversionRepoUUIDAndRootPath(UUID uuid, String rootPath) {
            this.uuid = uuid;
            this.rootPath = rootPath;
        }
    }

    @Extension
    public static class JobTriggerListenerImpl extends Listener {

        private Map<String, UUID> remoteUUIDCache = new HashMap<String, UUID>();

        private JobProvider jobProvider = new JobProvider() {
            @SuppressWarnings("rawtypes")
            public List<Job> getAllJobs() {
                return Jenkins.getInstance().getAllItems(Job.class);
            }
        };

        // for tests
        void setJobProvider(JobProvider jobProvider) {
            this.jobProvider = jobProvider;
        }

        private SubversionRepoUUIDAndRootPath remoteUUIDAndRootPathFromCacheOrFromSVN(Job job, SCM scm, ModuleLocation moduleLocation, String urlFromConfiguration) throws SVNException {
            SubversionRepoUUIDAndRootPath uuidAndRootPath = null;
            for (Map.Entry<String, UUID> e : remoteUUIDCache.entrySet()) {
                String remoteRepoRootURL = e.getKey();
                String remoteRepoRootURLWithSlash = remoteRepoRootURL + "/";
                if (urlFromConfiguration.startsWith(remoteRepoRootURLWithSlash) || urlFromConfiguration.equals(remoteRepoRootURL)) {
                    UUID uuid = e.getValue();
                    String rootPath = SVNURL.parseURIDecoded(e.getKey()).getPath();
                    uuidAndRootPath = new SubversionRepoUUIDAndRootPath(uuid, rootPath);

                    LOGGER.finer("Using cached uuid for module location " + urlFromConfiguration + " of job "+ job);
                    break;
                }
            }

            if (uuidAndRootPath == null) {
                if (LOGGER.isLoggable(FINER)) {
                    LOGGER.finer("Could not find " + urlFromConfiguration + " in " + remoteUUIDCache.keySet());
                }
                UUID remoteUUID = moduleLocation.getUUID(job, scm);
                SVNURL repositoryRoot = moduleLocation.getRepositoryRoot(job, scm);
                remoteUUIDCache.put(repositoryRoot.toString(), remoteUUID);
                uuidAndRootPath = new SubversionRepoUUIDAndRootPath(remoteUUID, repositoryRoot.getPath());
            }
            return uuidAndRootPath;
        }

        boolean doModuleLocationHasAPathFromAffectedPath(String configuredRepoFullPath, String rootRepoPath, Set<String> affectedPath) {
            boolean containsAnAffectedPath = false;
            if (configuredRepoFullPath.startsWith(rootRepoPath)) {
                String remainingRepoPath = configuredRepoFullPath.substring(rootRepoPath.length());
                if (remainingRepoPath.startsWith("/")) remainingRepoPath=remainingRepoPath.substring(1);
                String remainingRepoPathSlash = remainingRepoPath + '/';

                for (String path : affectedPath) {
                    if (path.equals(remainingRepoPath) /*for files*/ || 
                            path.startsWith(remainingRepoPathSlash) /*for dirs*/ ||
                            remainingRepoPath.length() == 0 /*when someone is checking out the whole repo (that is, configuredRepoFullPath==rootRepoPath)*/) {
                        // this project is possibly changed. poll now.
                        // if any of the data we used was bogus, the trigger will not detect a change
                        containsAnAffectedPath = true;
                        break;
                    }
                }
            }
            return containsAnAffectedPath;
        }

        private void scheduleImediatePollingOfJob(Job job, SCMTrigger trigger, List<SvnInfo> infos) {
            LOGGER.fine("Scheduling the immediate polling of " + job);

            final RevisionParameterAction[] actions;
            if (infos.isEmpty()) {
                actions = new RevisionParameterAction[0];
            } else {
                actions = new RevisionParameterAction[] { new RevisionParameterAction(infos) };
            }

            trigger.run(actions);
        }

        @Override
        public boolean onNotify(UUID uuid, long rev, Set<String> affectedPath) {
            boolean scmFound = false, triggerFound = false, uuidFound = false, pathFound = false;
            LOGGER.fine("Starting subversion locations checks for all jobs");
            for (Job p : this.jobProvider.getAllJobs()) {
                SCMTriggerItem scmTriggerItem = SCMTriggerItem.SCMTriggerItems.asSCMTriggerItem(p);
                if (scmTriggerItem == null) {
                    continue;
                }
                if (p instanceof AbstractProject && ((AbstractProject) p).isDisabled()) {
                    continue;
                }
                String jobName = p.getName();
                SCMS: for (SCM scm : scmTriggerItem.getSCMs()) {
                    if (scm instanceof SubversionSCM) scmFound = true; else continue;

                    SCMTrigger trigger = scmTriggerItem.getSCMTrigger();
                    if (trigger!=null && !doesIgnorePostCommitHooks(trigger)) triggerFound = true; else continue;

                    SubversionSCM sscm = (SubversionSCM) scm;

                    List<SvnInfo> infos = new ArrayList<SvnInfo>();

                    try {
                        boolean projectMatches = false;
                        for (ModuleLocation loc : sscm.getProjectLocations(p)) {
                            String urlFromConfiguration = loc.getURL();
                            //LOGGER.log(WARNING, "Checking uuid for module location + " + loc + " of job "+ p + " (urlFromConfiguration : " + urlFromConfiguration + ")");
                        
                            try {
                                SubversionRepoUUIDAndRootPath uuidAndRootPath = this.remoteUUIDAndRootPathFromCacheOrFromSVN(p, sscm, loc, urlFromConfiguration);
                                UUID remoteUUID = uuidAndRootPath.uuid;
                                if (remoteUUID.equals(uuid)) uuidFound = true; else continue;

                                String configuredRepoFullPath = loc.getSVNURL().getPath();
                                String rootRepoPath = uuidAndRootPath.rootPath;
                                if (this.doModuleLocationHasAPathFromAffectedPath(configuredRepoFullPath, rootRepoPath, affectedPath)) {
                                    projectMatches = true;
                                    pathFound = true;
                                }

                                if ( rev != -1 ) {
                                    infos.add(new SvnInfo(loc.getURL(), rev));
                                }
                            } catch (SVNCancelException e) {
                                LOGGER.log(WARNING, "Failed to handle Subversion commit notification (was trying to access " + urlFromConfiguration + " of job " + jobName + "). If you are using svn:externals feature ensure that the credentials of the externals are added on the Additional Credentials field", e);
                            } catch (SVNException e) {
                                LOGGER.log(WARNING, "Failed to handle Subversion commit notification (was trying to access " + urlFromConfiguration + " of job " + jobName + ")", e);
                            }
                            
                            if (projectMatches) {
                                this.scheduleImediatePollingOfJob(p, trigger, infos);
                                break SCMS;
                            }
                        }
                    } catch(IOException e) {
                        LOGGER.log(WARNING, "Failed to handle Subversion commit notification (getting module locations failed for job " + jobName + ")", e);
                    }
                }
            }
            LOGGER.fine("Ended subversion locations checks for all jobs");

            if (!scmFound)          LOGGER.warning("No subversion jobs found");
            else if (!triggerFound) LOGGER.warning("No subversion jobs using SCM polling or all jobs using SCM polling are ignoring post-commit hooks");
            else if (!uuidFound)    LOGGER.warning("No subversion jobs using repository: " + uuid);
            else if (!pathFound)    LOGGER.fine("No jobs found matching the modified files");

            return scmFound;
        }
    }
    
    private static boolean doesIgnorePostCommitHooks(SCMTrigger trigger) {
        if (IS_IGNORE_POST_COMMIT_HOOKS_METHOD == null)
            return false;
        
        try {
            return (Boolean)IS_IGNORE_POST_COMMIT_HOOKS_METHOD.invoke(trigger, (Object[])null);
        } catch (Exception e) {
            LOGGER.log(WARNING,"Failure when calling isIgnorePostCommitHooks",e);
            return false;
        }
    }

    static {
        try {
            IS_IGNORE_POST_COMMIT_HOOKS_METHOD = SCMTrigger.class.getMethod("isIgnorePostCommitHooks", (Class[])null);
        } catch (Exception e) {
            // we're running in an older Jenkins version which doesn't have this method
        }
    }

    private static final Logger LOGGER = Logger.getLogger(SubversionRepositoryStatus.class.getName());
}
