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

import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.reflect.api.ReflectionUtil;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * Rich representation of a {@link Class} with both byte-code and source-code analysis.
 *
 * @see JMethod
 * @author hohwille
 */
public class JType
    extends JElement
{

    private final ReflectionUtil reflectionUtil;

    private final JavaDocHelper javaDocHelper;

    /**
     * The constructor.
     *
     * @param byteType - see {@link #getByteType()}.
     * @param sourceType - see {@link #getSourceType()}.
     * @param reflectionUtil the {@link ReflectionUtil} used for byte-code analysis.
     * @param javaDocHelper the {@link JavaDocHelper} for parsing JavaDoc in source-code analysis.
     */
    public JType( GenericType<?> byteType, JavaClass sourceType, ReflectionUtil reflectionUtil,
                  JavaDocHelper javaDocHelper )
    {
        super( byteType, sourceType, ( sourceType == null ) ? "" : javaDocHelper.parseJavaDoc( sourceType, byteType,
                                                                                               sourceType.getComment() ) );
        this.reflectionUtil = reflectionUtil;
        this.javaDocHelper = javaDocHelper;
    }

    /**
     * @return the {@link ReflectionUtil} used for byte-code analysis.
     */
    ReflectionUtil getReflectionUtil()
    {
        return this.reflectionUtil;
    }

    /**
     * @return the {@link JavaDocHelper} for parsing JavaDoc in source-code analysis.
     */
    JavaDocHelper getJavaDocHelper()
    {
        return this.javaDocHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return getByteTypeString();
    }

}
