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

import net.sf.mmm.util.reflect.api.GenericType;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author hohwille
 */
public class ServiceDescriptor
    extends AbstractDescriptor
{

    private String id;

    private String name;

    private String description;

    private String basePath;

    private List<PathDescriptor> paths;

    private Set<String> consumes;

    private Set<String> produces;

    private GenericType<?> javaByteType;

    private JavaClass javaSourceType;

    /**
     * @return the id
     */
    public String getId()
    {
        if ( this.id == null )
        {
            if ( this.javaByteType != null )
            {
                return this.javaByteType.getRetrievalClass().getName().replaceAll( "[.$]", "_" );
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
    public List<PathDescriptor> getPaths()
    {
        if ( this.paths == null )
        {
            this.paths = new ArrayList<PathDescriptor>();
        }
        return this.paths;
    }

    public Map<String, List<PathDescriptor>> getPathsGroupedByHttpMethod()
    {

        Map<String, List<PathDescriptor>> map = new HashMap<String, List<PathDescriptor>>();
        for ( PathDescriptor path : getPaths() )
        {
            List<PathDescriptor> pathList = map.get( path.getHttpMethod() );
            if ( pathList == null )
            {
                pathList = new ArrayList<PathDescriptor>();
                map.put( path.getHttpMethod(), pathList );
            }
            pathList.add( path );
        }
        return map;
    }

    /**
     * @param paths is the paths to set
     */
    public void setPaths( List<PathDescriptor> paths )
    {
        this.paths = paths;
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
     * @return the javaByteClass
     */
    public GenericType<?> getJavaByteType()
    {
        return this.javaByteType;
    }

    /**
     * @param javaByteClass is the javaByteClass to set
     */
    public void setJavaByteType( GenericType<?> javaByteClass )
    {
        this.javaByteType = javaByteClass;
    }

    /**
     * @return the javaSourceClass
     */
    public JavaClass getJavaSourceType()
    {
        return this.javaSourceType;
    }

    /**
     * @param javaSourceClass is the javaSourceClass to set
     */
    public void setJavaSourceType( JavaClass javaSourceClass )
    {
        this.javaSourceType = javaSourceClass;
    }

}
