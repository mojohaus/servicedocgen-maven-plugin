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
package org.codehaus.mojo.servicedocgen.introspection;

import net.sf.mmm.util.lang.api.Visitor;
import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.reflect.base.ReflectionUtilImpl;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author hohwille
 */
public abstract class JElement
{

    private final GenericType<?> byteType;

    private final JavaClass sourceType;

    private final String comment;

    /**
     * The constructor.
     *
     * @param byteType - see {@link #getByteType()}.
     * @param sourceType - see {@link #getSourceType()}.
     * @param comment - see {@link #getComment()}.
     */
    public JElement( GenericType<?> byteType, JavaClass sourceType, String comment )
    {
        super();
        this.sourceType = sourceType;
        this.comment = comment;
        this.byteType = byteType;
    }

    /**
     * @return the type of the byte-code analysis.
     */
    public GenericType<?> getByteType()
    {
        return this.byteType;
    }

    /**
     * @return a compact string representation of {@link #getByteType()}.
     */
    public String getByteTypeString()
    {
        final StringBuilder appendable = new StringBuilder();
        Visitor<Class<?>> classFormatter = new Visitor<Class<?>>()
        {
            public void visit( Class<?> value )
            {
                appendable.append( value.getSimpleName() );
            }
        };
        ReflectionUtilImpl.getInstance().toString( getByteType(), appendable, classFormatter );
        return appendable.toString();
    }

    /**
     * @return the type of the source-code analysis. May be <code>null</code>.
     */
    public JavaClass getSourceType()
    {
        return this.sourceType;
    }

    /**
     * @return the JavaDoc comment. Will be empty if not available.
     */
    public String getComment()
    {
        return this.comment;
    }
}
