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
import java.lang.reflect.Type;
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
import org.codehaus.mojo.servicedocgen.descriptor.Descriptor;
import org.codehaus.mojo.servicedocgen.descriptor.InfoDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.InfoDescriptor.ContactDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.InfoDescriptor.LicenseDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ParameterDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.PathDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ResponseDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ServiceDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ServicesDescriptor;
import org.codehaus.mojo.servicedocgen.generation.ServicesGenerator;
import org.codehaus.mojo.servicedocgen.generation.velocity.VelocityServicesGenerator;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

/**
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
        if ( !isEmpty( this.sourceEncoding ) )
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
                new VelocityServicesGenerator( appendPath( this.templatePath, this.templateName ) );
            boolean ok = this.outputDirectory.mkdirs();
            if ( !ok )
            {
                throw new MojoExecutionException( "Could not create directory " + this.outputDirectory );
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
                String url = getTrimmed( this.project.getUrl() );
                if ( !url.isEmpty() )
                {
                    contact = new ContactDescriptor();
                    contact.setUrl( url );
                    String name = getTrimmed( this.project.getName() );
                    if ( name.isEmpty() )
                    {
                        name = getTrimmed( this.project.getArtifactId() );
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
                        contact.setUrl( getTrimmed( developer.getUrl() ) );
                        contact.setEmail( getTrimmed( developer.getEmail() ) );
                        contact.setName( getTrimmed( developer.getName() ) );
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

    private String getTrimmed( String value )
    {

        if ( value == null )
        {
            return "";
        }
        return value.trim();
    }

    private boolean isEmpty( String value )
    {

        if ( value == null )
        {
            return true;
        }
        return value.isEmpty();
    }

    private ServiceDescriptor createServiceDescriptor( JavaClass sourceClass, ServicesDescriptor servicesDescriptor )
        throws Exception
    {
        getLog().info( "Analyzing " + sourceClass.getName() );
        ServiceDescriptor serviceDescriptor = new ServiceDescriptor();
        serviceDescriptor.setJavaSourceType( sourceClass );
        serviceDescriptor.setName( sourceClass.getName() );
        Class<?> byteClass = getProjectClassloader().loadClass( sourceClass.getFullyQualifiedName() );
        Path serviceBasePath = byteClass.getAnnotation( Path.class );
        if ( serviceBasePath != null )
        {
            serviceDescriptor.setBasePath( serviceBasePath.value() );
        }
        GenericType<?> byteType = this.reflectionUtil.createGenericType( byteClass );
        serviceDescriptor.setJavaByteType( byteType );
        serviceDescriptor.setDescription( this.javaDocHelper.parseJavaDoc( sourceClass, byteType,
                                                                           sourceClass.getComment() ) );
        Consumes consumes = this.annotationUtil.getClassAnnotation( byteClass, Consumes.class );
        addConsumes( serviceDescriptor.getConsumes(), consumes );
        Produces produces = this.annotationUtil.getClassAnnotation( byteClass, Produces.class );
        addProduces( serviceDescriptor.getProduces(), produces );
        for ( Method byteMethod : byteClass.getMethods() )
        {
            getLog().debug( "Analyzing method " + byteMethod.toString() );
            PathDescriptor pathDescriptor = createPathDescriptor( serviceDescriptor, byteMethod );
            if ( pathDescriptor != null )
            {
                getLog().debug( "Method has been detected as service operation." );
                serviceDescriptor.getPaths().add( pathDescriptor );
            }
        }
        Collections.sort( serviceDescriptor.getPaths() );
        return serviceDescriptor;
    }

    private String appendPath( String pathPrefix, String path )
    {

        if ( ( pathPrefix == null ) || ( pathPrefix.isEmpty() ) )
        {
            return path;
        }
        if ( pathPrefix.endsWith( "/" ) )
        {
            return pathPrefix + path;
        }
        else
        {
            return pathPrefix + "/" + path;
        }
    }

    private PathDescriptor createPathDescriptor( ServiceDescriptor serviceDescriptor, Method method )
    {
        Method byteMethod = method;
        Path methodPath = null;
        do
        {
            methodPath = byteMethod.getAnnotation( Path.class );
            if ( methodPath == null )
            {
                byteMethod = this.reflectionUtil.getParentMethod( byteMethod );
                if ( byteMethod == null )
                {
                    return null;
                }
            }
        }
        while ( methodPath == null );

        PathDescriptor pathDescriptor = new PathDescriptor();
        pathDescriptor.setPath( methodPath.value() );

        if ( this.annotationUtil.getMethodAnnotation( method, Deprecated.class ) != null )
        {
            pathDescriptor.setDeprecated( true );
        }
        pathDescriptor.setJavaByteMethod( method );
        JavaMethod sourceMethod = findSourceMethod( serviceDescriptor.getJavaSourceType(), byteMethod );
        pathDescriptor.setJavaSourceMethod( sourceMethod );
        List<JavaParameter> sourceParameters = null;
        if ( sourceMethod != null )
        {
            pathDescriptor.setDescription( this.javaDocHelper.parseJavaDoc( serviceDescriptor.getJavaSourceType(),
                                                                            serviceDescriptor.getJavaByteType(),
                                                                            sourceMethod.getComment() ) );
            sourceParameters = sourceMethod.getParameters();
        }
        else
        {
            getLog().warn( "Could not find source of method " + byteMethod.toGenericString() );
        }

        // OperationDescriptor operationDescriptor =
        // createOperationDescriptor( byteType, sourceClass, serviceDescriptor, method, byteMethod );

        Set<String> consumes = pathDescriptor.getConsumes();
        addConsumes( consumes, byteMethod.getAnnotation( Consumes.class ) );
        if ( consumes.isEmpty() )
        {
            consumes.addAll( serviceDescriptor.getConsumes() );
        }
        Set<String> produces = pathDescriptor.getProduces();
        addProduces( produces, byteMethod.getAnnotation( Produces.class ) );
        if ( produces.isEmpty() )
        {
            produces.addAll( serviceDescriptor.getProduces() );
        }

        pathDescriptor.setHttpMethod( createHttpMethodDescriptor( serviceDescriptor.getJavaSourceType(), byteMethod ) );

        createParameters( serviceDescriptor, method, byteMethod, pathDescriptor, sourceParameters );

        createResponses( serviceDescriptor, method, pathDescriptor, sourceMethod );

        return pathDescriptor;
    }

    private void createParameters( ServiceDescriptor serviceDescriptor, Method method, Method byteMethod,
                                   PathDescriptor pathDescriptor, List<JavaParameter> sourceParameters )
    {
        Type[] byteParameterTypes = method.getGenericParameterTypes();
        Annotation[][] byteParameterAnnotations = byteMethod.getParameterAnnotations();
        for ( int i = 0; i < byteParameterTypes.length; i++ )
        {
            JavaParameter sourceParameter = null;
            if ( sourceParameters != null )
            {
                sourceParameter = sourceParameters.get( i );
            }
            ParameterDescriptor parameterDescriptor =
                createParameterDescriptor( serviceDescriptor, pathDescriptor, byteMethod, sourceParameter,
                                           byteParameterAnnotations[i], byteParameterTypes[i] );
            pathDescriptor.getParameters().add( parameterDescriptor );
        }
    }

    private void createResponses( ServiceDescriptor serviceDescriptor, Method method, PathDescriptor pathDescriptor,
                                  JavaMethod sourceMethod )
    {
        String description = "";
        JavaClass javaSourceType = null;
        if ( sourceMethod != null )
        {
            javaSourceType = sourceMethod.getReturns();
            DocletTag returnTag = sourceMethod.getTagByName( "return" );
            if ( returnTag != null )
            {
                description = returnTag.getValue();
            }
        }
        ResponseDescriptor responseSuccess =
            createResponseDescriptor( serviceDescriptor, method.getGenericReturnType(), javaSourceType, description );
        pathDescriptor.getResponses().add( responseSuccess );
    }

    private ResponseDescriptor createResponseDescriptor( ServiceDescriptor serviceDescriptor, Type type,
                                                         JavaClass javaSourceType, String description )
    {
        ResponseDescriptor responseSuccess = new ResponseDescriptor();
        GenericType<?> byteReturnType =
            this.reflectionUtil.createGenericType( type, serviceDescriptor.getJavaByteType() );
        if ( byteReturnType.getRetrievalClass() == void.class )
        {
            responseSuccess.setStatusCode( Descriptor.STATUS_CODE_NO_CONTENT );
        }
        else
        {
            responseSuccess.setStatusCode( Descriptor.STATUS_CODE_SUCCESS );
        }
        responseSuccess.setReason( "Success" );
        responseSuccess.setJavaByteType( byteReturnType );
        responseSuccess.setJavaSourceType( javaSourceType );
        responseSuccess.setDescription( this.javaDocHelper.parseJavaDoc( serviceDescriptor.getJavaSourceType(),
                                                                         serviceDescriptor.getJavaByteType(),
                                                                         description ) );
        JavaScriptType javaScriptType = getJavaScriptType( byteReturnType.getAssignmentClass() );
        responseSuccess.setJavaScriptType( javaScriptType.getName() );
        responseSuccess.setExample( javaScriptType.getExample() );
        return responseSuccess;
    }

    private ParameterDescriptor createParameterDescriptor( ServiceDescriptor serviceDescriptor,
                                                           PathDescriptor pathDescriptor, Method byteMethod,
                                                           JavaParameter sourceParameter, Annotation[] byteAnnotations,
                                                           Type byteParameterType )
    {
        ParameterDescriptor parameterDescriptor = new ParameterDescriptor();
        GenericType<?> genericParameterType =
            this.reflectionUtil.createGenericType( byteParameterType, serviceDescriptor.getJavaByteType() );
        parameterDescriptor.setJavaByteType( genericParameterType );
        if ( sourceParameter == null )
        {
            parameterDescriptor.setName( "undefined" );
        }
        else
        {
            parameterDescriptor.setName( sourceParameter.getName() );
            String comment = sourceParameter.getComment();
            if ( isEmpty( comment ) )
            {
                // qdox actually seems to be buggy or awkward here...
                for ( DocletTag tag : pathDescriptor.getJavaSourceMethod().getTagsByName( "param" ) )
                {
                    String prefix = sourceParameter.getName() + " ";
                    if ( tag.getValue().startsWith( prefix ) )
                    {
                        comment = tag.getValue().substring( prefix.length() );
                    }
                }
            }
            parameterDescriptor.setDescription( this.javaDocHelper.parseJavaDoc( serviceDescriptor.getJavaSourceType(),
                                                                                 serviceDescriptor.getJavaByteType(),
                                                                                 comment ) );
        }
        String location = Descriptor.LOCATION_BODY;
        boolean required = false;
        for ( Annotation annotation : byteAnnotations )
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
                if ( UriInfo.class.isAssignableFrom( genericParameterType.getAssignmentClass() ) )
                {
                    location = "query/path";
                }
                else
                {
                    return null;
                }
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
        JavaScriptType javaScriptType = getJavaScriptType( genericParameterType.getAssignmentClass() );
        parameterDescriptor.setJavaScriptType( javaScriptType.getName() );
        parameterDescriptor.setExample( javaScriptType.getExample() );
        return parameterDescriptor;
    }

    private String getJavaTypeString( GenericType<?> byteParameterType )
    {
        // final StringBuilder buffer = new StringBuilder();
        // Visitor<Class<?>> classFormatter = new Visitor<Class<?>>()
        // {
        // public void visit( Class<?> value )
        // {
        // buffer.append( value.getSimpleName() );
        // }
        // };
        // this.reflectionUtil.toString( byteParameterType, buffer, classFormatter );
        // String javaTypeString = buffer.toString();
        // return javaTypeString;
        return this.reflectionUtil.toString( byteParameterType );
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
        else if ( CharSequence.class.isAssignableFrom( byteClass ) )
        {
            return JavaScriptType.STRING;
        }
        return JavaScriptType.OBJECT;
    }

    private String createHttpMethodDescriptor( JavaClass sourceClass, Method byteMethod )
    {
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
            getLog().warn( "Service method " + sourceClass.getName() + "." + byteMethod.getName()
                               + " is missing JAX-RS annotation for HTTP method!" );
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

    private JavaMethod findSourceMethod( JavaClass sourceClass, Method byteMethod )
    {
        for ( JavaMethod sourceMethod : sourceClass.getMethods() )
        {
            if ( sourceMethod.getName().equals( byteMethod.getName() ) )
            {
                List<JavaParameter> sourceParameters = sourceMethod.getParameters();
                Class<?>[] byteParameters = byteMethod.getParameterTypes();
                if ( sourceParameters.size() == byteParameters.length )
                {
                    for ( int i = 0; i < byteParameters.length; i++ )
                    {
                        String byteTypeName = byteParameters[i].getName();
                        String sourceTypeName = sourceParameters.get( i ).getType().getFullyQualifiedName();
                        if ( !byteTypeName.equals( sourceTypeName ) )
                        {
                            return null;
                        }
                    }
                    return sourceMethod;
                }
            }
        }
        return null;
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
