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
package org.codehaus.mojo.servicedocgen;

/**
 * @author hohwille
 */
public enum JavaScriptType
{

    NUMBER( "number", "1.0" ),

    INTEGER( "integer", "1" ),

    BOOLEAN( "boolean", "true" ),

    ARRAY( "array", "[...]" ),

    STRING( "string", "'foo'" ),

    OBJECT( "object", "{...}" );

    private final String name;

    private final String example;

    private JavaScriptType( String name, String example )
    {
        this.name = name;
        this.example = example;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return the example
     */
    public String getExample()
    {
        return this.example;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return this.name;
    }

}
