package org.apache.xml.security.utils;

import java.security.SecureRandom;

/**
 * Singleton for an application-wide {@link SecureRandom}.
 *
 * @author $Author$
 */
public class PRNG {

  private static PRNG _prng = null;
  private SecureRandom _sr;

  private PRNG(SecureRandom secureRandom) {
     this._sr = secureRandom;
  }

  public static void init(SecureRandom secureRandom) {
     if (PRNG._prng == null) {
        PRNG._prng = new PRNG(secureRandom);
     }
  }

  public static PRNG getInstance() {
     if (PRNG._prng == null) {
        PRNG.init(new SecureRandom());
     }

     return PRNG._prng;
  }

  public SecureRandom getSecureRandom() {
     return this._sr;
  }

  public static byte[] createBytes(int length) {
     byte result[] = new byte[length];
     PRNG.getInstance().nextBytes(result);
     return result;
  }

  public void nextBytes(byte[] bytes) {
     this._sr.nextBytes(bytes);
  }

  public double nextDouble() {
     return this._sr.nextDouble();
  }

  public int nextInt() {
     return this._sr.nextInt();
  }

  public int nextInt(int i) {
     return this._sr.nextInt(i);
  }

  public boolean nextBoolean() {
     return this._sr.nextBoolean();
  }

  static {
     org.apache.xml.security.Init.init();
  }
}