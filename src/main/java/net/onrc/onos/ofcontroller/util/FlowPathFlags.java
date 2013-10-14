package net.onrc.onos.ofcontroller.util;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The class representing the Flow Path flags.
 */
public class FlowPathFlags {
    private long flags;

    // Discard the first-hop Flow Entry
    private final long DISCARD_FIRST_HOP_ENTRY		= (1 << 0);

    // Keep only the first-hop Flow Entry
    private final long KEEP_ONLY_FIRST_HOP_ENTRY	= (1 << 1);

    /**
     * Default constructor.
     */
    public FlowPathFlags() {
	this.flags = 0;
    }

    /**
     * Constructor for given flags.
     *
     * @param flags the flags value to set.
     */
    public FlowPathFlags(long flags) {
	this.flags = flags;
    }

    /**
     * Constructor for given flags as a string.
     *
     * The string value should contain the name of each flags to set. E.g.:
     *  "DISCARD_FIRST_HOP_ENTRY,KEEP_ONLY_FIRST_HOP_ENTRY"
     * @param flagsStr the string value of the flags to set.
     */
    public FlowPathFlags(String flagsStr) {
	this.setFlagsStr(flagsStr);
    }

    /**
     * Get the flags.
     *
     * @return the flags.
     */
    @JsonProperty("flags")
    public long flags() { return flags; }

    /**
     * Set the flags.
     *
     * @param flags the flags value to set.
     */
    @JsonProperty("flags")
    public void setFlags(long flags) {
	this.flags = flags;
    }

    /**
     * Set the flags as a string.
     *
     * The string value should contain the name of each flags to set. E.g.:
     *  "DISCARD_FIRST_HOP_ENTRY,KEEP_ONLY_FIRST_HOP_ENTRY"
     * @param flagsStr the string value of the flags to set.
     */
    @JsonProperty("flagsStr")
    public void setFlagsStr(String flagsStr) {
	this.flags = 0L;

	// Test all flags
	if (flagsStr.contains("DISCARD_FIRST_HOP_ENTRY"))
	    this.flags |= DISCARD_FIRST_HOP_ENTRY;
	if (flagsStr.contains("KEEP_ONLY_FIRST_HOP_ENTRY"))
	    this.flags |= KEEP_ONLY_FIRST_HOP_ENTRY;
    }

    /**
     * Test whether the DISCARD_FIRST_HOP_ENTRY flag is set.
     *
     * @return true if the DISCARD_FIRST_HOP_ENTRY flag is set,
     * otherwise false.
     */
    public boolean isDiscardFirstHopEntry() {
	return ((flags & DISCARD_FIRST_HOP_ENTRY) != 0);
    }

    /**
     * Test whether the KEEP_ONLY_FIRST_HOP_ENTRY flag is set.
     *
     * @return true if the KEEP_ONLY_FIRST_HOP_ENTRY flag is set,
     * otherwise false.
     */
    public boolean isKeepOnlyFirstHopEntry() {
	return ((flags & KEEP_ONLY_FIRST_HOP_ENTRY) != 0);
    }

    /**
     * Convert the Flow Path Flags to a string.
     *
     * The string has the following form:
     *  [flags=DISCARD_FIRST_HOP_ENTRY,KEEP_ONLY_FIRST_HOP_ENTRY]
     *
     * @return the Flow Path flags as a string.
     */
    @Override
    public String toString() {
	String flagsStr = null;
	String ret = "[flags=";

	// Test all flags
	if ((this.flags & DISCARD_FIRST_HOP_ENTRY) != 0) {
	    if (flagsStr != null)
		flagsStr += ",";
	    flagsStr += "DISCARD_FIRST_HOP_ENTRY";
	}
	if ((this.flags & KEEP_ONLY_FIRST_HOP_ENTRY) != 0) {
	    if (flagsStr != null)
		flagsStr += ",";
	    flagsStr += "KEEP_ONLY_FIRST_HOP_ENTRY";
	}
	ret += "]";

	return ret;
    }
}
