package org.jenkinsci.plugins.IBM_zOS_Connector;

import hudson.scm.EditType;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * <h1>SCLMFileState</h1>
 * Class describing file from SCLM.
 *
 * @author <a href="mailto:candiduslynx@gmail.com">Alexander Shcherbakov</a>
 *
 * @version 1.0
 */
public class SCLMFileState {
    // Fields constant over SCLMChangeLogSet.
    /**
     * SCLM Project Name. Equivalent to @@FLMPRJ.
     */
    public String project;
    /**
     * SCLM Alternate Project Definition. Equivalent to @@FLMALT.
     */
    public String alternate;
    /**
     * SCLM Group. Equivalent to @@FLMGRP.
     */
    public String group;

    // Vital fields
    /**
     * Member Type. Equivalent to @@FLMTYP.
     */
    public String type;
    /**
     * Member Name. Equivalent to @@FLMMBR.
     */
    public String name;
    /**
     * Member version. Equivalent to @@FLMMVR.
     */
    public long version;

    // Change information
    /**
     * Change Date and Time. Equivalent to combination of @@FLMCD4 and @@FLMCTM.
     */
    public Date changeDate;
    /**
     * Change User ID. Equivalent to @@FLMCUS.
     */
    public String changeUserID;
    /**
     * Change Group. Equivalent to @@FLMCLV.
     */
    public String changeGroup;

    /**
     * Edit Type (not regarded during comparison).
     */
    public EditType editType;

    /**
     * Format for date printing.
     */
    public final static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * Dummy constructor.
     */
    public SCLMFileState()
    {
        // Initialize fields.
        this.project = null;
        this.alternate = null;
        this.group = null;
        this.name = null;
        this.type = null;
        this.version = 0;
        this.changeDate = null;
        this.changeUserID = null;
        this.changeGroup = null;
    }

    /**
     * Full constructor.
     *
     * @param project SCLM Project Name.
     * @param alternate SCLM Alternate Project Definition.
     * @param group SCLM Group.
     * @param type SCLM file type.
     * @param name SCLM member name.
     * @param version SCLM member version.
     * @param changeDate Last Change Date.
     * @param changeUserID User ID of the last changer.
     * @param changeGroup SCLM Change Group.
     */
    public SCLMFileState (String project, String alternate, String group, String type, String name, long version, Date changeDate, String changeUserID, String changeGroup)
    {
        // Copy fields.
        this.project = project;
        this.alternate = alternate;
        this.group = group;
        this.name = name;
        this.type = type;
        this.version = version;
        this.changeDate = changeDate;
        this.changeUserID = changeUserID;
        this.changeGroup = changeGroup;
    }

    /**
     * This function is later used for removing duplicates.
     * <br>The order of comparison:
     * <br>1. Project
     * <br>2. Alternate
     * <br>3. Group
     * <br>4. Type
     * <br>5. Name
     * <br>6. Version
     * <br>7. Change Date and time
     * <br>8. Change UserID
     * <br>9. Change Group
     *
     * @param e the SCLMAffectedFile object we are comparing to.
     *
     * @return -1:0:1 depending on the compare result.
     */
    public int compareTo(SCLMFileState e) {
        int compareProject = this.project.compareTo(e.project);
        if(compareProject == 0) {
            int compareAlternate = this.alternate.compareTo(e.alternate);
            if(compareAlternate == 0) {
                int compareGroup = this.group.compareTo(e.group);
                if(compareGroup == 0) {
                    int compareType = this.type.compareTo(e.type);
                    if(compareType == 0) {
                        int compareName = this.name.compareTo(e.name);
                        if(compareName == 0) {
                            int compareVersion = (this.version == e.version ) ? 0 : (this.version > e.version ? 1 : -1);
                            if(compareVersion == 0) {
                                int compareChangeDate = this.changeDate.compareTo(e.changeDate);
                                if(compareChangeDate == 0) {
                                    int compareChangeUserID = this.changeUserID.compareTo(e.changeUserID);
                                    if(compareChangeUserID == 0) {
                                        return this.changeGroup.compareTo(e.changeGroup);
                                    }
                                    else {
                                        return compareChangeUserID;
                                    }
                                }
                                else {
                                    return compareChangeDate;
                                }
                            }
                            else {
                                return compareVersion;
                            }
                        }
                        else {
                            return compareName;
                        }
                    }
                    else {
                        return compareType;
                    }
                }
                else {
                    return compareGroup;
                }
            }
            else {
                return compareAlternate;
            }
        }
        else {
            return compareProject;
        }
    }

    /**
     * Comparator for changes sorting.
     * <br/>1. Change Date and Time (descending)
     * <br/>2. Type
     * <br/>3. Name
     * <br/>4. Version (descending)
     * <br/>5. Change User ID
     * <br/>6. Change Group
     */
    public static Comparator<SCLMFileState> ChangeComparator = new Comparator<SCLMFileState>()
    {
        public int compare(SCLMFileState o1, SCLMFileState o2)
        {
            int compareChangeDate = -o1.changeDate.compareTo(o2.changeDate);
            if (compareChangeDate == 0) {
                int compareType = o1.type.compareTo(o2.type);
                if (compareType == 0) {
                int compareName = o1.name.compareTo(o2.name);
                    if (compareName == 0) {
                        if (o1.version == o2.version) {
                            int compareChangeUserID = o1.changeUserID.compareTo(o2.changeUserID);
                            if (compareChangeUserID == 0) {
                                return o1.changeGroup.compareTo(o2.changeGroup);
                            } else {
                                return compareChangeUserID;
                            }
                        } else {
                            return (o1.version > o2.version) ? -1 : 1;
                        }
                    } else {
                        return compareName;
                    }
                } else {
                    return compareType;
                }
            } else {
                return compareChangeDate;
            }
        }
    };

    /**
     * Simple toString method.
     * @return printable representation of SCLMFileState.
     */
    @Override
    public String toString()
    {
        String eType;
        if(this.editType == EditType.EDIT) {
            eType = "EDIT";
        } else {
            if(this.editType == EditType.DELETE) {
                eType = "DELETE";
            } else {
                if(this.editType == EditType.ADD) {
                    eType = "ADD";
                } else {
                    eType = "ERROR";
                }
            }
        }
        return eType + ": [" + this.getPath() + "] " + this.changeGroup + " <" + SCLMFileState.DateFormat.format(this.changeDate) + "> | " + this.changeUserID + ", ver.:" + this.version;
    }

    /**
     * Printable path.
     *
     * @return Printable path for SCLM file.
     */
    public String getPath() {
        return this.project + "." + this.alternate + "." + this.group + "." + this.type + "(" + this.name + ")";
    }

    /**
     * Used for removing duplicate files (matching paths).
     *
     * @param o Object for comparison.
     *
     * @return Whether the files are at the same location.
     */
    @Override
    public boolean equals(Object o)
    {
        if ( ! (o instanceof SCLMFileState) )
        {
            return false;
        } else {
            return this.getPath().compareTo(((SCLMFileState)o).getPath()) == 0;
        }
    }
}
