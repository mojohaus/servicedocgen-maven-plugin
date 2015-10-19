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

import java.lang.annotation.Annotation;

import net.sf.mmm.util.reflect.api.GenericType;

import com.thoughtworks.qdox.model.JavaParameter;

/**
 * A parameter declaration of a {@link JMethod}.
 *
 * @see JMethod#getParameters()
 * @author hohwille
 */
public class JParameter
    extends JElement
{
    private final JavaParameter parameter;

    private final Annotation[] byteAnnotations;

    private final int index;

    /**
     * The constructor.
     *
     * @param byteType - see {@link #getByteType()}.
     * @param byteAnnotations - see {@link #getByteAnnotations()}.
     * @param parameter - see {@link #getParameter()}.
     * @param comment - see {@link #getComment()}.
     * @param index the index of the parameter in the method arguments.
     */
    public JParameter( GenericType<?> byteType, Annotation[] byteAnnotations, JavaParameter parameter, String comment,
                       int index )
    {
        super( byteType, parameter.getJavaClass(), comment );
        this.parameter = parameter;
        this.byteAnnotations = byteAnnotations;
        this.index = index;
    }

    /**
     * @return the {@link JavaParameter} from source-code analysis. May be <code>null</code>.
     */
    public JavaParameter getParameter()
    {
        return this.parameter;
    }

    /**
     * @return the array of {@link Annotation}s from byte-code analysis. Do not modify.
     */
    public Annotation[] getByteAnnotations()
    {
        return this.byteAnnotations;
    }

    /**
     * @return the name of the parameter (from source-code if available).
     */
    public String getName()
    {
        if ( this.parameter == null )
        {
            return "arg" + this.index;
        }
        return this.parameter.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return getName();
    }

}
