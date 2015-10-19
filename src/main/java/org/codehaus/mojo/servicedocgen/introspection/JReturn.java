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

import com.thoughtworks.qdox.model.JavaClass;

/**
 * An return declaration of a {@link JMethod}.
 *
 * @see JMethod#getReturns()
 * @author hohwille
 */
public class JReturn
    extends JElement
{

    /**
     * The constructor.
     *
     * @param byteType - see {@link #getByteType()}.
     * @param sourceType - see {@link #getSourceType()}.
     * @param comment - see {@link #getComment()}.
     */
    public JReturn( GenericType<?> byteType, JavaClass sourceType, String comment )
    {
        super( byteType, sourceType, comment );
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
