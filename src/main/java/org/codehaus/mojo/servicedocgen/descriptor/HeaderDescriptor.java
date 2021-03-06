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

/**
 * {@link Descriptor} for header information.
 *
 * @author hohwille
 */
public class HeaderDescriptor
    extends AbstractDescriptor
{

    private String description;

    private String javaScriptType;

    private String format;

    /**
     * @return the description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * @param description is the description to set
     */
    public void setDescription( String description )
    {
        this.description = description;
    }

    /**
     * @return the type
     */
    public String getJavaScriptType()
    {
        return this.javaScriptType;
    }

    /**
     * @param type is the type to set
     */
    public void setJavaScriptType( String type )
    {
        this.javaScriptType = type;
    }

    /**
     * @return the format
     */
    public String getFormat()
    {
        return this.format;
    }

    /**
     * @param format is the format to set
     */
    public void setFormat( String format )
    {
        this.format = format;
    }

}
