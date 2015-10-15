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
public class InfoDescriptor
    extends AbstractDescriptor
{

    private String title;

    private String description;

    private String termsOfService;

    private ContactDescriptor contact;

    private LicenseDescriptor license;

    private String version;

    /**
     * @return the title
     */
    public String getTitle()
    {
        return this.title;
    }

    /**
     * @param title is the title to set
     */
    public void setTitle( String title )
    {
        this.title = title;
    }

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
     * @return the termsOfService
     */
    public String getTermsOfService()
    {
        return this.termsOfService;
    }

    /**
     * @param termsOfService is the termsOfService to set
     */
    public void setTermsOfService( String termsOfService )
    {
        this.termsOfService = termsOfService;
    }

    /**
     * @return the contact
     */
    public ContactDescriptor getContact()
    {
        return this.contact;
    }

    /**
     * @param contact is the contact to set
     */
    public void setContact( ContactDescriptor contact )
    {
        this.contact = contact;
    }

    /**
     * @return the license
     */
    public LicenseDescriptor getLicense()
    {
        return this.license;
    }

    /**
     * @param license is the license to set
     */
    public void setLicense( LicenseDescriptor license )
    {
        this.license = license;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * @param version is the version to set
     */
    public void setVersion( String version )
    {
        this.version = version;
    }

    public static class ContactDescriptor
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
            return this.url;
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
            return this.email;
        }

        /**
         * @param email is the email to set
         */
        public void setEmail( String email )
        {
            this.email = email;
        }

    }

    public static class LicenseDescriptor
        extends AbstractDescriptor
    {

        private String name;

        private String url;

        /**
         * @return the name
         */
        public String getName()
        {
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

}
