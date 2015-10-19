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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.mojo.servicedocgen.introspection.JType;

/**
 * {@link Descriptor} of a service.
 *
 * @see ServicesDescriptor#getServices()
 * @author hohwille
 */
public class ServiceDescriptor
    extends AbstractDescriptor
{

    private String id;

    private String name;

    private String description;

    private String basePath;

    private List<OperationDescriptor> operations;

    private Set<String> consumes;

    private Set<String> produces;

    private JType javaType;

    /**
     * @return the id
     */
    public String getId()
    {
        if ( this.id == null )
        {
            if ( this.javaType != null )
            {
                return this.javaType.getByteType().getRetrievalClass().getName().replaceAll( "[.$]", "_" );
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
     * @return the paths
     */
    public List<OperationDescriptor> getOperations()
    {
        if ( this.operations == null )
        {
            this.operations = new ArrayList<OperationDescriptor>();
        }
        return this.operations;
    }

    /**
     * @return a {@link Map} {@link Map#get(Object) mapping} from {@link OperationDescriptor#getHttpMethod()
     *         HTTP-method} to {@link OperationDescriptor}.
     */
    public Map<String, List<OperationDescriptor>> getOperationsGroupedByHttpMethod()
    {

        Map<String, List<OperationDescriptor>> map = new HashMap<String, List<OperationDescriptor>>();
        for ( OperationDescriptor path : getOperations() )
        {
            List<OperationDescriptor> pathList = map.get( path.getHttpMethod() );
            if ( pathList == null )
            {
                pathList = new ArrayList<OperationDescriptor>();
                map.put( path.getHttpMethod(), pathList );
            }
            pathList.add( path );
        }
        return map;
    }

    /**
     * @param paths is the paths to set
     */
    public void setOperations( List<OperationDescriptor> paths )
    {
        this.operations = paths;
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
     * @return the javaType
     */
    public JType getJavaType()
    {
        return this.javaType;
    }

    /**
     * @param javaType is the javaType to set
     */
    public void setJavaType( JType javaType )
    {
        this.javaType = javaType;
    }

}
