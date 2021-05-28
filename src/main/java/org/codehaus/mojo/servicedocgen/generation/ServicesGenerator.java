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
package org.codehaus.mojo.servicedocgen.generation;

import java.io.File;
import java.io.IOException;

import org.codehaus.mojo.servicedocgen.descriptor.ServicesDescriptor;

/**
 * Interface for a generator that creates documentation for a given {@link ServicesDescriptor}.
 *
 * @see #generate(ServicesDescriptor, File, String)
 * @author hohwille
 */
public interface ServicesGenerator
{

    /**
     * Generates the documentation as output.
     *
     * @param descriptor the {@link ServicesDescriptor} with the collected meta-data.
     * @param outputDirectory the {@link File#isDirectory() directory} where to write the output to.
     * @param filename the name of the file to write the output to.
     * @throws IOException if something goes wrong.
     */
    void generate( ServicesDescriptor descriptor, File outputDirectory, String filename )
        throws IOException;

}
