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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import net.sf.mmm.util.math.api.NumberType;
import net.sf.mmm.util.math.base.MathUtilImpl;
import net.sf.mmm.util.reflect.api.AnnotationUtil;
import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.reflect.api.ReflectionUtil;
import net.sf.mmm.util.reflect.base.AnnotationUtilImpl;
import net.sf.mmm.util.reflect.base.ReflectionUtilImpl;
import net.sf.mmm.util.validation.base.Mandatory;

import org.apache.maven.model.Developer;
import org.apache.maven.model.Organization;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.servicedocgen.descriptor.ContactDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.Descriptor;
import org.codehaus.mojo.servicedocgen.descriptor.InfoDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.LicenseDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.OperationDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ParameterDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ResponseDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ServiceDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ServicesDescriptor;
import org.codehaus.mojo.servicedocgen.generation.ServicesGenerator;
import org.codehaus.mojo.servicedocgen.generation.velocity.VelocityServicesGenerator;
import org.codehaus.mojo.servicedocgen.introspection.JElement;
import org.codehaus.mojo.servicedocgen.introspection.JException;
import org.codehaus.mojo.servicedocgen.introspection.JMethod;
import org.codehaus.mojo.servicedocgen.introspection.JParameter;
import org.codehaus.mojo.servicedocgen.introspection.JType;
import org.codehaus.mojo.servicedocgen.introspection.JavaDocHelper;

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

    private ClassLoader projectClassloader;

    private ReflectionUtil reflectionUtil;

    private AnnotationUtil annotationUtil;

    private JavaDocHelper javaDocHelper;

    /**
     * {@inheritDoc}
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( this.project.getParent() != null )
        {
            getLog().debug( "" + this.project.getParent().getBasedir() );
        }

        if ( this.reflectionUtil == null )
        {
            this.reflectionUtil = ReflectionUtilImpl.getInstance();
        }
        if ( this.annotationUtil == null )
        {
            this.annotationUtil = AnnotationUtilImpl.getInstance();
        }

        this.classnamePattern = Pattern.compile( this.classnameRegex );

        JavaProjectBuilder builder = new JavaProjectBuilder();
        if ( !Util.isEmpty( this.sourceEncoding ) )
        {
            builder.setEncoding( this.sourceEncoding );
        }

        try
        {
            List<JavaClass> serviceClasses = scanServices( builder );
            this.javaDocHelper = new JavaDocHelper( getProjectClassloader(), builder, this.descriptor.getJavadocs() );
            ServicesDescriptor services = createServicesDescriptor( serviceClasses );

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

    private ServicesDescriptor createServicesDescriptor( List<JavaClass> serviceClasses )
        throws Exception
    {
        if ( this.descriptor == null )
        {
            this.descriptor = new ServicesDescriptor();
        }
        createInfoDescriptor();
        Set<String> descriptorSchemes = this.descriptor.getSchemes();
        if ( descriptorSchemes.isEmpty() )
        {
            descriptorSchemes.add( Descriptor.SCHEME_HTTPS );
        }
        for ( JavaClass type : serviceClasses )
        {
            ServiceDescriptor service = createServiceDescriptor( type, this.descriptor );
            this.descriptor.getServices().add( service );
        }
        return this.descriptor;
    }

    private InfoDescriptor createInfoDescriptor()
    {
        InfoDescriptor info = this.descriptor.getInfo();
        if ( info.getVersion() == null )
        {
            info.setVersion( this.project.getVersion() );
        }
        if ( info.getTitle() == null )
        {
            info.setTitle( this.project.getArtifactId() );
        }
        if ( info.getDescription() == null )
        {
            info.setDescription( this.project.getDescription() );
        }
        ContactDescriptor contact = info.getContact();
        if ( contact == null )
        {
            Organization organization = this.project.getOrganization();
            if ( organization != null )
            {
                contact = new ContactDescriptor();
                contact.setUrl( organization.getUrl() );
                contact.setName( organization.getName() );
                info.setContact( contact );
            }
            else
            {
                String url = Util.getTrimmed( this.project.getUrl() );
                if ( !url.isEmpty() )
                {
                    contact = new ContactDescriptor();
                    contact.setUrl( url );
                    String name = Util.getTrimmed( this.project.getName() );
                    if ( name.isEmpty() )
                    {
                        name = Util.getTrimmed( this.project.getArtifactId() );
                    }
                    contact.setName( name );
                    info.setContact( contact );
                }
                else
                {
                    List<Developer> developers = this.project.getDevelopers();
                    if ( ( developers != null ) && ( !developers.isEmpty() ) )
                    {
                        contact = new ContactDescriptor();
                        Developer developer = developers.get( 0 );
                        contact.setUrl( Util.getTrimmed( developer.getUrl() ) );
                        contact.setEmail( Util.getTrimmed( developer.getEmail() ) );
                        contact.setName( Util.getTrimmed( developer.getName() ) );
                        info.setContact( contact );
                    }
                }
            }
        }
        LicenseDescriptor serviceLicense = info.getLicense();
        if ( serviceLicense == null )
        {
            List<org.apache.maven.model.License> licenses = this.project.getLicenses();
            if ( ( licenses != null ) && ( !licenses.isEmpty() ) )
            {
                serviceLicense = new LicenseDescriptor();
                org.apache.maven.model.License projectLicense = licenses.get( 0 );
                serviceLicense.setName( projectLicense.getName() );
                serviceLicense.setUrl( projectLicense.getUrl() );
                info.setLicense( serviceLicense );
            }
        }
        return info;
    }

    private ServiceDescriptor createServiceDescriptor( JavaClass sourceType, ServicesDescriptor servicesDescriptor )
        throws Exception
    {
        getLog().info( "Analyzing " + sourceType.getName() );
        ServiceDescriptor serviceDescriptor = new ServiceDescriptor();
        serviceDescriptor.setName( sourceType.getName() );
        Class<?> byteClass = getProjectClassloader().loadClass( sourceType.getFullyQualifiedName() );
        Path serviceBasePath = byteClass.getAnnotation( Path.class );
        if ( serviceBasePath != null )
        {
            serviceDescriptor.setBasePath( serviceBasePath.value() );
        }
        GenericType<?> byteType = this.reflectionUtil.createGenericType( byteClass );
        serviceDescriptor.setJavaType( new JType( byteType, sourceType, this.reflectionUtil, this.javaDocHelper ) );
        serviceDescriptor.setDescription( this.javaDocHelper.parseJavaDoc( sourceType, byteType,
                                                                           sourceType.getComment() ) );
        Consumes consumes = this.annotationUtil.getTypeAnnotation( byteClass, Consumes.class );
        addConsumes( serviceDescriptor.getConsumes(), consumes );
        Produces produces = this.annotationUtil.getTypeAnnotation( byteClass, Produces.class );
        addProduces( serviceDescriptor.getProduces(), produces );
        for ( Method byteMethod : byteClass.getMethods() )
        {
            getLog().debug( "Analyzing method " + byteMethod.toString() );
            OperationDescriptor operationDescriptor = createOperationDescriptor( serviceDescriptor, byteMethod );
            if ( operationDescriptor != null )
            {
                getLog().debug( "Method has been detected as service operation." );
                serviceDescriptor.getOperations().add( operationDescriptor );
            }
        }
        Collections.sort( serviceDescriptor.getOperations() );
        return serviceDescriptor;
    }

    private OperationDescriptor createOperationDescriptor( ServiceDescriptor serviceDescriptor, Method byteMethod )
    {
        Method annotatedParentMethod = byteMethod;
        Path methodPath = null;
        do
        {
            methodPath = annotatedParentMethod.getAnnotation( Path.class );
            if ( methodPath == null )
            {
                annotatedParentMethod = this.reflectionUtil.getParentMethod( annotatedParentMethod );
                if ( annotatedParentMethod == null )
                {
                    return null;
                }
            }
        }
        while ( methodPath == null );

        OperationDescriptor operationDescriptor = new OperationDescriptor();
        operationDescriptor.setPath( methodPath.value() );

        if ( this.annotationUtil.getMethodAnnotation( byteMethod, Deprecated.class ) != null )
        {
            operationDescriptor.setDeprecated( true );
        }
        JMethod method = new JMethod( byteMethod, serviceDescriptor.getJavaType(), annotatedParentMethod );
        operationDescriptor.setJavaMethod( method );
        operationDescriptor.setDescription( method.getComment() );

        Set<String> consumes = operationDescriptor.getConsumes();
        addConsumes( consumes, annotatedParentMethod.getAnnotation( Consumes.class ) );
        if ( consumes.isEmpty() )
        {
            consumes.addAll( serviceDescriptor.getConsumes() );
        }
        Set<String> produces = operationDescriptor.getProduces();
        addProduces( produces, annotatedParentMethod.getAnnotation( Produces.class ) );
        if ( produces.isEmpty() )
        {
            produces.addAll( serviceDescriptor.getProduces() );
        }

        operationDescriptor.setHttpMethod( createHttpMethodDescriptor( method ) );

        // parameters
        for ( JParameter parameter : method.getParameters() )
        {
            ParameterDescriptor parameterDescriptor =
                createParameterDescriptor( serviceDescriptor, operationDescriptor, parameter );
            if ( parameterDescriptor != null )
            {
                operationDescriptor.getParameters().add( parameterDescriptor );
            }
        }

        // responses
        ResponseDescriptor responseSuccess =
            createResponseDescriptor( serviceDescriptor, method.getReturns(), "Success" );
        operationDescriptor.getResponses().add( responseSuccess );

        for ( JException exception : method.getExceptions() )
        {
            ResponseDescriptor response = createResponseDescriptor( serviceDescriptor, exception, "Error" );
            operationDescriptor.getResponses().add( response );
        }

        return operationDescriptor;
    }

    private ParameterDescriptor createParameterDescriptor( ServiceDescriptor serviceDescriptor,
                                                           OperationDescriptor operationDescriptor, JParameter parameter )
    {
        ParameterDescriptor parameterDescriptor = new ParameterDescriptor();
        parameterDescriptor.setJavaParameter( parameter );
        parameterDescriptor.setName( parameter.getName() );
        parameterDescriptor.setDescription( parameter.getComment() );
        String location = Descriptor.LOCATION_BODY;
        boolean required = false;
        for ( Annotation annotation : parameter.getByteAnnotations() )
        {
            if ( annotation instanceof QueryParam )
            {
                QueryParam queryParam = (QueryParam) annotation;
                location = Descriptor.LOCATION_QUERY;
                parameterDescriptor.setName( queryParam.value() );
            }
            else if ( annotation instanceof HeaderParam )
            {
                HeaderParam headerParam = (HeaderParam) annotation;
                location = Descriptor.LOCATION_HEADER;
                parameterDescriptor.setName( headerParam.value() );
            }
            else if ( annotation instanceof PathParam )
            {
                PathParam pathParam = (PathParam) annotation;
                location = Descriptor.LOCATION_PATH;
                parameterDescriptor.setName( pathParam.value() );
            }
            else if ( annotation instanceof FormParam )
            {
                FormParam formParam = (FormParam) annotation;
                location = Descriptor.LOCATION_FORM_DATA;
                parameterDescriptor.setName( formParam.value() );
            }
            else if ( annotation instanceof CookieParam )
            {
                CookieParam cookieParam = (CookieParam) annotation;
                location = Descriptor.LOCATION_COOKIE;
                parameterDescriptor.setName( cookieParam.value() );
            }
            else if ( annotation instanceof Context )
            {
                if ( UriInfo.class.isAssignableFrom( parameter.getByteType().getAssignmentClass() ) )
                {
                    location = "query/path";
                }
                else
                {
                    return null;
                }
            }
            else if ( annotation instanceof DefaultValue )
            {
                DefaultValue defaultValue = (DefaultValue) annotation;
                parameterDescriptor.setDefaultValue( defaultValue.value() );
                ;
            }
            else if ( annotation instanceof NotNull )
            {
                required = true;
            }
            else if ( annotation instanceof Mandatory )
            {
                required = true;
            }
        }
        parameterDescriptor.setLocation( location );
        parameterDescriptor.setRequired( required );
        JavaScriptType javaScriptType = getJavaScriptType( parameter.getByteType().getAssignmentClass() );
        parameterDescriptor.setJavaScriptType( javaScriptType.getName() );
        parameterDescriptor.setExample( javaScriptType.getExample() );
        return parameterDescriptor;
    }

    private ResponseDescriptor createResponseDescriptor( ServiceDescriptor serviceDescriptor, JElement javaElement,
                                                         String reason )
    {
        ResponseDescriptor response = new ResponseDescriptor();
        GenericType<?> byteReturnType = javaElement.getByteType();
        if ( byteReturnType.getRetrievalClass() == void.class )
        {
            response.setStatusCode( Descriptor.STATUS_CODE_NO_CONTENT );
            response.setDescription( "No content" );
        }
        else
        {
            response.setStatusCode( Descriptor.STATUS_CODE_SUCCESS );
            response.setDescription( javaElement.getComment() );
        }
        response.setReason( reason );
        response.setJavaElement( javaElement );
        JavaScriptType javaScriptType = getJavaScriptType( byteReturnType.getAssignmentClass() );
        response.setJavaScriptType( javaScriptType.getName() );
        response.setExample( javaScriptType.getExample() );
        return response;
    }

    private JavaScriptType getJavaScriptType( Class<?> clazz )
    {
        Class<?> byteClass = this.reflectionUtil.getNonPrimitiveType( clazz );
        if ( Number.class.isAssignableFrom( byteClass ) )
        {
            NumberType<? extends Number> numberType = MathUtilImpl.getInstance().getNumberType( byteClass );
            if ( ( numberType == null ) || numberType.isDecimal() )
            {
                return JavaScriptType.NUMBER;
            }
            else
            {
                return JavaScriptType.INTEGER;
            }
        }
        else if ( byteClass.isArray() || ( Collection.class.isAssignableFrom( byteClass ) ) )
        {
            return JavaScriptType.ARRAY;
        }
        else if ( Boolean.class.equals( byteClass ) )
        {
            return JavaScriptType.BOOLEAN;
        }
        else if ( Void.class.isAssignableFrom( byteClass ) )
        {
            return JavaScriptType.VOID;
        }
        else if ( CharSequence.class.isAssignableFrom( byteClass ) )
        {
            return JavaScriptType.STRING;
        }
        return JavaScriptType.OBJECT;
    }

    private String createHttpMethodDescriptor( JMethod method )
    {
        Method byteMethod = method.getByteMethod();
        if ( byteMethod.isAnnotationPresent( GET.class ) )
        {
            return Descriptor.HTTP_METHOD_GET;
        }
        else if ( byteMethod.isAnnotationPresent( PUT.class ) )
        {
            return Descriptor.HTTP_METHOD_PUT;
        }
        else if ( byteMethod.isAnnotationPresent( POST.class ) )
        {
            return Descriptor.HTTP_METHOD_POST;
        }
        else if ( byteMethod.isAnnotationPresent( DELETE.class ) )
        {
            return Descriptor.HTTP_METHOD_DELETE;
        }
        else if ( byteMethod.isAnnotationPresent( OPTIONS.class ) )
        {
            return Descriptor.HTTP_METHOD_OPTIONS;
        }
        else if ( byteMethod.isAnnotationPresent( HEAD.class ) )
        {
            return Descriptor.HTTP_METHOD_HEAD;
        }
        else
        {
            getLog().warn( "Service method " + method + " is missing JAX-RS annotation for HTTP method!" );
            return null;
        }
    }

    private void addConsumes( Set<String> set, Consumes consumes )
    {

        if ( consumes == null )
        {
            return;
        }
        for ( String mimeType : consumes.value() )
        {
            set.add( mimeType );
        }
    }

    private void addProduces( Set<String> set, Produces produces )
    {

        if ( produces == null )
        {
            return;
        }
        for ( String mimeType : produces.value() )
        {
            set.add( mimeType );
        }
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
