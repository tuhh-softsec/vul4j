package net.onrc.onos.apps.bgproute;

/*
 * TODO This Ptree needs to be refactored if we're going to use it permenantly.
 *
 * The biggest problem is it leaks PTreeNode references - these need to stay within
 * the Ptree as they contain data fundamental to the structure of the tree.
 * You should put RIB entries in and get RIB entries out.
 * Also we need to get rid of the referencing scheme to determine when to delete nodes.
 * Deletes should be explicit, and there's no need to keep track of references if
 * we don't leak them out the the Ptree.
 */
public class Ptree {
    private int maxKeyBits;
    private int maxKeyOctets;
    //private int refCount;
    private PtreeNode top;
    private byte[] maskBits = {(byte) 0x00, (byte) 0x80, (byte) 0xc0, (byte) 0xe0, (byte) 0xf0, (byte) 0xf8, (byte) 0xfc, (byte) 0xfe, (byte) 0xff};

    public Ptree(int maxKeyBits) {
        this.maxKeyBits = maxKeyBits;
        maxKeyOctets = bit_to_octet(maxKeyBits);
        //refCount = 0;
    }

    public synchronized PtreeNode acquire(byte[] key) {
        return acquire(key, maxKeyBits);
    }

    public synchronized PtreeNode acquire(byte[] key, int keyBits) {
        if (keyBits > maxKeyBits) {
            return null;
        }

        PtreeNode node = top;
        PtreeNode match = null;

        while (node != null
                && node.keyBits <= keyBits
                && key_match(node.key, node.keyBits, key, keyBits) == true) {
            if (node.keyBits == keyBits) {
                return addReference(node);
            }

            match = node;

            if (bit_check(key, node.keyBits) == true) {
                node = node.right;
            } else {
                node = node.left;
            }
        }

        PtreeNode add = null;

        if (node == null) {
            add = new PtreeNode(key, keyBits, maxKeyOctets);

            if (match != null) {
                node_link(match, add);
            } else {
                top = add;
            }
        } else {
            add = node_common(node, key, keyBits);

            if (match != null) {
                node_link(match, add);
            } else {
                top = add;
            }
            node_link(add, node);

            if (add.keyBits != keyBits) {
                match = add;

                add = new PtreeNode(key, keyBits, maxKeyOctets);
                node_link(match, add);
            }
        }

        return addReference(add);
    }

    public synchronized PtreeNode lookup(byte[] key, int keyBits) {
        if (keyBits > maxKeyBits) {
            return null;
        }

        PtreeNode node = top;

        while (node != null
                && node.keyBits <= keyBits
                && key_match(node.key, node.keyBits, key, keyBits) == true) {
            if (node.keyBits == keyBits) {
                return addReference(node);
            }

            if (bit_check(key, node.keyBits) == true) {
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return null;
    }

    public synchronized PtreeNode match(byte[] key, int keyBits) {
        if (keyBits > maxKeyBits) {
            return null;
        }
        PtreeNode node = top;
        PtreeNode matched = null;

        if (node != null) {

            while (node != null
                    && node.keyBits <= keyBits
                    && key_match(node.key, node.keyBits, key, keyBits) == true) {
                matched = node;

                if (bit_check(key, node.keyBits) == true) {
                    node = node.right;
                } else {
                    node = node.left;
                }
            }
        }

        if (matched != null) {
            return addReference(matched);
        }

        return null;
    }

    public synchronized PtreeNode begin() {
        if (top == null) {
            return null;
        }
        return addReference(top);
    }

    public synchronized PtreeNode next(PtreeNode node) {
        PtreeNode next;

        if (node.left != null) {
            next = node.left;
            addReference(next);
            delReference(node);
            return next;
        }
        if (node.right != null) {
            next = node.right;
            addReference(next);
            delReference(node);
            return next;
        }

        PtreeNode start = node;
        while (node.parent != null) {
            if (node.parent.left == node && node.parent.right != null) {
                next = node.parent.right;
                addReference(next);
                delReference(start);
                return next;
            }
            node = node.parent;
        }

        delReference(start);

        return null;
    }

    public static int bit_to_octet(int keyBits) {
        return Math.max((keyBits + 7) / 8, 1);
    }

    private PtreeNode addReference(PtreeNode node) {
        node.refCount++;
        return node;
    }

    public synchronized void delReference(PtreeNode node) {
        if (node.refCount > 0) {
            node.refCount--;
        }
        if (node.refCount == 0) {
            node_remove(node);
        }
    }

    private boolean key_match(byte[] key1, int key1Len, byte[] key2, int key2Len) {
        int offset;
        int shift;

        if (key1Len > key2Len) {
            return false;
        }

        offset = (Math.min(key1Len, key2Len)) / 8;
        shift = (Math.min(key1Len, key2Len)) % 8;

        if (shift != 0) {
            if ((maskBits[shift] & (key1[offset] ^ key2[offset])) != 0) {
                return false;
            }
        }

        while (offset != 0) {
            offset--;
            if (key1[offset] != key2[offset]) {
                return false;
            }
        }
        return true;
    }

    private boolean bit_check(byte[] key, int keyBits) {
        int offset = keyBits / 8;
        int shift = 7 - (keyBits % 8);
        int bit = key[offset] & 0xff;

        bit >>= shift;

        if ((bit & 1) == 1) {
            return true;
        } else {
            return false;
        }
    }

    private void node_link(PtreeNode node, PtreeNode add) {
        boolean bit = bit_check(add.key, node.keyBits);

        if (bit == true) {
            node.right = add;
        } else {
            node.left = add;
        }
        add.parent = node;
    }

    private PtreeNode node_common(PtreeNode node, byte[] key, int keyBits) {
        int i;
        int limit = Math.min(node.keyBits, keyBits) / 8;

        for (i = 0; i < limit; i++) {
            if (node.key[i] != key[i]) {
                break;
            }
        }

        int commonLen = i * 8;
        int boundary = 0;

        if (commonLen != keyBits) {
            byte diff = (byte) (node.key[i] ^ key[i]);
            byte mask = (byte) 0x80;
            int shiftMask = 0;

            while (commonLen < keyBits && ((mask & diff) == 0)) {
                boundary = 1;

                shiftMask = (mask & 0xff);
                shiftMask >>= 1;
                mask = (byte) shiftMask;

                commonLen++;
            }
        }

        PtreeNode add = new PtreeNode(null, commonLen, maxKeyOctets);

        int j;
        for (j = 0; j < i; j++) {
            add.key[j] = node.key[j];
        }

        if (boundary != 0) {
            add.key[j] = (byte) (node.key[j] & maskBits[add.keyBits % 8]);
        }

        return add;
    }

    private void node_remove(PtreeNode node) {
        PtreeNode child;
        PtreeNode parent;

        if (node.left != null && node.right != null) {
            return;
        }

        if (node.left != null) {
            child = node.left;
        } else {
            child = node.right;
        }

        parent = node.parent;

        if (child != null) {
            child.parent = parent;
        }

        if (parent != null) {
            if (parent.left == node) {
                parent.left = child;
            } else {
                parent.right = child;
            }
        } else {
            top = child;
        }

        if (parent != null && parent.refCount == 0) {
            node_remove(parent);
        }
    }
}
