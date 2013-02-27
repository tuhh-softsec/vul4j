package net.floodlightcontroller.util;

import org.openflow.util.HexString;
import net.floodlightcontroller.util.serializers.DpidDeserializer;
import net.floodlightcontroller.util.serializers.DpidSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing a network switch DPID.
 */
@JsonDeserialize(using=DpidDeserializer.class)
@JsonSerialize(using=DpidSerializer.class)
public class Dpid {
    static public long UNKNOWN = 0;

    private long value;

    /**
     * Default constructor.
     */
    public Dpid() {
	this.value = Dpid.UNKNOWN;
    }

    /**
     * Constructor from a long value.
     *
     * @param value the value to use.
     */
    public Dpid(long value) {
	this.value = value;
    }

    /**
     * Constructor from a string.
     *
     * @param value the value to use.
     */
    public Dpid(String value) {
	this.value = HexString.toLong(value);
    }

    /**
     * Get the value of the DPID.
     *
     * @return the value of the DPID.
     */
    public long value() { return value; }

    /**
     * Set the value of the DPID.
     *
     * @param value the value to set.
     */
    public void setValue(long value) {
	this.value = value;
    }

    /**
     * Convert the DPID value to a ':' separated hexadecimal string.
     *
     * @return the DPID value as a ':' separated hexadecimal string.
     */
    @Override
    public String toString() {
	return HexString.toHexString(this.value);
    }
}
