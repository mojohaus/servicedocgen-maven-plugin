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
package org.codehaus.mojo.servicedocgen.generation.velocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.ToolManager;
import org.codehaus.mojo.servicedocgen.EscapeHelper;
import org.codehaus.mojo.servicedocgen.descriptor.ServicesDescriptor;
import org.codehaus.mojo.servicedocgen.generation.ServicesGenerator;

/**
 * Implementation of {@link ServicesDescriptor} based on apache velocity.
 *
 * @author hohwille
 */
public class VelocityServicesGenerator
    implements ServicesGenerator
{

    private static final String ENCODING = "UTF-8";

    private final String templatePath;

    private final Context context;

    private Template template;

    private final VelocityEngine engine;

    /**
     * The constructor.
     *
     * @param templatePath the classpath location where to look for velocity templates.
     */
    public VelocityServicesGenerator( String templatePath )
    {
        super();
        this.engine = new VelocityEngine();
        this.engine.setProperty( RuntimeConstants.RESOURCE_LOADER, "classpath" );
        this.engine.setProperty( "classpath.resource.loader.class", ClasspathResourceLoader.class.getName() );
        this.engine.init();

        ToolManager manager = new ToolManager();
        this.context = manager.createContext();
        this.templatePath = templatePath;
        this.template = this.engine.getTemplate( templatePath, ENCODING );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate( ServicesDescriptor descriptor, File outputDirectory, String filename )
        throws IOException
    {
        this.context.put( "services", descriptor );
        this.context.put( "EscapeHelper", EscapeHelper.class );
        File outputFile = new File( outputDirectory, filename );
        OutputStream out = new FileOutputStream( outputFile );
        try
        {
            Writer writer = new OutputStreamWriter( out, ENCODING );
            this.template.merge( this.context, writer );
            writer.close();
        }
        finally
        {
            out.close();
        }
    }
}
