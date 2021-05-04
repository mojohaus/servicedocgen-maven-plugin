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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;

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

import org.apache.maven.model.Developer;
import org.apache.maven.model.Organization;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.mojo.servicedocgen.descriptor.ContactDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.Descriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ErrorDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ErrorDescriptor.Match;
import org.codehaus.mojo.servicedocgen.descriptor.InfoDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.LicenseDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.OperationDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ParameterDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ResponseDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ServiceDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ServicesDescriptor;
import org.codehaus.mojo.servicedocgen.introspection.JElement;
import org.codehaus.mojo.servicedocgen.introspection.JException;
import org.codehaus.mojo.servicedocgen.introspection.JMethod;
import org.codehaus.mojo.servicedocgen.introspection.JParameter;
import org.codehaus.mojo.servicedocgen.introspection.JReturn;
import org.codehaus.mojo.servicedocgen.introspection.JType;
import org.codehaus.mojo.servicedocgen.introspection.JavaDocHelper;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

import net.sf.mmm.util.exception.api.IllegalCaseException;
import net.sf.mmm.util.lang.api.Datatype;
import net.sf.mmm.util.lang.api.SimpleDatatype;
import net.sf.mmm.util.math.api.NumberType;
import net.sf.mmm.util.math.base.MathUtilImpl;
import net.sf.mmm.util.pojo.descriptor.api.PojoDescriptor;
import net.sf.mmm.util.pojo.descriptor.api.PojoDescriptorBuilder;
import net.sf.mmm.util.pojo.descriptor.api.PojoDescriptorBuilderFactory;
import net.sf.mmm.util.pojo.descriptor.api.PojoPropertyDescriptor;
import net.sf.mmm.util.pojo.descriptor.api.accessor.PojoPropertyAccessorNonArg;
import net.sf.mmm.util.pojo.descriptor.api.accessor.PojoPropertyAccessorNonArgMode;
import net.sf.mmm.util.pojo.descriptor.impl.PojoDescriptorBuilderFactoryImpl;
import net.sf.mmm.util.reflect.api.AnnotationUtil;
import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.reflect.api.ReflectionUtil;
import net.sf.mmm.util.reflect.base.AnnotationUtilImpl;
import net.sf.mmm.util.reflect.base.ReflectionUtilImpl;
import net.sf.mmm.util.validation.base.Mandatory;

/**
 * {@link Analyzer} contains the logic to analyze the services on byte-code and source-code level and create the
 * according {@link Descriptor}s.
 *
 * @see #createServicesDescriptor(List)
 * @author hohwille
 */
public class Analyzer
{
    /** {@link ResponseDescriptor#getReason() Reason} for a valid success result. */
    public static final String RESPONSE_REASON_SUCCESS = "Success";

    /** {@link ResponseDescriptor#getReason() Reason} for an error (exception). */
    public static final String RESPONSE_REASON_ERROR = "Error";

    /** {@link ResponseDescriptor#getDescription() Description} for void results. */
    public static final String DESCRIPTION_VOID = "No content";

    private final ClassLoader projectClassloader;

    private final ReflectionUtil reflectionUtil;

    private final AnnotationUtil annotationUtil;

    private final PojoDescriptorBuilder pojoDescriptorBuilder;

    private final PojoDescriptorBuilderFactory pojoDescriptorBuilderFactory;

    private final JavaDocHelper javaDocHelper;

    private final JavaProjectBuilder builder;

    private final Log log;

    private final MavenProject project;

    private ServicesDescriptor descriptor;
    
    private Map<String, JElement> schemas = new HashMap<String, JElement>();

    /**
     * The constructor.
     *
     * @param log the {@link Log}.
     * @param project the {@link MavenProject}. May be <code>null</code> for testing.
     * @param projectClassloader the {@link ClassLoader} to load byte-code.
     * @param builder the {@link JavaProjectBuilder}.
     * @param descriptor the pre-configured {@link ServicesDescriptor} template.
     * @param introspectFields <code>true</code> to introspect beans using fields, <code>false</code> for getters.
     */
    public Analyzer( Log log, MavenProject project, ClassLoader projectClassloader, JavaProjectBuilder builder,
                     ServicesDescriptor descriptor, boolean introspectFields )
    {
        super();
        this.log = log;
        this.project = project;
        this.projectClassloader = projectClassloader;
        this.reflectionUtil = ReflectionUtilImpl.getInstance();
        this.annotationUtil = AnnotationUtilImpl.getInstance();
        this.builder = builder;
        this.descriptor = descriptor;
        this.pojoDescriptorBuilderFactory = PojoDescriptorBuilderFactoryImpl.getInstance();
        if ( introspectFields )
        {
            this.pojoDescriptorBuilder = this.pojoDescriptorBuilderFactory.createPrivateFieldDescriptorBuilder();
        }
        else
        {
            this.pojoDescriptorBuilder = this.pojoDescriptorBuilderFactory.createPublicMethodDescriptorBuilder();
        }
        this.javaDocHelper = new JavaDocHelper( this.projectClassloader, this.builder, this.descriptor.getJavadocs() );
        if ( descriptor != null )
        {
            boolean hasFallback = false;
            for ( ErrorDescriptor errorDescriptor : descriptor.getErrors() )
            {
                if ( errorDescriptor.getMatch() == Match.assignable )
                {
                    String errorName = errorDescriptor.getErrorName();
                    Class<?> errorClass;
                    try
                    {
                        errorClass = projectClassloader.loadClass( errorName );
                    }
                    catch ( Exception e )
                    {
                        log.warn( "Failed to load error class '" + errorName + "':" + e.getMessage(), e );
                        errorClass = UnknownError.class;
                    }
                    errorDescriptor.setErrorClass( errorClass );
                }
                else if ( errorDescriptor.getMatch() == Match.regex )
                {
                    String errorName = errorDescriptor.getErrorName();
                    if ( errorName.isEmpty() || errorName.equals( "." ) || errorName.equals( ".*" ) )
                    {
                        hasFallback = true;
                    }
                }
            }
            if ( !hasFallback )
            {
                ErrorDescriptor errorDescriptor = new ErrorDescriptor();
                errorDescriptor.setErrorName( "" );
                descriptor.getErrors().add( errorDescriptor );
            }
        }
    }

    protected Log getLog()
    {

        return this.log;
    }

    /**
     * Does the actualy analysis.
     *
     * @param serviceClasses the {@link List} of service classes.
     * @return the {@link ServicesDescriptor}.
     * @throws Exception if something goes wrong.
     */
    public ServicesDescriptor createServicesDescriptor( List<JavaClass> serviceClasses )
        throws Exception
    {
        if ( this.descriptor == null )
        {
            this.descriptor = new ServicesDescriptor();
        }
        InfoDescriptor info = this.descriptor.getInfo();
        if ( info == null )
        {
            info = new InfoDescriptor();
            this.descriptor.setInfo( info );
        }
        createInfoDescriptor( info );
        Set<String> descriptorSchemes = this.descriptor.getSchemes();
        if ( descriptorSchemes.isEmpty() )
        {
            descriptorSchemes.add( Descriptor.SCHEME_HTTPS );
        }
        for ( JavaClass type : serviceClasses )
        {
            ServiceDescriptor service = createServiceDescriptor( type );
            this.descriptor.getServices().add( service );
        }
        
        String schemaJson = getSchemaAsString(ReportType.OPENAPI_JSON);
        String schemaYaml = getSchemaAsString(ReportType.OPENAPI_YAML);
        this.descriptor.setSchemaDefinitionJson(schemaJson);
        this.descriptor.setSchemaDefinitionYaml(schemaYaml);
        return this.descriptor;
    }

    protected ServiceDescriptor createServiceDescriptor( JavaClass sourceType )
        throws Exception
    {
        getLog().info( "Analyzing " + sourceType.getName() );
        ServiceDescriptor serviceDescriptor = new ServiceDescriptor();
        serviceDescriptor.setName( sourceType.getName() );
        Class<?> byteClass;
        try
        {
            byteClass = this.projectClassloader.loadClass( sourceType.getFullyQualifiedName() );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MavenReportException( "Failed to load class " + sourceType.getName()
                + " - did you forget to invoke compile (e.g. mvn verify site)?", e );
        }
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

    protected InfoDescriptor createInfoDescriptor( InfoDescriptor info )
    {
        if ( this.project == null )
        {
            return info;
        }
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

    protected OperationDescriptor createOperationDescriptor( ServiceDescriptor serviceDescriptor, Method byteMethod )
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
            
            if( !this.schemas.containsKey( parameter.getByteTypeString() ) );
            {
                this.schemas.put(parameter.getByteTypeString(), parameter);
            }
        }

        // responses
        ResponseDescriptor responseSuccess =
            createResponseDescriptor( serviceDescriptor, operationDescriptor, method.getReturns(), false );
        operationDescriptor.getResponses().add( responseSuccess );

        for ( JException exception : method.getExceptions() )
        {
            ResponseDescriptor response =
                createResponseDescriptor( serviceDescriptor, operationDescriptor, exception, true );
            operationDescriptor.getResponses().add( response );
        }

        for ( ErrorDescriptor errorDescriptor : this.descriptor.getErrors() )
        {
            if ( errorDescriptor.getMatch() == Match.always )
            {
                JElement exception = new JException( this.reflectionUtil.createGenericType( Throwable.class ), null,
                                                     errorDescriptor.getComment() );
                ResponseDescriptor response =
                    createResponseDescriptor( serviceDescriptor, operationDescriptor, exception, true );
                operationDescriptor.getResponses().add( response );
            }
        }

        return operationDescriptor;
    }

    protected ParameterDescriptor createParameterDescriptor( ServiceDescriptor serviceDescriptor,
                                                             OperationDescriptor operationDescriptor,
                                                             JParameter parameter )
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
        JavaScriptType javaScriptType = getJavaScriptType( parameter.getByteType(), false );
        parameterDescriptor.setJavaScriptType( javaScriptType.getName() );
        parameterDescriptor.setExample( createExample( operationDescriptor, javaScriptType, parameter ) );
        return parameterDescriptor;
    }

    protected ResponseDescriptor createResponseDescriptor( ServiceDescriptor serviceDescriptor,
                                                           OperationDescriptor operationDescriptor,
                                                           JElement javaElement, boolean error )
    {
        String reason = error ? RESPONSE_REASON_ERROR : RESPONSE_REASON_SUCCESS;
        String example = null;
        ResponseDescriptor response = new ResponseDescriptor();
        GenericType<?> byteReturnType = javaElement.getByteType();
        String description = javaElement.getComment();
        if ( error )
        {
            String statusCode = Descriptor.STATUS_CODE_INTERNAL_SERVER_ERROR;
            for ( ErrorDescriptor errorDescriptor : this.descriptor.getErrors() )
            {
                boolean matches = isMatchingError( byteReturnType, errorDescriptor );
                if ( matches )
                {
                    statusCode = errorDescriptor.getStatusCode();
                    boolean isXml = Util.containsSubstring( operationDescriptor.getProduces(), "xml" );
                    if ( isXml )
                    {
                        example = errorDescriptor.getXmlExample();
                    }
                    else
                    {
                        example = errorDescriptor.getJsonExample();
                    }
                    break;
                }
            }
            response.setStatusCode( statusCode );
        }
        else
        {
            if ( byteReturnType.getRetrievalClass() == void.class )
            {
                response.setStatusCode( Descriptor.STATUS_CODE_NO_CONTENT );
                if ( Util.isEmpty( description ) )
                {
                    description = DESCRIPTION_VOID;
                }
            }
            else
            {
                response.setStatusCode( Descriptor.STATUS_CODE_SUCCESS );
            }
            
            if( !this.schemas.containsKey( javaElement.getByteTypeString() ) );
            {
                this.schemas.put( javaElement.getByteTypeString(), javaElement );
            }
        }
        response.setDescription( description );
        response.setReason( reason );
        response.setJavaElement( javaElement );
        JavaScriptType javaScriptType = getJavaScriptType( byteReturnType, true );
        response.setJavaScriptType( javaScriptType.getName() );
        if ( example == null )
        {
            example = createExample( operationDescriptor, javaScriptType, javaElement );
        }
        response.setExample( example );
        
        return response;
    }

    private boolean isMatchingError( GenericType<?> byteReturnType, ErrorDescriptor errorDescriptor )
    {
        Class<?> byteClass = byteReturnType.getRetrievalClass();
        Match match = errorDescriptor.getMatch();
        switch ( match )
        {
            case regex:
                Matcher matcher = errorDescriptor.getErrorNamePattern().matcher( byteClass.getName() );
                return matcher.matches();
            case assignable:
                return errorDescriptor.getErrorClass().isAssignableFrom( byteClass );
            case always:
                // yes, this is not a bug.
                // always means it will always produce an error result but independent of a specific exception
                return ( byteClass == Throwable.class );
            default:
                throw new IllegalCaseException( Match.class, match );
        }
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

    protected JavaScriptType getJavaScriptType( GenericType<?> byteType, boolean retrieval )
    {
        return getJavaScriptType( byteType, retrieval, 0 );
    }

    private JavaScriptType getJavaScriptType( GenericType<?> byteType, boolean retrieval, int recursion )
    {
        Class<?> clazz;
        if ( retrieval )
        {
            clazz = byteType.getRetrievalClass();
        }
        else
        {
            clazz = byteType.getAssignmentClass();
        }
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
        else if ( SimpleDatatype.class.isAssignableFrom( byteClass ) )
        {
            if ( recursion <= 2 )
            {
                Type typeVariable = SimpleDatatype.class.getTypeParameters()[0];
                GenericType<?> datatype = this.reflectionUtil.createGenericType( typeVariable, byteType );
                return getJavaScriptType( datatype, retrieval, recursion + 1 );
            }
        }
        else if ( byteClass.isEnum() )
        {
            return JavaScriptType.STRING;
        }
        else if ( isDate( byteClass ) )
        {
            return JavaScriptType.DATE;
        }
        else if ( Type.class.isAssignableFrom( byteClass ) )
        {
            return JavaScriptType.TYPE;
        }
        return JavaScriptType.OBJECT;
    }

    private boolean isDate( Class<?> byteClass )
    {
        if ( Date.class.isAssignableFrom( byteClass ) )
        {
            return true;
        }
        else if ( Calendar.class.isAssignableFrom( byteClass ) )
        {
            return true;
        }
        else
        {
            String className = byteClass.getName();
            if ( "java.time.LocalDate".equals( className ) )
            {
                return true;
            }
            else if ( "java.time.LocalDateTime".equals( className ) )
            {
                return true;
            }
            else if ( "java.time.Instant".equals( className ) )
            {
                return true;
            }
            else if ( "java.time.OffsetDateTime".equals( className ) )
            {
                return true;
            }
            else if ( "java.time.ZonedDateTime".equals( className ) )
            {
                return true;
            }
        }
        return false;
    }

    protected String createExample( OperationDescriptor operationDescriptor, JavaScriptType javaScriptType,
                                    JElement element )
    {
        if ( javaScriptType == JavaScriptType.VOID )
        {
            return null;
        }
        boolean retrieval = ( element instanceof JReturn );
        GenericType<?> byteType = element.getByteType();
        Class<?> byteClass;
        if ( retrieval )
        {
            byteClass = byteType.getRetrievalClass();
        }
        else
        {
            byteClass = byteType.getAssignmentClass();
        }
        StringBuilder buffer = new StringBuilder();
        createExample( javaScriptType, byteType, byteClass, "", buffer, new HashSet<Class<?>>(), retrieval );
        return buffer.toString();
    }

    private void createExample( GenericType<?> byteType, String indent, StringBuilder buffer,
                                Set<Class<?>> visitedClassSet, boolean retrieval )
    {
        Class<?> byteClass;
        if ( retrieval )
        {
            byteClass = byteType.getRetrievalClass();
        }
        else
        {
            byteClass = byteType.getAssignmentClass();
        }
        JavaScriptType javaScriptType = getJavaScriptType( byteType, retrieval );
        createExample( javaScriptType, byteType, byteClass, indent, buffer, visitedClassSet, retrieval );
    }

    private void createExample( JavaScriptType javaScriptType, GenericType<?> byteType, Class<?> byteClass,
                                String indent, StringBuilder buffer, Set<Class<?>> visitedClassSet, boolean retrieval )
    {
        if ( javaScriptType == JavaScriptType.ARRAY )
        {
            GenericType<?> componentType = byteType.getComponentType();
            if ( componentType != null )
            {
                buffer.append( '[' );
                createExample( componentType, indent, buffer, visitedClassSet, retrieval );
                buffer.append( ']' );
                return;
            }
        }
        else if ( javaScriptType == JavaScriptType.OBJECT )
        {
            boolean added = true;
            if ( !Datatype.class.isAssignableFrom( byteClass ) )
            {
                added = visitedClassSet.add( byteClass );
            }
            if ( byteClass == Object.class )
            {
                added = false;
            }
            if ( added )
            {
                buffer.append( '{' );
                String childIndent = indent + "  ";
                buffer.append( '\n' );
                buffer.append( childIndent );
                if ( Map.class.isAssignableFrom( byteClass ) )
                {
                    createExampleForMap( byteType, buffer, visitedClassSet, retrieval, childIndent );
                }
                else
                {
                    createExampleForBean( byteType, byteClass, buffer, visitedClassSet, retrieval, childIndent );
                }
                buffer.append( '\n' );
                buffer.append( indent );
                buffer.append( '}' );
                return;
            }
        }
        buffer.append( javaScriptType.getExample() );
    }

    private void createExampleForBean( GenericType<?> byteType, Class<?> byteClass, StringBuilder buffer,
                                       Set<Class<?>> visitedClassSet, boolean retrieval, String childIndent )
    {
        PojoDescriptor<?> pojoDescriptor = this.pojoDescriptorBuilder.getDescriptor( byteType );
        boolean repeating = false;
        List<? extends PojoPropertyDescriptor> propertyDescriptors =
            new ArrayList<PojoPropertyDescriptor>( pojoDescriptor.getPropertyDescriptors() );
        Comparator<PojoPropertyDescriptor> comparator = new Comparator<PojoPropertyDescriptor>()
        {
            public int compare( PojoPropertyDescriptor o1, PojoPropertyDescriptor o2 )
            {
                return o1.getName().compareTo( o2.getName() );
            }
        };
        Collections.sort( propertyDescriptors, comparator );
        for ( PojoPropertyDescriptor propertyDescriptor : propertyDescriptors )
        {
            if ( !propertyDescriptor.getName().equals( "class" ) )
            {
                PojoPropertyAccessorNonArg getter =
                    propertyDescriptor.getAccessor( PojoPropertyAccessorNonArgMode.GET );
                if ( getter != null )
                {
                    if ( repeating )
                    {
                        buffer.append( ",\n" );
                        buffer.append( childIndent );
                    }
                    repeating = true;
                    String propertyName = propertyDescriptor.getName();
                    buffer.append( '"' );
                    buffer.append( propertyName );
                    buffer.append( "\" = " );
                    createExample( getter.getPropertyType(), childIndent, buffer, visitedClassSet, retrieval );
                }
            }
        }
    }

    private void createExampleForMap( GenericType<?> byteType, StringBuilder buffer, Set<Class<?>> visitedClassSet,
                                      boolean retrieval, String childIndent )
    {
        buffer.append( "\"&lt;key&gt;\" = " );
        GenericType<?> componentType = byteType.getComponentType();
        if ( componentType == null )
        {
            buffer.append( "..." );
        }
        else
        {
            createExample( componentType, childIndent, buffer, visitedClassSet, retrieval );
            buffer.append( '\n' );
            buffer.append( childIndent );
            buffer.append( ", ..." );
        }
    }
    
    private String getSchemaAsString( ReportType reportType )
    {
        PojoDescriptorBuilderFactory pojoDescriptorBuilderFactory = PojoDescriptorBuilderFactoryImpl.getInstance();
        PojoDescriptorBuilder pojoDescriptorBuilder = pojoDescriptorBuilderFactory.createPrivateFieldDescriptorBuilder();
        StringBuffer buffer = new StringBuffer();
        List<String> schemasCreated = new ArrayList<String>();
        
        Iterator<Entry<String, JElement>> it = this.schemas.entrySet().iterator();
        while ( it.hasNext() )
        {
            Entry<String, JElement> entry = it.next();
            JElement javaElement = entry.getValue();
            if( !javaElement.getSourceType().isPrimitive() && !javaElement.getSourceType().getName().equals( "String" ) ) 
            {
                if( entry.getValue().getByteType().getComponentType() == null )
                {
                    if( reportType == ReportType.OPENAPI_JSON )
                    {
                        createSchemaAsJsonForType( pojoDescriptorBuilder, entry.getValue().getByteType(), buffer, "      ", schemasCreated );
                    } else if ( reportType == ReportType.OPENAPI_YAML ) 
                    {
                        createSchemaAsYamlForType( pojoDescriptorBuilder, entry.getValue().getByteType(), buffer, "    ", schemasCreated );
                    }
                } else
                {
                    if( reportType == ReportType.OPENAPI_JSON )
                    {
                        createSchemaAsJsonForType( pojoDescriptorBuilder, entry.getValue().getByteType().getComponentType(), buffer, "      ", schemasCreated );
                    } else if ( reportType == ReportType.OPENAPI_YAML ) 
                    {
                        createSchemaAsYamlForType( pojoDescriptorBuilder, entry.getValue().getByteType().getComponentType(), buffer, "    ", schemasCreated );
                    }
                }
            }
        }
        return buffer.toString();
    }
    
    private void createSchemaAsJsonForType( PojoDescriptorBuilder pojoDescriptorBuilder, GenericType<?> byteType, StringBuffer buffer, String indentation, List<String> schemasCreated )
    {
        PojoDescriptor<?> pojoDescriptor = pojoDescriptorBuilder.getDescriptor( byteType );
        List<? extends PojoPropertyDescriptor> propertyDescriptors = new ArrayList<PojoPropertyDescriptor>( pojoDescriptor.getPropertyDescriptors() );
        Map<String, GenericType<?>> schemasToCreate = new HashMap<String, GenericType<?>>();
        
        if( !schemasCreated.contains( byteType.getAssignmentClass().getSimpleName() ) )
        {
            String schemaName = byteType.getAssignmentClass().getSimpleName();
            buffer.append( indentation.concat( "'" + schemaName + "': {\n") );
        
            buffer.append( indentation.concat( "  'type': 'object',\n") );
            buffer.append( indentation.concat( "  'properties': {\n") );
            for ( PojoPropertyDescriptor propertyDescriptor : propertyDescriptors )
            { 
                PojoPropertyAccessorNonArg getter = propertyDescriptor.getAccessor( PojoPropertyAccessorNonArgMode.GET );
                if ( getter != null )
                {
                    String type = getter.getReturnType().getTypeName();
                    if( !type.equals( getter.getReturnType().getAssignmentClass().getName() ) )
                    {
                        type = getter.getReturnType().getAssignmentClass().getName();
                    }
                    
                    buffer.append( indentation.concat( "    '" + propertyDescriptor.getName() + "': {\n" ) );
                    if( type.equals( "int" ) || type.equals( "long" ) || type.equals( "byte" ) )
                    {
                        buffer.append( indentation.concat( "      'type': 'integer'\n" ) );
                    } else if( type.equals( "double" ) || type.equals( "float") ) {
                        buffer.append( indentation.concat( "      'type': 'number'\n" ) );
                    } else if( type.equals( "boolean" ) )
                    {
                        buffer.append( indentation.concat( "      'type': 'boolean'\n" ) );
                    } else if( type.equals( "java.lang.String" ) )
                    {
                        buffer.append( indentation.concat( "      'type': 'string'\n" ) );
                    } else
                    {
                        boolean addSchema = true;
                        GenericType<?> byteTypeToCreate;
                        if( getter.getReturnType().getComponentType() == null )
                        {
                            byteTypeToCreate = getter.getReturnType();
                            buffer.append( indentation.concat( "      $ref: '#/components/schemas/" + byteTypeToCreate.getAssignmentClass().getSimpleName() + "'\n") );
                        } else
                        {
                            byteTypeToCreate = getter.getReturnType().getComponentType();
                            String typeName = byteTypeToCreate.getTypeName();
                            
                            buffer.append( indentation.concat( "      'type': 'array',\n") );
                            buffer.append( indentation.concat( "      'items': {\n") );
                            if( typeName.equals( "int" ) || typeName.equals( "long" ) || typeName.equals( "byte" ) )
                            {
                                buffer.append( indentation.concat( "        'type': 'integer'\n") );
                                addSchema = false;
                            } else if( typeName.equals( "double" ) || typeName.equals( "float") )
                            {
                                buffer.append( indentation.concat( "        'type': 'number'\n") );
                                addSchema = false;
                            } else if( typeName.equals( "boolean" ) ) {
                                buffer.append( indentation.concat( "        'type': 'boolean'\n") );
                                addSchema = false;
                            } else if( typeName.equals( "java.lang.String" ) )
                            {
                                buffer.append( indentation.concat( "        'type': 'string'\n") );
                                addSchema = false;
                            } else
                            {
                                buffer.append( indentation.concat( "        $ref: '#/components/schemas/" + byteTypeToCreate.getAssignmentClass().getSimpleName() + "'\n") );
                            }
                            buffer.append( indentation.concat( "      }\n" ) );
                        }
                        
                        if( !schemasToCreate.containsKey( byteTypeToCreate.getAssignmentClass().getSimpleName() ) && addSchema )
                        {
                            schemasToCreate.put( byteTypeToCreate.getAssignmentClass().getSimpleName(), byteTypeToCreate );
                        }
                    }

                    buffer.append( indentation.concat( "    },\n" ) );
                }
            }

            buffer.append( indentation.concat( "  },\n" ) );
            buffer.append( indentation.concat( "},\n" ) );
            
            schemasCreated.add( schemaName );
                
            for( Entry<String, GenericType<?>> entry : schemasToCreate.entrySet() )
            {
                if( !schemasCreated.contains( entry.getKey() ) )
                {
                    createSchemaAsJsonForType( pojoDescriptorBuilder, entry.getValue(), buffer, indentation, schemasCreated );
                }
            }
        }
        
        return;
    }
    
    private void createSchemaAsYamlForType( PojoDescriptorBuilder pojoDescriptorBuilder, GenericType<?> byteType, StringBuffer buffer, String indentation, List<String> schemasCreated )
    {
        PojoDescriptor<?> pojoDescriptor = pojoDescriptorBuilder.getDescriptor( byteType );
        List<? extends PojoPropertyDescriptor> propertyDescriptors = new ArrayList<PojoPropertyDescriptor>( pojoDescriptor.getPropertyDescriptors() );
        Map<String, GenericType<?>> schemasToCreate = new HashMap<String, GenericType<?>>();
        
        if( !schemasCreated.contains( byteType.getAssignmentClass().getSimpleName() ) )
        {
            String schemaName = byteType.getAssignmentClass().getSimpleName();
            buffer.append( indentation.concat( schemaName + ":\n") );
            
            buffer.append( indentation.concat( "  type: object\n" ) );
            buffer.append( indentation.concat( "  properties:\n" ) );
            for ( PojoPropertyDescriptor propertyDescriptor : propertyDescriptors )
            { 
                PojoPropertyAccessorNonArg getter = propertyDescriptor.getAccessor( PojoPropertyAccessorNonArgMode.GET );
                if ( getter != null )
                {
                    String type = getter.getReturnType().getTypeName();
                    if( !type.equals( getter.getReturnType().getAssignmentClass().getName() ) )
                    {
                        type = getter.getReturnType().getAssignmentClass().getName();
                    }
                    
                    buffer.append( indentation.concat( "    " + propertyDescriptor.getName() + ":\n" ) );
                    if( type.equals( "int" ) || type.equals( "long" ) || type.equals( "byte" ) )
                    {
                        buffer.append( indentation.concat( "      type: integer\n" ) );
                    } else if ( type.equals( "double" ) || type.equals( "float" ) )
                    {
                        buffer.append( indentation.concat( "      type': number\n" ) );
                    } else if ( type.equals( "boolean" ) )
                    {
                        buffer.append( indentation.concat( "      type: boolean\n" ) );
                    } else if ( type.equals( "java.lang.String" ) )
                    {
                        buffer.append( indentation.concat( "      type: string\n" ) );
                    } else
                    {
                        boolean addSchema = true;
                        GenericType<?> byteTypeToCreate;
                        if (getter.getReturnType().getComponentType() == null )
                        {
                            byteTypeToCreate = getter.getReturnType();
                            buffer.append( indentation.concat( "      $ref: '#/components/schemas/" + byteTypeToCreate.getAssignmentClass().getSimpleName() + "'\n" ) );
                        } else
                        {
                            byteTypeToCreate = getter.getReturnType().getComponentType();
                            String typeName = byteTypeToCreate.getTypeName();
                            
                            buffer.append( indentation.concat( "      type: array\n" ) );
                            buffer.append( indentation.concat( "      items:\n" ) );
                            if( typeName.equals( "int" ) || typeName.equals( "long" ) || typeName.equals( "byte" ) )
                            {
                                buffer.append( indentation.concat( "        type: integer\n" ) );
                                addSchema = false;
                            } else if( typeName.equals( "double" ) || typeName.equals( "float") )
                            {
                                buffer.append( indentation.concat( "        type: number\n" ) );
                                addSchema = false;
                            } else if( typeName.equals( "boolean" ) ) {
                                buffer.append( indentation.concat( "        type: boolean\n" ) );
                                addSchema = false;
                            } else if( typeName.equals( "java.lang.String" ) )
                            {
                                buffer.append( indentation.concat( "        type: string\n" ) );
                                addSchema = false;
                            } else
                            {
                                buffer.append( indentation.concat( "        $ref: '#/components/schemas/" + byteTypeToCreate.getAssignmentClass().getSimpleName() + "'\n" ) );
                            }
                        }
                        
                        if ( !schemasToCreate.containsKey(byteTypeToCreate.getAssignmentClass().getSimpleName() ) && addSchema )
                        {
                            schemasToCreate.put( byteTypeToCreate.getAssignmentClass().getSimpleName(), byteTypeToCreate );
                        }
                    }
                }
            }
            
            schemasCreated.add( schemaName );
        }
        
        for( Entry<String, GenericType<?>> entry : schemasToCreate.entrySet() )
        {
            if ( !schemasCreated.contains( entry.getKey() ) )
            {
                createSchemaAsYamlForType( pojoDescriptorBuilder, entry.getValue(), buffer, indentation, schemasCreated );
            }
        }
        
        return;
    }

}
