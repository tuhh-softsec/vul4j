/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package ant;



import java.io.*;
import java.security.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * Class Md5Task
 *
 * @author $Author$
 * @version $Revision$
 */
public class Md5Task extends Task {

   /** Field _md5Str */
   private byte[] _md5Str = null;

   /** Field _sha1Str */
   private byte[] _sha1Str = null;

   /** Field _filename */
   private String _filename = null;

   /**
    * Method execute
    *
    * @throws BuildException
    */
   public void execute() throws BuildException {

      if ((this._md5Str == null) && (this._sha1Str == null)) {
         throw new BuildException(
            "You must supply either a SHA1 or MD5 hash value or both, using the Sha1=\"\" and Md5=\"\" attributes");
      }

      if (this._filename == null) {
         throw new BuildException(
            "You must supply a file name using the file=\"\" attribute");
      }

      File f = new File(this._filename);

      if (!f.isFile() ||!f.canRead() ||!f.exists()) {
         throw new BuildException("Could not open " + this._filename
                                  + " for reading");
      }

      try {
         byte[] bytes = Md5Task.getBytesFromFile(this._filename);
         MessageDigest sha1 = MessageDigest.getInstance("SHA-1", "SUN");
         MessageDigest md5 = MessageDigest.getInstance("MD5", "SUN");

         sha1.update(bytes);
         md5.update(bytes);

         byte[] sha1dig = sha1.digest();
         byte[] md5dig = md5.digest();

         if (!MessageDigest.isEqual(this._sha1Str, sha1dig)
                 ||!MessageDigest.isEqual(this._md5Str, md5dig)) {
            System.out.println("Warning!!!");

            if (!MessageDigest.isEqual(this._sha1Str, sha1dig)) {
               System.out.println("The SHA1 hash value of " + this._filename
                                  + " is corrupted:");
               System.out.println("   was           "
                                  + HexDump.byteArrayToHexString(sha1dig));
               System.out
                  .println("   but should be "
                           + HexDump.byteArrayToHexString(this._sha1Str));
               System.out.println("");
            }

            if (!MessageDigest.isEqual(this._md5Str, md5dig)) {
               System.out.println("The MD5 hash value of " + this._filename
                                  + " is corrupted:");
               System.out.println("   was           "
                                  + HexDump.byteArrayToHexString(md5dig));
               System.out.println("   but should be "
                                  + HexDump.byteArrayToHexString(this._md5Str));
               System.out.println("");
            }

            throw new BuildException(
               "The digest values don't match; possibly the file "
               + this._filename + " was modified during download");
         }

         System.out.println("The hash values of " + this._filename + " are OK");
      } catch (FileNotFoundException ex) {
         throw new BuildException(ex.getMessage(), ex);
      } catch (IOException ex) {
         throw new BuildException(ex.getMessage(), ex);
      } catch (NoSuchAlgorithmException ex) {
         throw new BuildException(ex.getMessage(), ex);
      } catch (NoSuchProviderException ex) {
         throw new BuildException(ex.getMessage(), ex);
      }
   }

   /**
    * Method getBytesFromFile
    *
    * @param fileName
    * @return
    * @throws FileNotFoundException
    * @throws IOException
    */
   public static byte[] getBytesFromFile(String fileName)
           throws FileNotFoundException, IOException {

      byte refBytes[] = null;

      {
         FileInputStream fisRef = new FileInputStream(fileName);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte buf[] = new byte[1024];
         int len;

         while ((len = fisRef.read(buf)) > 0) {
            baos.write(buf, 0, len);
         }

         refBytes = baos.toByteArray();
      }

      return refBytes;
   }

   /**
    * Method setMd5
    *
    * @param md5Str
    */
   public void setMd5(String md5Str) {
      this._md5Str = HexDump.hexStringToByteArray(md5Str);
   }

   /**
    * Method setSha1
    *
    * @param sha1Str
    */
   public void setSha1(String sha1Str) {
      this._sha1Str = HexDump.hexStringToByteArray(sha1Str);
   }

   /**
    * Method setFile
    *
    * @param fileStr
    */
   public void setFile(String fileStr) {
      this._filename = fileStr;
   }
}
