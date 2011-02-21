/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.extras.extended;


import org.apache.directory.shared.ldap.model.message.AbstractExtendedRequest;


/**
 * 
 * An extended operation requesting the server to generate a public/private key pair and a certificate
 * and store them in a specified target entry in the DIT.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CertGenerationRequest extends AbstractExtendedRequest<ICertGenerationResponse> implements ICertGenerationRequest
{
    /** The serial version UUID */
    private static final long serialVersionUID = 1L;

    /** the Dn of the server entry which will be updated*/
    private String targetDN;

    /** the issuer Dn that will be set in the certificate*/
    private String issuerDN;// = "CN=ApacheDS, OU=Directory, O=ASF, C=US";

    /** the Dn of the subject that is present in the certificate*/
    private String subjectDN;// = "CN=ApacheDS, OU=Directory, O=ASF, C=US";

    /** name of the algorithm used for generating the keys*/
    private String keyAlgorithm;// = "RSA";

    
    /**
     * Creates a new instance of CertGenerationRequest.
     *
     * @param messageId the message id
     * @param targerDN the Dn of target entry whose key and certificate values will be changed
     * @param issuerDN Dn to be used as the issuer's Dn in the certificate
     * @param subjectDN Dn to be used as certificate's subject
     * @param keyAlgorithm crypto algorithm name to be used for generating the keys
     */
    public CertGenerationRequest( int messageId, String targerDN, String issuerDN, String subjectDN, String keyAlgorithm )
    {
        super( messageId );
        setRequestName( EXTENSION_OID );
        this.targetDN = targerDN;
        this.issuerDN = issuerDN;
        this.subjectDN = subjectDN;
        this.keyAlgorithm = keyAlgorithm;
    }


    /**
     * Creates a new instance of CertGenerationRequest.
     */
    public CertGenerationRequest()
    {
        setRequestName( EXTENSION_OID );
    }


    /**
     * {@inheritDoc}
     */
    public String getTargetDN()
    {
        return targetDN;
    }


    /**
     * {@inheritDoc}
     */
    public void setTargetDN( String targetDN )
    {
        this.targetDN = targetDN;
    }


    /**
     * {@inheritDoc}
     */
    public String getIssuerDN()
    {
        return issuerDN;
    }


    /**
     * {@inheritDoc}
     */
    public void setIssuerDN( String issuerDN )
    {
        this.issuerDN = issuerDN;
    }


    /**
     * {@inheritDoc}
     */
    public String getSubjectDN()
    {
        return subjectDN;
    }


    /**
     * {@inheritDoc}
     */
    public void setSubjectDN( String subjectDN )
    {
        this.subjectDN = subjectDN;
    }


    /**
     * {@inheritDoc}
     */
    public String getKeyAlgorithm()
    {
        return keyAlgorithm;
    }


    /**
     * {@inheritDoc}
     */
    public void setKeyAlgorithm( String keyAlgorithm )
    {
        this.keyAlgorithm = keyAlgorithm;
    }


    @Override
    public ICertGenerationResponse getResultResponse()
    {
        return new CertGenerationResponse();
    }
    
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "Certficate Generation Object { " ).append( " Target Dn: " ).append( targetDN ).append( ',' );
        sb.append( " Issuer Dn: " ).append( issuerDN ).append( ',' );
        sb.append( " Subject Dn: " ).append( subjectDN ).append( ',' );
        sb.append( " Key Algorithm: " ).append( keyAlgorithm ).append( " }" );

        return sb.toString();
    }
}
