package net.onrc.onos.ofcontroller.bgproute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PtreeNode {
	public PtreeNode parent;
	public PtreeNode left;
	public PtreeNode right;
	
	public byte key[];
	public int keyBits;
	
	public int refCount;
	
	public Rib rib;
	protected static Logger log = LoggerFactory.getLogger(BgpRoute.class);
	
	PtreeNode(byte [] key, int key_bits, int max_key_octet) {
		parent = null;
		left = null;
		right = null;
		refCount = 0;
		rib = null;
		this.key = new byte[max_key_octet];
		this.keyBits = key_bits;
		log.debug("inside Ptreenode constructor key {} bits {}", key, key_bits);
		
		int octet = Ptree.bit_to_octet(key_bits);
		for (int i = 0; i < max_key_octet; i++) {
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
