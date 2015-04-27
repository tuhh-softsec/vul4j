package org.jenkinsci.plugins.IBM_zOS_Connector;

import hudson.scm.EditType;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

/**
 * <h1>LogSet</h1>
 *
 * Proxy for SCLMChangeLogSet.
 *
 * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
 *
 * @version 1.0
 *
 * @see SCLMChangeLogSet
 * @see LogSet.Entry
 * @see LogSet.AffectedFile
 */
public class LogSet {
    /**
     * Entries.
     */
    public LinkedList<Entry> entries;

    /**
     * Dummy constructor.
     */
    public LogSet() {
        this.entries = null;
    }

    /**
     * Add entry.
     * @param entry Entry to add.
     */
    public void addEntry(LogSet.Entry entry)
    {
        if (this.entries == null)
            this.entries = new LinkedList<Entry>();
        this.entries.add(entry);
    }

    /**
     * Get all entries.
     *
     * @return <b><code>entries</code></b>.
     */
    public List<LogSet.Entry> getEntries()
    {
        return this.entries;
    }

    /**
     * <h1>LogSet.Entry</h1>
     *
     * Entry for LogSet.
     *
     * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
     *
     * @version 1.0
     *
     * @see LogSet
     * @see hudson.scm.ChangeLogSet.Entry
     */
    public static class Entry {
        /**
         * Affected file.
         */
        public LogSet.AffectedFile affectedFile;

        /**
         * Dummy constructor.
         */
        public Entry() {
            this.affectedFile = new LogSet.AffectedFile();
        }

        /**
         * Set change date.
         *
         * @param changeDate Date.
         *
         * @throws ParseException
         */
        public void setChangeDate(String changeDate) throws ParseException {
            this.affectedFile.file.changeDate = SCLMFileState.DateFormat.parse(changeDate);
        }

        /**
         * Set project.
         * @param project SCLM Project Name.
         */
        public void setProject(String project) { this.affectedFile.file.project = project; }

        /**
         * Set Alternate.
         * @param alternate SCLM Project Alternate Definition.
         */
        public void setAlternate (String alternate) { this.affectedFile.file.alternate = alternate; }

        /**
         * Set Group.
         * @param group SCLM Group.
         */
        public void setGroup (String group) { this.affectedFile.file.group = group; }

        /**
         * Set type.
         * @param type SCLM Member Type.
         */
        public void setType (String type) {
            this.affectedFile.file.type = type;
        }

        /**
         * Set name.
         * @param name SCLM Member Name.
         */
        public void setName (String name) {
            this.affectedFile.file.name = name;
        }

        /**
         * Set version.
         * @param version SCLM Member Version.
         */
        public void setVersion (String version) {
            this.affectedFile.file.version = Long.parseLong(version);
        }

        /**
         * Set User ID.
         * @param userID z/OS User ID.
         */
        public void setUserID(String userID)
        {
            this.affectedFile.file.changeUserID = userID;
        }

        /**
         * Set Change group.
         * @param changeGroup SCLM Change Group.
         */
        public void setChangeGroup (String changeGroup) {
            this.affectedFile.file.changeGroup = changeGroup;
        }

        /**
         * Set EditType.
         * @param editType EditType.
         *
         * @see EditType
         */
        public void setEditType(String editType) {
            if(editType.equals("DELETE")) {
                this.affectedFile.file.editType = EditType.DELETE;
            } else {
                if(editType.equals("EDIT")) {
                    this.affectedFile.file.editType = EditType.EDIT;
                } else {
                    if(editType.equals("ADD")) {
                        this.affectedFile.file.editType = EditType.ADD;
                    } else {
                        this.affectedFile.file.editType = null;
                    }
                }
            }
        }
    }

    /**
     * <h1>LogSet.AffectedFile</h1>
     *
     * AffectedFile for LogSet.
     *
     * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
     *
     * @version 1.0
     *
     * @see LogSet
     * @see hudson.scm.ChangeLogSet.AffectedFile
     */
    public static class AffectedFile
    {
        /**
         * SCLMFileState to save state.
         */
        public SCLMFileState file;

        /**
         * Dummy constructor.
         */
        public AffectedFile() {
            this.file = new SCLMFileState();
        }
    }
}
