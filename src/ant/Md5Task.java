/*
 * Copyright  1999-2004 The Apache Software Foundation.
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
package ant;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

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
    *
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
