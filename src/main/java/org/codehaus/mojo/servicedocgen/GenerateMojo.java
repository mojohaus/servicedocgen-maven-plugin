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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.Path;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.servicedocgen.descriptor.ServicesDescriptor;
import org.codehaus.mojo.servicedocgen.generation.ServicesGenerator;
import org.codehaus.mojo.servicedocgen.generation.velocity.VelocityServicesGenerator;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * {@link AbstractMojo Maven Plugin} to automatically generate documentation for services of the current project.
 * <ol>
 * <li>Scans the current projects source code for (JAX-RS annotated) services that match the RegEx configured by
 * <code>classnameRegex</code>.</li>
 * <li>Analyzes the services from source-code (extract JavaDoc, etc.) and byte-code (resolve generic parameters, etc.)
 * and create intermediate meta-data as {@link ServicesDescriptor}.</li>
 * <li>Generates documentation from the collected meta-data (by default as HTML from a velocity template shipped with
 * this plugin but can be overridden via configuration parameters).</li>
 * </ol>
 * scan and analyze services from the current project via source-code and byte-code analysis. Creates
 *
 * @author hohwille
 */
@Mojo( name = "generate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresProject = true, requiresDirectInvocation = false, executionStrategy = "once-per-session", requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME )
public class GenerateMojo
    extends AbstractMojo
{

    /**
     * The directory where the generated service documentation will be written to.
     */
    @Parameter( defaultValue = "${project.build.directory}/servicedoc" )
    private File outputDirectory;

    /**
     * The fully qualified classname of the service to generate. Empty for auto-discovery (default).
     */
    @Parameter
    private String serviceClassName;

    /**
     * The regex {@link Pattern} that service classes have to match.
     */
    @Parameter( defaultValue = ".*Service.*" )
    private String classnameRegex;

    private Pattern classnamePattern;

    /**
     * The Maven Project.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    @Parameter( required = false )
    private ServicesDescriptor descriptor;

    @Parameter( defaultValue = "${project.runtimeClasspathElements}", readonly = true )
    private List<String> runtimeClasspathElements;

    @Parameter( defaultValue = "org/codehaus/mojo/servicedocgen/generation/velocity" )
    private String templatePath;

    @Parameter( defaultValue = "Service-Documentation.html.vm" )
    private String templateName;

    @Parameter( defaultValue = "${project.build.sourceEncoding}" )
    private String sourceEncoding;

    /**
     * Set to <code>true</code> if you want to introspect fields of Java beans and <code>false</code> for getters.
     */
    @Parameter( defaultValue = "false" )
    private boolean introspectFields;

    private ClassLoader projectClassloader;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        this.classnamePattern = Pattern.compile( this.classnameRegex );

        JavaProjectBuilder builder = new JavaProjectBuilder();
        if ( !Util.isEmpty( this.sourceEncoding ) )
        {
            builder.setEncoding( this.sourceEncoding );
        }

        try
        {
            List<JavaClass> serviceClasses = scanServices( builder );
            if ( serviceClasses.isEmpty() )
            {
                getLog().info( "No services found - omitting service documentation generation." );
                return;
            }
            Analyzer analyzer =
                new Analyzer( getLog(), this.project, getProjectClassloader(), builder, this.descriptor,
                              this.introspectFields );
            ServicesDescriptor services = analyzer.createServicesDescriptor( serviceClasses );

            getLog().info( "Generating output..." );
            ServicesGenerator generator =
                new VelocityServicesGenerator( Util.appendPath( this.templatePath, this.templateName ) );
            if ( !this.outputDirectory.isDirectory() )
            {
                boolean ok = this.outputDirectory.mkdirs();
                if ( !ok )
                {
                    throw new MojoExecutionException( "Could not create directory " + this.outputDirectory );
                }
            }
            generator.generate( services, this.outputDirectory );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Unexpected Error!", e );
        }
    }

    private List<JavaClass> scanServices( JavaProjectBuilder builder )
        throws IOException
    {
        List<JavaClass> serviceClasses = new ArrayList<JavaClass>();
        for ( String sourceDir : this.project.getCompileSourceRoots() )
        {
            File sourceFolder = new File( sourceDir );
            builder.addSourceFolder( sourceFolder );
            if ( this.serviceClassName == null )
            {
                scanJavaFilesRecursive( sourceFolder, builder, serviceClasses );
            }
        }
        if ( this.serviceClassName != null )
        {
            JavaClass type = builder.getClassByName( this.serviceClassName );
            boolean isService = isServiceClass( type );
            if ( isService )
            {
                serviceClasses.add( type );
            }
        }
        return serviceClasses;
    }

    private void scanJavaFilesRecursive( File sourceDir, JavaProjectBuilder builder, List<JavaClass> serviceClasses )
        throws IOException
    {
        for ( File file : sourceDir.listFiles() )
        {
            if ( file.isDirectory() )
            {
                scanJavaFilesRecursive( file, builder, serviceClasses );
            }
            else
            {
                JavaSource source = builder.addSource( file );
                for ( JavaClass type : source.getClasses() )
                {
                    boolean isService = isServiceClass( type );
                    if ( isService )
                    {
                        serviceClasses.add( type );
                    }
                }
            }
        }

    }

    private boolean isServiceClass( JavaClass type )
    {
        if ( this.classnamePattern.matcher( type.getName() ).matches() )
        {
            getLog().debug( "Class matches: " + type.getName() );
            for ( JavaAnnotation annotation : type.getAnnotations() )
            {
                if ( Path.class.getName().equals( annotation.getType().getFullyQualifiedName() ) )
                {
                    getLog().info( "Found service: " + type.getName() );
                    return true;
                }
            }
        }
        return false;
    }

    private ClassLoader getProjectClassloader()
        throws MojoExecutionException
    {
        if ( this.projectClassloader == null )
        {
            this.projectClassloader = new URLClassLoader( buildClasspathUrls(), this.getClass().getClassLoader() );
        }
        return this.projectClassloader;
    }

    private URL[] buildClasspathUrls()
        throws MojoExecutionException
    {
        List<URL> urls = new ArrayList<URL>( this.runtimeClasspathElements.size() );
        for ( String element : this.runtimeClasspathElements )
        {
            try
            {
                urls.add( new File( element ).toURI().toURL() );
            }
            catch ( MalformedURLException e )
            {
                throw new MojoExecutionException( "Unable to access project dependency: " + element, e );
            }
        }
        return urls.toArray( new URL[urls.size()] );
    }

}
