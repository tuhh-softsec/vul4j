/**
 l*    Copyright 2011,2012, Big Switch Networks, Inc.
 *    Originally created by David Erickson, Stanford University
 *
 *    Licensed under the Apache License, Version 2.0 (the "License"); you may
 *    not use this file except in compliance with the License. You may obtain
 *    a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 **/

package net.onrc.onos.core.devicemanager;

import java.io.Serializable;
import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.core.packet.IPv4;

/**
 * An entity on the network is a visible trace of a device that corresponds
 * to a packet received from a particular interface on the edge of a network,
 * with a particular VLAN tag, and a particular MAC address, along with any
 * other packet characteristics we might want to consider as helpful for
 * disambiguating devices.
 * <p/>
 * Entities are the most basic element of devices; devices consist of one or
 * more entities.  Entities are immutable once created, except for the last
 * seen timestamp.
 *
 * @author readams
 */
public class OnosDevice implements Serializable { //implements Comparable<OnosDevice> {

    private static final int ACTIVITY_TIMEOUT = 30000;

    /**
     * The MAC address associated with this entity.
     */
    private MACAddress macAddress;

    /**
     * The IP address associated with this entity, or null if no IP learned
     * from the network observation associated with this entity.
     */
    private Integer ipv4Address;

    /**
     * The VLAN tag on this entity, or null if untagged.
     */
    private Short vlan;

    /**
     * The DPID of the switch for the ingress point for this entity,
     * or null if not present.
     */
    private long switchDPID;

    /**
     * The port number of the switch for the ingress point for this entity,
     * or null if not present.
     */
    private short switchPort;

    /**
     * The last time we observed this entity on the network.
     */
    private Date lastSeenTimestamp;

    private Date activeSince;

    private int hashCode = 0;

    // ************
    // Constructors
    // ************
    protected OnosDevice() {
    }

    /**
     * Create a new entity.
     *
     * @param macAddress
     * @param vlan
     * @param ipv4Address
     * @param switchDPID
     * @param switchPort
     * @param lastSeenTimestamp
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
                        justification = "TODO: Store a copy of the object?")
    public OnosDevice(MACAddress macAddress, Short vlan,
                      Integer ipv4Address, Long switchDPID, short switchPort,
                      Date lastSeenTimestamp) {
        this.macAddress = macAddress;
        this.ipv4Address = ipv4Address;
        this.vlan = vlan;
        this.switchDPID = switchDPID;
        this.switchPort = switchPort;
        this.lastSeenTimestamp = lastSeenTimestamp;
        this.activeSince = lastSeenTimestamp;
    }

    // ***************
    // Getters/Setters
    // ***************

    public MACAddress getMacAddress() {
        return macAddress;
    }

    public Integer getIpv4Address() {
        return ipv4Address;
    }

    public void setIpv4Address(Integer ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    public Short getVlan() {
        return vlan;
    }

    public Long getSwitchDPID() {
        return switchDPID;
    }

    public void setSwitchDPID(long dpid) {
        this.switchDPID = dpid;
    }

    public short getSwitchPort() {
        return switchPort;
    }

    public void setSwitchPort(short port) {
        this.switchPort = port;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP",
                        justification = "TODO: Return a copy of the object?")
    public Date getLastSeenTimestamp() {
        return lastSeenTimestamp;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
                        justification = "TODO: Store a copy of the object?")
    public void setLastSeenTimestamp(Date lastSeenTimestamp) {
        if (activeSince == null ||
                (activeSince.getTime() + ACTIVITY_TIMEOUT) <
                        lastSeenTimestamp.getTime()) {
            this.activeSince = lastSeenTimestamp;
        }
        this.lastSeenTimestamp = lastSeenTimestamp;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP",
                        justification = "TODO: Return a copy of the object?")
    public Date getActiveSince() {
        return activeSince;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
                        justification = "TODO: Store a copy of the object?")
    public void setActiveSince(Date activeSince) {
        this.activeSince = activeSince;
    }

    @Override
    public int hashCode() {
        if (hashCode != 0) {
            return hashCode;
        }
        final int prime = 31;
        hashCode = 1;
        hashCode = prime * hashCode
                + ((ipv4Address == null) ? 0 : ipv4Address.hashCode());
        hashCode = prime * hashCode + (int) (macAddress.toLong() ^ (macAddress.toLong() >>> 32));
        hashCode = prime * hashCode + (int) switchDPID;
        hashCode = prime * hashCode + (int) switchPort;
        hashCode = prime * hashCode + ((vlan == null) ? 0 : vlan.hashCode());
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OnosDevice other = (OnosDevice) obj;
        if (hashCode() != other.hashCode()) {
            return false;
        }
        if (ipv4Address == null) {
            if (other.ipv4Address != null) {
                return false;
            }
        } else if (!ipv4Address.equals(other.ipv4Address)) {
            return false;
        }
        if (macAddress == null) {
            if (other.macAddress != null) {
                return false;
            }
        } else if (!macAddress.equals(other.macAddress)) {
            return false;
        }
        if (switchDPID != other.switchDPID) {
            return false;
        }
        if (switchPort != other.switchPort) {
            return false;
        }
        if (vlan == null) {
            if (other.vlan != null) {
                return false;
            }
        } else if (!vlan.equals(other.vlan)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Entity [macAddress=");
        builder.append(macAddress.toString());
        builder.append(", ipv4Address=");
        builder.append(IPv4.fromIPv4Address(ipv4Address == null ?
                0 : ipv4Address.intValue()));
        builder.append(", vlan=");
        builder.append(vlan);
        builder.append(", switchDPID=");
        builder.append(switchDPID);
        builder.append(", switchPort=");
        builder.append(switchPort);
        builder.append(", lastSeenTimestamp=");
        builder.append(lastSeenTimestamp == null ? "null" : lastSeenTimestamp.getTime());
        builder.append(", activeSince=");
        builder.append(activeSince == null ? "null" : activeSince.getTime());
        builder.append("]");
        return builder.toString();
    }

    /*
    @Override
    public int compareTo(OnosDevice o) {
        if (macAddress < o.macAddress) return -1;
        if (macAddress > o.macAddress) return 1;

        int r;
        if (switchDPID == null)
            r = o.switchDPID == null ? 0 : -1;
        else if (o.switchDPID == null)
            r = 1;
        else
            r = switchDPID.compareTo(o.switchDPID);
        if (r != 0) return r;

        if (switchPort == null)
            r = o.switchPort == null ? 0 : -1;
        else if (o.switchPort == null)
            r = 1;
        else
            r = switchPort.compareTo(o.switchPort);
        if (r != 0) return r;

        if (ipv4Address == null)
            r = o.ipv4Address == null ? 0 : -1;
        else if (o.ipv4Address == null)
            r = 1;
        else
            r = ipv4Address.compareTo(o.ipv4Address);
        if (r != 0) return r;

        if (vlan == null)
            r = o.vlan == null ? 0 : -1;
        else if (o.vlan == null)
            r = 1;
        else
            r = vlan.compareTo(o.vlan);
        if (r != 0) return r;

        return 0;
    }*/

}