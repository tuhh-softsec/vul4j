package net.onrc.onos.apps.bgproute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PtreeNode {
    public PtreeNode parent;
    public PtreeNode left;
    public PtreeNode right;

    public byte[] key;
    public int keyBits;

    public int refCount;

    // public RibEntry rib;
    private static final Logger log = LoggerFactory.getLogger(BgpRoute.class);

    PtreeNode(byte[] key, int keyBits, int maxKeyOctet) {
        parent = null;
        left = null;
        right = null;
        refCount = 0;
        // rib = null;
        this.key = new byte[maxKeyOctet];
        this.keyBits = keyBits;
        log.debug("inside Ptreenode constructor key {} bits {}", key, keyBits);

        int octet = Ptree.bit_to_octet(keyBits);
        for (int i = 0; i < maxKeyOctet; i++) {
            if (i < octet) {
                if (key != null) {
                    log.debug(octet + ": filling key[{}] {}", i, key[i]);
                    this.key[i] = key[i];
                } else {
                    log.debug("no filling, null key", i);
                }
            } else {
                log.debug("filling key {} as 0", i);
                this.key[i] = 0;
            }
        }
    }
}
