/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.codehaus.mojo.servicedocgen.descriptor;

import org.codehaus.mojo.servicedocgen.introspection.JElement;

/**
 * {@link Descriptor} of a response to a {@link OperationDescriptor operation}.
 *
 * @see OperationDescriptor#getResponses()
 * @author hohwille
 */
public class ResponseDescriptor
    extends AbstractDescriptor
{
    private String statusCode;

    private String reason;

    private String description;

    private String example;

    private String javaScriptType;

    private JElement javaElement;

    /**
     * @return the statusCode
     */
    public String getStatusCode()
    {
        return this.statusCode;
    }

    /**
     * @param statusCode is the statusCode to set
     */
    public void setStatusCode( String statusCode )
    {
        this.statusCode = statusCode;
    }

    /**
     * @return the reason
     */
    public String getReason()
    {
        return notNull( this.reason );
    }

    /**
     * @param reason is the reason to set
     */
    public void setReason( String reason )
    {
        this.reason = reason;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return notNull( this.description );
    }

    /**
     * @param description is the description to set
     */
    public void setDescription( String description )
    {
        this.description = description;
    }

    /**
     * @return the example
     */
    public String getExample()
    {
        return this.example;
    }

    /**
     * @param example is the example to set
     */
    public void setExample( String example )
    {
        this.example = example;
    }

    /**
     * @return the javaScriptType
     */
    public String getJavaScriptType()
    {
        return this.javaScriptType;
    }

    /**
     * @param javaScriptType is the javaScriptType to set
     */
    public void setJavaScriptType( String javaScriptType )
    {
        this.javaScriptType = javaScriptType;
    }

    /**
     * @return the javaElement
     */
    public JElement getJavaElement()
    {
        return this.javaElement;
    }

    /**
     * @param javaElement is the javaElement to set
     */
    public void setJavaElement( JElement javaElement )
    {
        this.javaElement = javaElement;
    }

}
