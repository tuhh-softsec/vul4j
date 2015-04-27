package org.jenkinsci.plugins.IBM_zOS_Connector;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.User;
import hudson.scm.ChangeLogSet;
import hudson.scm.EditType;
import hudson.scm.RepositoryBrowser;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.*;

/**
 * <h1>SCLMChangeLogSet</h1>
 *
 * ChangeLogSet for SCLMSCM.
 * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
 *
 * @version 1.0
 *
 * @see ChangeLogSet
 */
public class SCLMChangeLogSet extends ChangeLogSet<SCLMChangeLogSet.Entry> {
    /**
     * List of Change Entries.
     *
     * @see Entry
     */
    private LinkedList<Entry> items;

    /**
     * Dummy constructor.
     *
     * @param run Current Run.
     * @param browser Current Repository Browser.
     */
    protected SCLMChangeLogSet(Run<?,?> run, RepositoryBrowser<?> browser)
    {
        super(run,browser);
        this.items = new LinkedList<Entry>();
    }

    /**
     * Get SCM kind.
     * @return "SCLM".
     */
    @Exported
    public String getKind() {
        return "SCLM";
    }

    /**
     * Construct SCLMChangeLogSet from LogSet.
     * @param logSet LogSet.
     *
     * @see LogSet
     */
    public void fromLogSet (LogSet logSet)
    {
        // Init entries list.
        this.items = new LinkedList<Entry>();
        // Copy entries.
        if(logSet.entries != null) {
            for (LogSet.Entry ent : logSet.entries) {
                this.items.add(new Entry(ent));
            }
        }
        // Set parent.
        for (Entry e : this.items) {
            e.setParent(this);
        }
    }

    /**
     * Check if have no info.
     * @return Whether there is no info.
     */
    @Override
    public boolean isEmptySet() {
        return (this.items == null) || this.items.isEmpty();
    }

    /**
     * Iterator on entries.
     *
     * @return Iterator for entries (most recent come first).
     */
    public Iterator iterator() {
        Collections.sort(this.items, Entry.EntryComparator);
        return this.items.iterator();
    }

    /**
     * Get logs.
     *
     * @return Entries.
     */
    public LinkedList<Entry> getLogs() {
        return this.items;
    }

    /**
     * <h1>Entry</h1>
     *
     * Entry for SCLMChangeLogSet.
     *
     * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
     *
     * @version 1.0
     *
     * @see SCLMChangeLogSet
     * @see SCLMAffectedFile
     */
    @ExportedBean(defaultVisibility=999)
    public static class Entry extends ChangeLogSet.Entry {
        /**
         * Affected file.
         */
        private SCLMAffectedFile affectedFile;
        /**
         * Parent of the entry.
         */
        private SCLMChangeLogSet parent = null;

        /**
         * Dummy constructor.
         */
        public Entry() {
            this.affectedFile = new SCLMAffectedFile();
        }

        /**
         * Construct entry from LogSet.Entry.
         * @param e LogSet.Entry.
         */
        public Entry (LogSet.Entry e)
        {
            this.affectedFile = new SCLMAffectedFile(e.affectedFile);
        }

        /**
         * Set parent.
         * @param set New parent.
         */
        @Override
        public void setParent(ChangeLogSet set)
        {
            this.parent = (SCLMChangeLogSet) set;
        }

        /**
         * Get current parent.
         * @return Current parent.
         */
        @Override
        public SCLMChangeLogSet getParent()
        {
            return this.parent;
        }

        /**
         * Get entry message.
         * @return Message for entry.
         */
        @Override
        public String getMsg() {
            String editType;
            if(this.affectedFile.getEditType() == EditType.ADD) {
                editType = "ADD";
            } else {
                if(this.affectedFile.getEditType() == EditType.EDIT) {
                    editType = "EDIT";
                } else {
                    if(this.affectedFile.getEditType() == EditType.DELETE) {
                        editType = "DELETE";
                    } else {
                        editType = "ERROR?";
                    }
                }
            }
            return editType + ": " + this.affectedFile.getPath();
        }

        /**
         * Get EditType of the entry.
         * @return EditType.
         *
         * @see EditType
         */
        public EditType getEditType() {
            return this.affectedFile.getEditType();
        }

        /**
         * Get path of affectedFile.
         *
         * @return Affected file path.
         */
        public String getPath() {
            return this.affectedFile.getPath();
        }

        /**
         * Get affectedFile.
         *
         * @return AffectedFile.
         */
        public LinkedList<AffectedFile> getItems()
        {
            LinkedList<AffectedFile> res = new LinkedList<AffectedFile>();
            res.add(this.affectedFile);
            return res;
        }

        /**
         * Get author of the changes.
         *
         * @return Author of AffectedFile change.
         */
        @Override
        public User getAuthor() {
            return User.get(this.affectedFile.file.changeUserID);
        }

        /**
         * Get AffectedFile version.
         *
         * @return AffectedFile version.
         */
        @Exported
        public String getVersion()
        {
            return String.valueOf(this.affectedFile.file.version);
        }

        /**
         * Get the path for AffectedFile.
         *
         * @return AffectedFile path.
         */
        @Override
        public Collection<String> getAffectedPaths() {
            LinkedList<String> res = new LinkedList<String>();
            res.add(this.affectedFile.file.getPath());
            return res;
        }

        /**
         * Get change date.
         *
         * @return Date of change in AffectedFile.
         */
        public String getDate() {
            return SCLMFileState.DateFormat.format(this.affectedFile.file.changeDate);
        }

        /**
         * Get printable view.
         *
         * @return SCLMFileState.toString()
         */
        @Override
        public String toString() {
            return this.affectedFile.file.toString();
        }

        /**
         * Comparator for Entries. Based on SCLMFileState comparator.
         */
        public static Comparator<Entry> EntryComparator = new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                return SCLMAffectedFile.affectedFilesComparator.compare(o1.affectedFile, o2.affectedFile);
            }
        };

    }

    /**
     * <h1>SCLMAffectedFile</h1>
     *
     * Affected File info.
     *
     * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
     *
     * @version 1.0
     *
     * @see SCLMChangeLogSet
     * @see Entry
     */
    @Extension
    public static class SCLMAffectedFile implements AffectedFile
    {
        /**
         * SCLM file.
         */
        private SCLMFileState file;

        /**
         * Constructor based on LogSet.AffectedFile.
         *
         * @param file LogSet.AffectedFile to be copied.
         */
        public SCLMAffectedFile(LogSet.AffectedFile file)
        {
            this.file = file.file;
        }

        /**
         * Dummy constructor.
         */
        public SCLMAffectedFile()
        {
            this.file = new SCLMFileState();
        }

        /**
         * Get file path.
         *
         * @return File path.
         */
        public String getPath() {
            return this.file.getPath();
        }

        /**
         * Get EditType.
         * @return EditType of the file.
         */
        public EditType getEditType() {
            return this.file.editType;
        }

        /**
         * Comparator based on SCLMFileState comparator.
         */
        public static Comparator<SCLMAffectedFile> affectedFilesComparator = new Comparator<SCLMAffectedFile>() {
            @Override
            public int compare(SCLMAffectedFile o1, SCLMAffectedFile o2) {
                return SCLMFileState.ChangeComparator.compare(o1.file, o2.file);
            }
        };
    }
}
