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

import org.codehaus.mojo.servicedocgen.Util;

/**
 * {@link Descriptor} for contact information.
 *
 * @author hohwille
 */
public class ContactDescriptor
    extends AbstractDescriptor
{

    private String name;

    private String url;

    private String email;

    /**
     * @return the name
     */
    public String getName()
    {
        if ( this.name == null )
        {
            if ( this.email != null )
            {
                return this.email;
            }
            else if ( this.url != null )
            {
                return this.url;
            }
        }
        return this.name;
    }

    /**
     * @param name is the name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return notNull( this.url );
    }

    /**
     * @param url is the url to set
     */
    public void setUrl( String url )
    {
        this.url = url;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return notNull( this.email );
    }

    /**
     * @param email is the email to set
     */
    public void setEmail( String email )
    {
        this.email = email;
    }

    /**
     * @return the hyperlink reference of this contact.
     */
    public String getHref()
    {
        if ( !Util.isEmpty( this.email ) )
        {
            return "mailto:" + this.email;
        }
        return getUrl();
    }

}