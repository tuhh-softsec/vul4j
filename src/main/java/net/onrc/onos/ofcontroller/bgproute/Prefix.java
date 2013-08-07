package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Prefix {
	private final int MAX_BYTES = 4;
	
	private final int prefixLength;
	private final byte[] address;
	
	//For verifying the arguments and pretty printing
	private final InetAddress inetAddress;
	
	public Prefix(byte[] addr, int prefixLength) {
		if (addr == null || addr.length != MAX_BYTES || 
				prefixLength < 0 || prefixLength > MAX_BYTES * Byte.SIZE) {
			throw new IllegalArgumentException();
		}

		address = canonicalizeAddress(addr, prefixLength);
		this.prefixLength = prefixLength;
		
		try {
			inetAddress = InetAddress.getByAddress(address);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException();
		}
	}

	public Prefix(String strAddress, int prefixLength) {
		byte[] addr = null;
		try {
			addr = InetAddress.getByName(strAddress).getAddress();
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Invalid IP inetAddress argument");
		}
				
		if (addr == null || addr.length != MAX_BYTES || 
				prefixLength < 0 || prefixLength > MAX_BYTES * Byte.SIZE) {
			throw new IllegalArgumentException();
		}
		
		address = canonicalizeAddress(addr, prefixLength);
		this.prefixLength = prefixLength;
		
		try {
			inetAddress = InetAddress.getByAddress(address);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException();
		}
	}
	
	private byte[] canonicalizeAddress(byte[] address, int prefixLength) {
		byte[] result = new byte[address.length];
		
		if (prefixLength == 0) {
			for (int i = 0; i < MAX_BYTES; i++) {
				result[i] = 0;
			}
			
			return result;
		}
		
		result = Arrays.copyOf(address, address.length);
		
		//Set all bytes after the end of the prefix to 0
		int lastByteIndex = (prefixLength - 1) / Byte.SIZE;
		for (int i = lastByteIndex; i < MAX_BYTES; i++) {
			result[i] = 0;
		}
		
		byte lastByte = address[lastByteIndex];
		byte mask = 0;
		byte lsb = 1;
		int lastBit = (prefixLength - 1) % Byte.SIZE;
		for (int i = 0; i < Byte.SIZE; i++) {
			if (i <= lastBit + 1) {
				mask |= lsb;
			}
			mask <<= 1;
		}

		result[lastByteIndex] = (byte) (lastByte & mask);
		
		return result;
	}

	public int getPrefixLength() {
		return prefixLength;
	}
	
	public byte[] getAddress() {
		return address;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Prefix)) {
			return false;
		}
		
		Prefix otherPrefix = (Prefix) other;
		
		return (Arrays.equals(address, otherPrefix.address)) &&
				(prefixLength == otherPrefix.prefixLength);
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + prefixLength;
		hash = 31 * hash + Arrays.hashCode(address);
		return hash;
	}
	
	@Override
	public String toString() {
		return inetAddress.getHostAddress() + "/" + prefixLength;
	}
	
	public String printAsBits() {
		String result = "";
		for (int i = 0; i < address.length; i++) {
			byte b = address[i];
			for (int j = 0; j < Byte.SIZE; j++) {
				byte mask = (byte) (0x80 >>> j);
				result += ((b & mask) == 0)? "0" : "1";
				if (i*Byte.SIZE+j == prefixLength-1) {
					return result;
				}
			}
			result += " ";
		}
		return result.substring(0, result.length() - 1);
	}
}
