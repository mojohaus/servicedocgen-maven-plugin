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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Toplevel {@link Descriptor} for all discovered services.
 *
 * @see #getServices()
 * @author hohwille
 */
public class ServicesDescriptor
    extends AbstractDescriptor
{

    private InfoDescriptor info;

    private String host;

    private int port;

    private String basePath;

    private List<JavaDocDescriptor> javadocs;

    private Set<String> schemes;

    private Set<String> consumes;

    private Set<String> produces;

    private List<ServiceDescriptor> services;

    private List<ErrorDescriptor> errors;

    private ExternalDocumentationDescriptor externalDocs;
    
    private String schemaDefinitionJson;
    
    private String schemaDefinitionYaml;

    /**
     * The constructor.
     */
    public ServicesDescriptor()
    {
        super();
    }

    /**
     * @return the info
     */
    public InfoDescriptor getInfo()
    {
        if ( this.info == null )
        {
            this.info = new InfoDescriptor();
        }
        return this.info;
    }

    /**
     * @param info is the info to set
     */
    public void setInfo( InfoDescriptor info )
    {
        this.info = info;
    }

    /**
     * @return the host
     */
    public String getHost()
    {
        return this.host;
    }

    /**
     * @param host is the host to set
     */
    public void setHost( String host )
    {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort()
    {
        return this.port;
    }

    /**
     * @param port is the port to set
     */
    public void setPort( int port )
    {
        this.port = port;
    }

    /**
     * @return the basePath
     */
    public String getBasePath()
    {
        return this.basePath;
    }

    /**
     * @param basePath is the basePath to set
     */
    public void setBasePath( String basePath )
    {
        this.basePath = basePath;
    }

    /**
     * @return the javadocs
     */
    public List<JavaDocDescriptor> getJavadocs()
    {
        if ( this.javadocs == null )
        {
            this.javadocs = new ArrayList<JavaDocDescriptor>();
        }
        return this.javadocs;
    }

    /**
     * @param javadocs is the javadocs to set
     */
    public void setJavadocs( List<JavaDocDescriptor> javadocs )
    {
        this.javadocs = javadocs;
    }

    /**
     * @return the schemes
     */
    public Set<String> getSchemes()
    {
        if ( this.schemes == null )
        {
            this.schemes = new HashSet<String>();
        }
        return this.schemes;
    }

    /**
     * @param schemes is the schemes to set
     */
    public void setSchemes( Set<String> schemes )
    {
        this.schemes = schemes;
    }

    /**
     * @return the consumes
     */
    public Set<String> getConsumes()
    {
        if ( this.consumes == null )
        {
            this.consumes = new HashSet<String>();
        }
        return this.consumes;
    }

    /**
     * @param consumes is the consumes to set
     */
    public void setConsumes( Set<String> consumes )
    {
        this.consumes = consumes;
    }

    /**
     * @return the produces
     */
    public Set<String> getProduces()
    {
        if ( this.produces == null )
        {
            this.produces = new HashSet<String>();
        }
        return this.produces;
    }

    /**
     * @param produces is the produces to set
     */
    public void setProduces( Set<String> produces )
    {
        this.produces = produces;
    }

    /**
     * @return the services
     */
    public List<ServiceDescriptor> getServices()
    {
        if ( this.services == null )
        {
            this.services = new ArrayList<ServiceDescriptor>();
        }
        return this.services;
    }

    /**
     * @param services is the services to set
     */
    public void setServices( List<ServiceDescriptor> services )
    {
        this.services = services;
    }

    /**
     * @return the errors
     */
    public List<ErrorDescriptor> getErrors()
    {
        if ( this.errors == null )
        {
            this.errors = new ArrayList<ErrorDescriptor>();
        }
        return this.errors;
    }

    /**
     * @param errors is the errors to set
     */
    public void setErrors( List<ErrorDescriptor> errors )
    {
        this.errors = errors;
    }

    /**
     * @return the externalDocs
     */
    public ExternalDocumentationDescriptor getExternalDocs()
    {
        return this.externalDocs;
    }

    /**
     * @param externalDocs is the externalDocs to set
     */
    public void setExternalDocs( ExternalDocumentationDescriptor externalDocs )
    {
        this.externalDocs = externalDocs;
    }

    /**
     * @return the schema definition as json
     */
    public String getSchemaDefinitionJson()
    {
        return this.schemaDefinitionJson;
    }

    /**
     * @param schemaDefinitionJson is the schema definition as json to set
     */
    public void setSchemaDefinitionJson( String schemaDefinitionJson )
    {
        this.schemaDefinitionJson = schemaDefinitionJson;
    }

    /**
     * @return the schema definition as yaml
     */
    public String getSchemaDefinitionYaml()
    {
        return this.schemaDefinitionYaml;
    }

    /**
     * @param schemaDefinitionYaml is the schema definition as yaml to set
     */
    public void setSchemaDefinitionYaml( String schemaDefinitionYaml )
    {
        this.schemaDefinitionYaml = schemaDefinitionYaml;
    }

}
