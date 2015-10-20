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
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.assertj.core.api.Assertions;
import org.codehaus.mojo.servicedocgen.descriptor.Descriptor;
import org.codehaus.mojo.servicedocgen.descriptor.OperationDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ParameterDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ResponseDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ServiceDescriptor;
import org.codehaus.mojo.servicedocgen.descriptor.ServicesDescriptor;
import org.codehaus.mojo.servicedocgen.example.DemoRestService;
import org.junit.Test;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

/**
 * @author hohwille
 */
public class AnalyzerTest
    extends Assertions
{

    protected Analyzer getAnalyzer( JavaProjectBuilder builder )
    {

        Analyzer analyzer =
            new Analyzer( new SystemStreamLog(), null, Thread.currentThread().getContextClassLoader(), builder,
                          new ServicesDescriptor(), false );
        return analyzer;
    }

    /**
     * Test of {@link Analyzer#createServicesDescriptor(List)} using {@link DemoRestService}.
     *
     * @throws Exception if something goes wrong.
     */
    @Test
    public void testAnalyzer()
        throws Exception
    {
        // given
        String className = DemoRestService.class.getName();
        // when
        ServicesDescriptor descriptor = analyze( className );
        // then
        assertThat( descriptor ).isNotNull();
        List<ServiceDescriptor> services = descriptor.getServices();
        assertThat( services ).hasSize( 1 );
        ServiceDescriptor serviceDescriptor = services.get( 0 );
        assertThat( serviceDescriptor.getBasePath() ).isEqualTo( "/demo/v1" );
        assertThat( serviceDescriptor.getConsumes() ).containsOnly( MediaType.APPLICATION_JSON );
        assertThat( serviceDescriptor.getProduces() ).containsOnly( MediaType.APPLICATION_JSON );
        assertThat( serviceDescriptor.getDescription() ).isEqualTo( "This is a cool REST-Service to demonstrate <code>servicedocgen-maven-plugin</code>." );
        List<OperationDescriptor> operations = serviceDescriptor.getOperations();
        assertThat( operations ).hasSize( 5 );
        // operations are ordered by HTTP method and path
        // check first operation
        OperationDescriptor operation0 = operations.get( 0 );
        assertThat( operation0 ).isNotNull();
        assertThat( operation0.getHttpMethod() ).isEqualTo( Descriptor.HTTP_METHOD_DELETE );
        assertThat( operation0.getPath() ).isEqualTo( "/string/{id}" );
        assertThat( operation0.getConsumes() ).containsOnly( MediaType.APPLICATION_JSON );
        assertThat( operation0.getProduces() ).containsOnly( MediaType.APPLICATION_JSON );
        assertThat( operation0.getDescription() ).isEqualTo( "Deletes the <code>DemoTo</code> with the given <code>id</code>." );
        // check parameters
        List<ParameterDescriptor> parameters = operation0.getParameters();
        assertThat( parameters ).hasSize( 1 );
        ParameterDescriptor parameter = parameters.get( 0 );
        assertThat( parameter ).isNotNull();
        assertThat( parameter.getName() ).isEqualTo( "id" );
        assertThat( parameter.getLocation() ).isEqualTo( Descriptor.LOCATION_PATH );
        assertThat( parameter.getJavaScriptType() ).isEqualTo( JavaScriptType.INTEGER.getName() );
        assertThat( parameter.getDescription() ).isEqualTo( "the primary key of the object to delete." );
        assertThat( parameter.getExample() ).isEqualTo( "1" );
        assertThat( parameter.getDefaultValue() ).isEmpty();
        // check responses
        List<ResponseDescriptor> responses = operation0.getResponses();
        assertThat( responses ).hasSize( 1 );
        ResponseDescriptor response = responses.get( 0 );
        assertThat( response ).isNotNull();
        assertThat( response.getStatusCode() ).isEqualTo( Descriptor.STATUS_CODE_NO_CONTENT );
        assertThat( response.getReason() ).isEqualTo( "Success" );
        assertThat( response.getJavaScriptType() ).isEqualTo( JavaScriptType.VOID.getName() );
        assertThat( response.getExample() ).isNull();
        // check third operation
        OperationDescriptor operation2 = operations.get( 2 );
        assertThat( operation2 ).isNotNull();
        assertThat( operation2.getHttpMethod() ).isEqualTo( Descriptor.HTTP_METHOD_GET );
        assertThat( operation2.getPath() ).isEqualTo( "/string/{id}" );
        assertThat( operation2.getConsumes() ).containsOnly( MediaType.APPLICATION_JSON );
        assertThat( operation2.getProduces() ).containsOnly( MediaType.APPLICATION_JSON );
        assertThat( operation2.getDescription() ).isEqualTo( "Finds the <code>DemoTo</code> with the given <code>id</code>." );
        // check parameters
        parameters = operation2.getParameters();
        assertThat( parameters ).hasSize( 1 );
        parameter = parameters.get( 0 );
        assertThat( parameter ).isNotNull();
        assertThat( parameter.getName() ).isEqualTo( "id" );
        assertThat( parameter.getLocation() ).isEqualTo( Descriptor.LOCATION_PATH );
        assertThat( parameter.getJavaScriptType() ).isEqualTo( JavaScriptType.INTEGER.getName() );
        assertThat( parameter.getDescription() ).isEqualTo( "the primary key of the requested object." );
        assertThat( parameter.getExample() ).isEqualTo( "1" );
        assertThat( parameter.getDefaultValue() ).isEmpty();
        // check responses
        responses = operation2.getResponses();
        assertThat( responses ).hasSize( 1 );
        response = responses.get( 0 );
        assertThat( response ).isNotNull();
        assertThat( response.getStatusCode() ).isEqualTo( Descriptor.STATUS_CODE_SUCCESS );
        assertThat( response.getReason() ).isEqualTo( "Success" );
        assertThat( response.getJavaScriptType() ).isEqualTo( JavaScriptType.OBJECT.getName() );
        assertThat( response.getExample() ).isEqualTo( "{\n" + //
            "  \"id\" = 1,\n" + //
            "  \"modificationCounter\" = 1,\n" + //
            "  \"parent\" = {...},\n" + //
            "  \"revision\" = 1.0,\n" + //
            "  \"type\" = \"text\"\n" + //
            "}" );

        // check fourth operation
        OperationDescriptor operation3 = operations.get( 3 );
        assertThat( operation3 ).isNotNull();
        assertThat( operation3.getHttpMethod() ).isEqualTo( Descriptor.HTTP_METHOD_POST );
        assertThat( operation3.getPath() ).isEqualTo( "/longs" );
        assertThat( operation3.getConsumes() ).containsOnly( MediaType.APPLICATION_JSON );
        assertThat( operation3.getProduces() ).containsOnly( MediaType.APPLICATION_JSON );
        assertThat( operation3.getDescription() ).isEqualTo( "Saves the given <code>DemoTo</code>s." );
        // check parameters
        parameters = operation3.getParameters();
        assertThat( parameters ).hasSize( 1 );
        parameter = parameters.get( 0 );
        assertThat( parameter ).isNotNull();
        assertThat( parameter.getName() ).isEqualTo( "objects" );
        assertThat( parameter.getLocation() ).isEqualTo( Descriptor.LOCATION_BODY );
        assertThat( parameter.getJavaScriptType() ).isEqualTo( JavaScriptType.ARRAY.getName() );
        assertThat( parameter.getDescription() ).isEqualTo( "the objects to save." );
        String example = "[{\n" + //
            "  \"id\" = 1,\n" + //
            "  \"modificationCounter\" = 1,\n" + //
            "  \"parent\" = {...},\n" + //
            "  \"revision\" = 1.0,\n" + //
            "  \"type\" = 1\n" + //
            "}]";
        assertThat( parameter.getExample() ).isEqualTo( example );
        assertThat( parameter.getDefaultValue() ).isEmpty();
        // check responses
        responses = operation3.getResponses();
        assertThat( responses ).hasSize( 1 );
        response = responses.get( 0 );
        assertThat( response ).isNotNull();
        assertThat( response.getStatusCode() ).isEqualTo( Descriptor.STATUS_CODE_SUCCESS );
        assertThat( response.getReason() ).isEqualTo( "Success" );
        assertThat( response.getJavaScriptType() ).isEqualTo( JavaScriptType.ARRAY.getName() );
        assertThat( response.getExample() ).isEqualTo( example );
    }

    private ServicesDescriptor analyze( String className )
        throws Exception
    {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceFolder( new File( "src/main/java" ) );
        builder.addSourceFolder( new File( "src/test/java" ) );
        Analyzer analyzer = getAnalyzer( builder );
        JavaClass serviceClass = builder.getClassByName( className );
        List<JavaClass> serviceClasses = Arrays.asList( serviceClass );
        return analyzer.createServicesDescriptor( serviceClasses );
    }
}
