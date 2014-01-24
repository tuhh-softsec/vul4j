/* Copyright (c) 2013 Stanford University
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR(S) DISCLAIM ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL AUTHORS BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package edu.stanford.ramcloud;

/*
 * This class provides Java bindings for RAMCloud. Right now it is a rather
 * simple subset of what RamCloud.h defines.
 *
 * Running ``javah'' on this file will generate a C header file with the
 * appropriate JNI function definitions. The glue interfacing to the C++
 * RAMCloud library can be found in JRamCloud.cc.
 *
 * For JNI information, the IBM tutorials and Android developer docs are much
 * better than Sun's at giving an overall intro:
 *      http://www.ibm.com/developerworks/java/tutorials/j-jni/section4.html
 *      http://developer.android.com/training/articles/perf-jni.html
 */
public class JRamCloud {
    static {
        System.loadLibrary("edu_stanford_ramcloud_JRamCloud");
    }

    /// Pointer to the underlying C++ RAMCloud object associated with this
    /// object.
    private long ramcloudObjectPointer = 0;

    /**
     * See src/RejectRules.h.
     */
    public class RejectRules {
	private long givenVersion;
        private boolean doesntExist;
        private boolean exists;
        private boolean versionLeGiven;
        private boolean versionNeGiven;

        public RejectRules() {
            this.givenVersion = -1;
            this.exists = this.doesntExist = this.versionLeGiven = this.versionNeGiven = false;
        }

        public void setLeVersion(long version) {
            setVersion(version);
            this.versionLeGiven = true;
        }

        public void setExists() {
            this.exists = true;
        }

        public void setDoesntExists() {
            this.doesntExist = true;
        }

        public void setNeVersion(long version) {
            setVersion(version);
            this.versionNeGiven = true;
        }

        private void setVersion(long version) {
            this.givenVersion = version;
        }	
    }
    
    public static class multiReadObject {
        long tableId;
        byte[] key;
        
        public multiReadObject(long _tableId, byte[] _key){
            tableId = _tableId;
            key = _key;
        }
    }
    
    public static class MultiWriteObject {
        long tableId;
        byte[] key;
        byte[] value;
        RejectRules rules;

        public MultiWriteObject(long tableId, byte[] key, byte[] value, RejectRules rules) {
            this.tableId = tableId;
            this.key = key;
            this.value = value;
            this.rules = rules;
        }
    }

    public class MultiWriteRspObject {
        private int status;
        private long version;

        public MultiWriteRspObject(int status, long version) {
            this.status = status;
            this.version = version;
        }
        public int getStatus() {
            return status;
        }

        public long getVersion() {
            return version;
        }
    }

    /**
     * This class is returned by Read operations. It encapsulates the entire
     * object, including the key, value, and version.
     *
     * It mostly exists because Java doesn't support primitive out parameters
     * or multiple return values, and we don't know the object's size ahead of
     * time, so passing in a fixed-length array would be problematic.
     */
    public class Object {
        Object(byte[] _key, byte[] _value, long _version)
        {
            key = _key;
            value = _value;
            version = _version;
        }

        public String
        getKey()
        {
            return new String(key);
        }

        public String
        getValue()
        {
            return new String(value);
        }

        final public byte[] key;
        final public byte[] value;
        final public long version;
    }

    public class TableEnumerator {
        private long tableEnumeratorObjectPointer = 0;
        private long ramCloudObjectPointer = 0;
        
        public TableEnumerator(long tableId)
        {
            ramCloudObjectPointer = ramcloudObjectPointer;
            tableEnumeratorObjectPointer = init(tableId);
        }
        
        private native long init(long tableId);
        public native boolean hasNext();
        public native Object next();
    }

    /**
     * Connect to the RAMCloud cluster specified by the given coordinator's
     * service locator string. This causes the JNI code to instantiate the
     * underlying RamCloud C++ object.
     */
    public
    JRamCloud(String coordinatorLocator)
    {
        ramcloudObjectPointer = connect(coordinatorLocator);
    }

    /**
     * Disconnect from the RAMCloud cluster. This causes the JNI code to
     * destroy the underlying RamCloud C++ object.
     */
    public void
    disconnect()
    {
        if (ramcloudObjectPointer != 0) {
            disconnect(ramcloudObjectPointer);
            ramcloudObjectPointer = 0;
        }
    }

    /**
     * This method is called by the garbage collector before destroying the
     * object. The user really should have called disconnect, but in case
     * they did not, be sure to clean up after them.
     */
    public void
    finalize()
    {
        System.err.println("warning: JRamCloud::disconnect() was not called " +
                           "prior to the finalizer. You should disconnect " +
                           "your JRamCloud object when you're done with it.");
        disconnect();
    }

    /**
     * Convenience read() wrapper that take a String key argument.
     */
    public Object
    read(long tableId, String key)
    {
        return read(tableId, key.getBytes());
    }

    /**
     * Convenience read() wrapper that take a String key argument.
     */
    public Object
    read(long tableId, String key, RejectRules rules)
    {
        return read(tableId, key.getBytes(), rules);
    }
    
    /**
     * Convenience remove() wrapper that take a String key argument.
     */
    public long
    remove(long tableId, String key)
    {
        return remove(tableId, key.getBytes());
    }

    /**
     * Convenience remove() wrapper that take a String key argument.
     */
    public long
    remove(long tableId, String key, RejectRules rules)
    {
        return remove(tableId, key.getBytes(), rules);
    }

    /**
     * Convenience write() wrapper that take String key and value arguments.
     */
    public long
    write(long tableId, String key, String value)
    {
        return write(tableId, key.getBytes(), value.getBytes());
    }

    /**
     * Convenience write() wrapper that take String key and value arguments.
     */
    public long
    write(long tableId, String key, String value, RejectRules rules)
    {
        return write(tableId, key.getBytes(), value.getBytes(), rules);
    }

    /**
     * Convenience write() wrapper that takes a String key and a byte[] value
     * argument.
     */
    public long
    write(long tableId, String key, byte[] value)
    {
        return write(tableId, key.getBytes(), value);
    }

    /**
     * Convenience write() wrapper that takes a String key and a byte[] value
     * argument.
     */
    public long
    write(long tableId, String key, byte[] value, RejectRules rules)
    {
        return write(tableId, key.getBytes(), value, rules);
    }
    
    private static native long connect(String coordinatorLocator);
    private static native void disconnect(long ramcloudObjectPointer);

    public native long createTable(String name);
    public native long createTable(String name, int serverSpan);
    public native void dropTable(String name);
    public native long getTableId(String name);
    public native Object read(long tableId, byte[] key);
    public native Object read(long tableId, byte[] key, RejectRules rules);
    public native Object[] multiRead(multiReadObject[] mread);
    public native long remove(long tableId, byte[] key);
    public native long remove(long tableId, byte[] key, RejectRules rules);
    public native long write(long tableId, byte[] key, byte[] value);
    public native long write(long tableId, byte[] key, byte[] value, RejectRules rules);
    public native long writeRule(long tableId, byte[] key, byte[] value, RejectRules rules);
    public native MultiWriteRspObject[] multiWrite(MultiWriteObject[] mwrite);

    /*
     * The following exceptions may be thrown by the JNI functions:
     */

    public class TableDoesntExistException extends Exception {
        public TableDoesntExistException(String message)
        {
            super(message);
        }
    }

    public class ObjectDoesntExistException extends Exception {
        public ObjectDoesntExistException(String message)
        {
            super(message);
        }
    }

    public class ObjectExistsException extends Exception {
        public ObjectExistsException(String message)
        {
            super(message);
        }
    }

    public class WrongVersionException extends Exception {
        public WrongVersionException(String message)
        {
            super(message);
        }
    }
    
    public class InvalidObjectException extends Exception {
        public InvalidObjectException(String message) {
            super(message);
        }
    }
    
    public class RejectRulesException extends Exception {
        public RejectRulesException(String message) {
            super(message);
        }
    }

    /**
     * A simple end-to-end test of the java bindings.
     */
    public static void
    main(String argv[])
    {
        JRamCloud ramcloud = new JRamCloud(argv[0]);
        long tableId = ramcloud.createTable("hi");
        System.out.println("created table, id = " + tableId);
        long tableId2 = ramcloud.getTableId("hi");
        System.out.println("getTableId says tableId = " + tableId2);

        System.out.println("wrote obj version = " +
            ramcloud.write(tableId, "thisIsTheKey", "thisIsTheValue"));

        JRamCloud.Object o = ramcloud.read(tableId, "thisIsTheKey");
        System.out.println("read object: key = [" + o.getKey() + "], value = ["
            + o.getValue() + "], version = " + o.version);

        ramcloud.remove(tableId, "thisIsTheKey");

        try {
            ramcloud.read(tableId, "thisIsTheKey");
            System.out.println("Error: shouldn't have read successfully!");
        } catch (Exception e) {
            // OK
        }

        ramcloud.write(tableId, "thisIsTheKey", "thisIsTheValue");
        
        long before = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            JRamCloud.Object unused = ramcloud.read(tableId, "thisIsTheKey");
        }
        long after = System.nanoTime();
        System.out.println("Avg read latency: " +
            ((double)(after - before) / 1000 / 1000) + " usec");
        
        // multiRead test
        long tableId4 = ramcloud.createTable("table4");
        System.out.println("table4 id " + tableId4);
        ramcloud.write(tableId4, "object1-1", "value:1-1");
        ramcloud.write(tableId4, "object1-2", "value:1-2");
        ramcloud.write(tableId4, "object1-3", "value:1-3");
        long tableId5 = ramcloud.createTable("table5");
        System.out.println("table5 id " + tableId5);
        ramcloud.write(tableId5, "object2-1", "value:2-1");
        long tableId6 = ramcloud.createTable("table6");
        ramcloud.write(tableId6, "object3-1", "value:3-1");
        ramcloud.write(tableId6, "object3-2", "value:3-2");

        multiReadObject mread[] = new multiReadObject[2];
        //for (int k = 0; k < 2000; k++) {
        mread[0] = new multiReadObject(tableId4, "object1-1".getBytes());
        mread[1] = new multiReadObject(tableId5, "object2-1".getBytes());
        JRamCloud.Object out[] = ramcloud.multiRead(mread);
        for (int i = 0 ; i < 2 ; i++){
            System.out.println("multi read object: key = [" + out[i].getKey() + "], value = ["
                    + out[i].getValue() + "]");
        //}
        }
        MultiWriteObject mwrite[] = new MultiWriteObject[2];
        for (int i = 0; i < 1000; i++) {
            String key1 = "key1" + new Integer(i).toString();
            String key2 = "key2" + new Integer(i).toString();
      
            mwrite[0] = new MultiWriteObject(tableId4, key1.getBytes(), "v0-value".getBytes(), null);
            mwrite[1] = new MultiWriteObject(tableId5, key2.getBytes(), "v1".getBytes(), null);
            MultiWriteRspObject[] rsp = ramcloud.multiWrite(mwrite);
            if (rsp != null) {
                for (int j = 0; j < rsp.length; j++) {
                    System.out.println("multi write rsp(" + j + ") status:version " + rsp[j].getStatus() + ":" + rsp[j].getVersion());
                }
            }
        }
        for (int i = 0; i < 1000; i++) {
            String key1 = "key1" + new Integer(i).toString();
            String key2 = "key2" + new Integer(i).toString();
            mread[0] = new multiReadObject(tableId4, key1.getBytes());
            mread[1] = new multiReadObject(tableId5, key2.getBytes());
            out = ramcloud.multiRead(mread);
            for (int j = 0; j < 2; j++) {
                System.out.println("multi read object: key = [" + out[j].getKey() + "], value = [" + out[j].getValue() + "]");
            }
        }
        ramcloud.dropTable("table4");
        ramcloud.dropTable("table5");
        ramcloud.dropTable("table6");
        ramcloud.disconnect();
    }
}
