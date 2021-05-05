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

import org.codehaus.mojo.servicedocgen.introspection.JMethod;

/**
 * {@link Descriptor} of an operation of a {@link ServiceDescriptor service}.
 *
 * @see ServiceDescriptor#getOperations()
 * @author hohwille
 */
public class OperationDescriptor
    extends AbstractDescriptor
    implements Comparable<OperationDescriptor>
{

    private String id;

    private String path;

    private String httpMethod;

    private String description;

    private boolean deprecated;

    private Set<String> consumes;

    private Set<String> produces;

    private List<ParameterDescriptor> parameters;

    private List<ResponseDescriptor> responses;

    private JMethod javaMethod;

    /**
     * @return the id
     */
    public String getId()
    {
        if ( this.id == null )
        {
            if ( this.path != null )
            {
                return this.httpMethod + "_" + this.path.replaceAll( "[/{}]", "_" );
            }
        }
        return this.id;
    }

    /**
     * @param id is the id to set
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * @return the path
     */
    public String getPath()
    {
        return this.path;
    }

    /**
     * @param path is the path to set
     */
    public void setPath( String path )
    {
        this.path = path;
    }

    /**
     * @return the method
     */
    public String getHttpMethod()
    {
        return this.httpMethod;
    }

    /**
     * @param method is the method to set
     */
    public void setHttpMethod( String method )
    {
        this.httpMethod = method;
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
     * @return the deprecated
     */
    public boolean isDeprecated()
    {
        return this.deprecated;
    }

    /**
     * @param deprecated is the deprecated to set
     */
    public void setDeprecated( boolean deprecated )
    {
        this.deprecated = deprecated;
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
     * @return the parameters
     */
    public List<ParameterDescriptor> getParameters()
    {
        if ( this.parameters == null )
        {
            this.parameters = new ArrayList<ParameterDescriptor>();
        }
        return this.parameters;
    }

    /**
     * @param parameters is the parameters to set
     */
    public void setParameters( List<ParameterDescriptor> parameters )
    {
        this.parameters = parameters;
    }

    /**
     * @return the responses
     */
    public List<ResponseDescriptor> getResponses()
    {
        if ( this.responses == null )
        {
            this.responses = new ArrayList<ResponseDescriptor>();
        }
        return this.responses;
    }

    /**
     * @param responses is the responses to set
     */
    public void setResponses( List<ResponseDescriptor> responses )
    {
        this.responses = responses;
    }

    /**
     * @return the javaMethod
     */
    public JMethod getJavaMethod()
    {
        return this.javaMethod;
    }

    /**
     * @param javaMethod is the javaMethod to set
     */
    public void setJavaMethod( JMethod javaMethod )
    {
        this.javaMethod = javaMethod;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo( OperationDescriptor o )
    {
        if ( o == null )
        {
            return 1;
        }
        int result = this.httpMethod.compareTo( o.httpMethod );
        if ( result == 0 )
        {
            result = this.path.compareTo( o.path );
        }
        return result;
    }

}