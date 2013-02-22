package net.floodlightcontroller.util;

import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.IPv4Net;
import net.floodlightcontroller.util.serializers.FlowEntryMatchSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing the Flow Entry Matching filter.
 *
 * The Flow Entry matching filter that is used to specify
 * the network data that would be forwarded on the data path from
 * the source to the destination. Examples: MAC address (of the
 * sender), IP prefix that includes the destination's IP address, etc.
 *
 * NOTE: The FlowEntryMatch specification below is incomplete: we need
 * more matching fields, we need to indicate which fields need to be
 * matched, etc.
 */
@JsonSerialize(using=FlowEntryMatchSerializer.class)
public class FlowEntryMatch {
    private MACAddress srcMac;		// Matching source MAC address
    private MACAddress dstMac;		// Matching destination MAC address
    private IPv4Net srcIPv4Net;		// Matching source IPv4 prefix
    private IPv4Net dstIPv4Net;		// Matching destination IPv4 prefix

    /**
     * Default constructor.
     */
    public FlowEntryMatch() {
    }

    /**
     * Get the matching source MAC address.
     *
     * @return the matching source MAC address.
     */
    public MACAddress srcMac() { return srcMac; }

    /**
     * Set the matching source MAC address.
     *
     * @param srcMac the matching source MAC address to set.
     */
    public void setSrcMac(MACAddress srcMac) {
	this.srcMac = srcMac;
    }

    /**
     * Get the matching destination MAC address.
     *
     * @return the matching destination MAC address.
     */
    public MACAddress dstMac() { return dstMac; }

    /**
     * Set the matching destination MAC address.
     *
     * @param dstMac the matching destination MAC address to set.
     */
    public void setDstMac(MACAddress dstMac) {
	this.dstMac = dstMac;
    }

    /**
     * Get the matching source IPv4 prefix.
     *
     * @return the matching source IPv4 prefix.
     */
    public IPv4Net srcIPv4Net() { return srcIPv4Net; }

    /**
     * Set the matching source IPv4 prefix.
     *
     * @param srcIPv4Net the matching source IPv4 prefix to set.
     */
    public void setSrcIPv4Net(IPv4Net srcIPv4Net) {
	this.srcIPv4Net = srcIPv4Net;
    }

    /**
     * Get the matching destination IPv4 prefix.
     *
     * @return the matching destination IPv4 prefix.
     */
    public IPv4Net dstIPv4Net() { return dstIPv4Net; }

    /**
     * Set the matching destination IPv4 prefix.
     *
     * @param srcIPv4Net the matching destination IPv4 prefix to set.
     */
    public void setDstIPv4Net(IPv4Net dstIPv4Net) {
	this.dstIPv4Net = dstIPv4Net;
    }

    /**
     * Convert the matching filter to a string.
     *
     * The string has the following form:
     *  [srcMac:XXX dstMac:XXX srcIPv4Net:XXX dstIPv4Net:XXX]
     *
     * @return the matching filter as a string.
     */
    @Override
    public String toString() {
	String ret = "[srcMac: " + this.srcMac.toString();
	ret += " dstMac:" + this.dstMac.toString();
	ret += " srcIPv4Net:" + this.srcIPv4Net.toString();
	ret += " dstIPv4Net:" + this.dstIPv4Net.toString();
	ret += "]";
	return ret;
    }
}
