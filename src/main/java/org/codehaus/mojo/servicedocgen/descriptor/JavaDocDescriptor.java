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
 * @author hohwille
 */
public class JavaDocDescriptor
{

    private String packagePrefix;

    private String url;

    /**
     * The constructor.
     */
    public JavaDocDescriptor()
    {
        super();
    }

    /**
     * The constructor.
     *
     * @param packagePrefix
     * @param url
     */
    public JavaDocDescriptor( String packagePrefix, String url )
    {
        super();
        this.packagePrefix = packagePrefix;
        this.url = url;
    }

    /**
     * @return the packagePrefix
     */
    public String getPackagePrefix()
    {
        if ( this.packagePrefix == null )
        {
            return "";
        }
        return this.packagePrefix;
    }

    /**
     * @param packagePrefix is the packagePrefix to set
     */
    public void setPackagePrefix( String packagePrefix )
    {
        this.packagePrefix = packagePrefix;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return this.url;
    }

    /**
     * @param url is the url to set
     */
    public void setUrl( String url )
    {
        this.url = url;
    }

}
