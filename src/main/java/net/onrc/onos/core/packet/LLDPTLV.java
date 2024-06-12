/**
 *    Copyright 2011, Big Switch Networks, Inc.
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

package net.onrc.onos.core.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
public class LLDPTLV {
    protected byte type;
    protected short length;
    protected byte[] value;

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public LLDPTLV setType(byte type) {
        this.type = type;
        return this;
    }

    /**
     * @return the length
     */
    public short getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public LLDPTLV setLength(short length) {
        this.length = length;
        return this;
    }

    /**
     * @return the value
     */
    // @SuppressFBWarnings(value = "EI_EXPOSE_REP",
    //                     justification = "TODO: Return a copy of the object?")
    public byte[] getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    // @SuppressFBWarnings(value = "EI_EXPOSE_REP2",
    //                     justification = "TODO: Store a copy of the object?")
    public LLDPTLV setValue(byte[] value) {
        this.value = value;
        return this;
    }

    public byte[] serialize() {
        // type = 7 bits
        // info string length 9 bits, each value == byte
        // info string
        short scratch = (short) (((0x7f & this.type) << 9) | (0x1ff & this.length));
        byte[] data = new byte[2 + this.length];
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.putShort(scratch);
        if (this.value != null) {
            bb.put(this.value);
        }
        return data;
    }

    public LLDPTLV deserialize(ByteBuffer bb) {
        short sscratch;
        sscratch = bb.getShort();
        this.type = (byte) ((sscratch >> 9) & 0x7f);
        this.length = (short) (sscratch & 0x1ff);
        if (this.length > 0) {
            this.value = new byte[this.length];

            // if there is an underrun just toss the TLV
            if (bb.remaining() < this.length) {
                return null;
            }
            bb.get(this.value);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 1423;
        int result = 1;
        result = prime * result + length;
        result = prime * result + type;
        result = prime * result + Arrays.hashCode(value);
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        //
        // NOTE: Subclasses are are considered as change of identity, hence
        // equals() will return false if the class type doesn't match.
        //
        // The implication is that two instances - base class and derived class
        // will be different even if they have same bits on the wire.
        // We use this assumption to address the fundamental
        // "equivalence relation" issue with class inheritance and "equals()":
        //    http://www.artima.com/lejava/articles/equality.html
        //    http://www.angelikalanger.com/Articles/JavaSolutions/SecretsOfEquals/Equals.html
        //
        // Based on existing code, we don't mix the usage of based and derived
        // class instances.
        //
        // Note that the fix below is different from the Floodlight fix.
        // The Floodlight code uses "if (!(obj instanceof LLDPTLV))", but
        // that statement breaks the "equivalence relation".
        //
        if (getClass() != obj.getClass()) {
            return false;
        }
        LLDPTLV other = (LLDPTLV) obj;
        if (length != other.length) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (!Arrays.equals(value, other.value)) {
            return false;
        }
        return true;
    }
}