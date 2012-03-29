/*
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.rest.service.raw;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.dbunit.operation.DatabaseOperation;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.catalog.parse.MessageStreamParser;
import org.jboss.seam.mock.EnhancedMockHttpServletRequest;
import org.jboss.seam.mock.EnhancedMockHttpServletResponse;
import org.jboss.seam.mock.ResourceRequestEnvironment.Method;
import org.jboss.seam.mock.ResourceRequestEnvironment.ResourceRequest;
import org.testng.annotations.Test;
import org.zanata.ZanataRawRestTest;

public class FileRestTest extends ZanataRawRestTest
{

   @Override
   protected void prepareDBUnitOperations()
   {
      beforeTestOperations.add(new DataSetOperation("org/zanata/test/model/AccountData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
      beforeTestOperations.add(new DataSetOperation("org/zanata/test/model/ProjectsData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
      beforeTestOperations.add(new DataSetOperation("org/zanata/test/model/LocalesData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
      beforeTestOperations.add(new DataSetOperation("org/zanata/test/model/TextFlowTestData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
   }
   
   @Test 
   public void getPo() throws Exception
   {
      new ResourceRequest(unauthorizedEnvironment, Method.GET, "/restv1/file/translation/sample-project/1.0/en-US/po")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            request.setQueryString(""); // Need to add this when using query params or else the test fails
            request.addQueryParameter("docId", "/my/path/document.txt");
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertThat(response.getStatus(), is(200)); // Ok
            assertHeaderValue(response, "Content-Disposition", "attachment; filename=\"document.txt.po\"");
            assertThat(response.getContentType(), is(MediaType.APPLICATION_OCTET_STREAM));
            assertPoFileCorrect( response.getContentAsString() );    
            assertPoFileContainsTranslations( response.getContentAsString(), 
                  "hello world", "" );
         }
      }.run();
   }
   
   @Test 
   public void getPo2() throws Exception
   {
      new ResourceRequest(unauthorizedEnvironment, Method.GET, "/restv1/file/translation/sample-project/1.0/en-US/po")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            request.setQueryString(""); // Need to add this when using query params or else the test fails
            request.addQueryParameter("docId", "my/path/document-2.txt");
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertThat(response.getStatus(), is(200)); // Ok
            assertHeaderValue(response, "Content-Disposition", "attachment; filename=\"document-2.txt.po\"");
            assertThat(response.getContentType(), is(MediaType.APPLICATION_OCTET_STREAM));
            assertPoFileCorrect( response.getContentAsString() );    
            assertPoFileContainsTranslations( response.getContentAsString(), 
                  "mssgId1", "mssgTrans1",
                  "mssgId2", "mssgTrans2",
                  "mssgId3", "mssgTrans3" );
         }
      }.run();
   }
   
   private static void assertPoFileCorrect( String poFileContents )
   {
      MessageStreamParser messageParser = new MessageStreamParser( new StringReader(poFileContents) );
      
      while (messageParser.hasNext())
      {
         Message message = messageParser.next();
         
         if( message.isHeader() )
         {
            // assert that expected headers are present (with values if needed)
            assertThat(message.getMsgstr(), containsString("MIME-Version:"));
            assertThat(message.getMsgstr(), containsString("Content-Type:"));
            assertThat(message.getMsgstr(), containsString("Content-Transfer-Encoding:"));
            assertThat(message.getMsgstr(), containsString("Last-Translator:"));
            assertThat(message.getMsgstr(), containsString("PO-Revision-Date:"));
            assertThat(message.getMsgstr(), containsString("Language-Team:"));
            assertThat(message.getMsgstr(), containsString("X-Generator: Zanata")); // Generator is Zanata
            assertThat(message.getMsgstr(), containsString("Plural-Forms:"));
         }
      }
   }
   
   /**
    * Validates that the po files contains the appropriate translations.
    * @param poFileContents The contents of the PO file as a string
    * @param translations The translations in (msgid, msgstr) pairs. E.g. mssgid1, trans1, mssgid2, trans2, ... etc.
    */
   private static void assertPoFileContainsTranslations( String poFileContents, String ... translations )
   {
      if( translations.length % 2 != 0 )
      {
         throw new AssertionError("Translation parameters should be given in pairs.");
      }
      
      MessageStreamParser messageParser = new MessageStreamParser( new StringReader(poFileContents) );
      
      List<String> found = new ArrayList<String>( translations.length );
      
      // Assert that all the given translations are present
      while (messageParser.hasNext())
      {
         Message message = messageParser.next();
         
         if( !message.isHeader() )
         {
            // Find the message id in the array given to check
            int foundAt = 0;
            while( foundAt < translations.length )
            {
               // Message Id found
               if( message.getMsgid().equals( translations[foundAt] ) )
               {
                  found.add( message.getMsgid() );
                  // Translation does not match
                  if( !message.getMsgstr().equals( translations[foundAt + 1] ) )
                  {
                     throw new AssertionError("Expected translation for mssgid '" + message.getMsgid() + "' " +
                     		"is: '" + translations[foundAt + 1] + "'. " +
                     		"Instead got '" + message.getMsgstr() + "'");
                  }
               }

               foundAt += 2;
            }
         }
      }
      
      // If there are some messages not found
      if( found.size() < translations.length / 2 )
      {
         StringBuilder assertionError = new StringBuilder("The following mssgids were expected yet not found: ");
         for(int i=0; i<translations.length; i += 2)
         {
            if( !found.contains(translations[i]) )
            {
               assertionError.append( translations[i]+" | " );
            }
         }
         
         throw new AssertionError( assertionError.toString() );
      }
   }
}
