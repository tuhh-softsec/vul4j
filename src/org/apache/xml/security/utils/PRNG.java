/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
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

  private PRNG() {
     // we don't allow instantiation
  }

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
}