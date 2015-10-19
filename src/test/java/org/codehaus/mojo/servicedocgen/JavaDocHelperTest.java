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
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.reflect.base.ReflectionUtilImpl;

import org.assertj.core.api.Assertions;
import org.codehaus.mojo.servicedocgen.descriptor.JavaDocDescriptor;
import org.junit.Test;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaType;

/**
 * Test-case for {@link JavaDocHelper}.
 *
 * @author hohwille
 */
public class JavaDocHelperTest
    extends Assertions
{

    public static final String JAVADOC_URL = "http://localhost/apidocs";

    private JavaDocHelper createJavaDocHelper( JavaProjectBuilder builder )
    {
        builder.addSourceFolder( new File( "src/main/java" ) );
        builder.addSourceFolder( new File( "src/test/java" ) );
        JavaDocHelper helper =
            new JavaDocHelper( Thread.currentThread().getContextClassLoader(), builder,
                               Arrays.asList( new JavaDocDescriptor( "", JAVADOC_URL ) ) );
        return helper;
    }

    private String parseMethodJavaDoc( String methodName )
    {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        JavaDocHelper helper = createJavaDocHelper( builder );
        JavaClass sourceClass = builder.getClassByName( JavaDocHelperTest.class.getName() );
        assertThat( sourceClass ).as( "test-method " + methodName ).isNotNull();
        List<JavaType> emptyList = Collections.emptyList();
        JavaMethod sourceMethod = sourceClass.getMethod( methodName, emptyList, false );
        GenericType<?> byteType = ReflectionUtilImpl.getInstance().createGenericType( JavaDocHelperTest.class );
        String parsedJavaDoc = helper.parseJavaDoc( sourceClass, byteType, sourceMethod.getComment() );
        return parsedJavaDoc;
    }

    /**
     * Test of {@link JavaDocHelper#parseJavaDoc(JavaClass, net.sf.mmm.util.reflect.api.GenericType, String)} with link
     * tag.
     */
    @Test
    public void testLink()
    {
        String parsedJavaDoc = parseMethodJavaDoc( "testLink" );
        String expected =
            "Test of <code><a href='"
                + JAVADOC_URL
                + "/org/codehaus/mojo/servicedocgen/JavaDocHelper.html"
                + "#parseJavaDoc-com.thoughtworks.qdox.model.JavaClass-net.sf.mmm.util.reflect.api.GenericType-java.lang.String-'>"
                + "JavaDocHelper.parseJavaDoc(JavaClass, GenericType, String)</a></code> with link tag.";
        assertThat( parsedJavaDoc ).isEqualTo( expected );
    }

    /**
     * Test of {@link UriInfo link tag to JEE class}.
     */
    public void testLinkJeeClass()
    {
        String parsedJavaDoc = parseMethodJavaDoc( "testLinkJeeClass" );
        String expected =
            "Test of <code><a href='" + JavaDocHelper.JAVADOC_JAVAEE_URL + "/javax/ws/rs/core/UriInfo.html'>"
                + "link tag to JEE class</a></code>.";
        assertThat( parsedJavaDoc ).isEqualTo( expected );
    }

    /**
     * Test of {@link JavaDocHelperTest#testLinkNonArg() link tag to non-arg method}.
     */
    @Test
    public void testLinkNonArg()
    {
        String parsedJavaDoc = parseMethodJavaDoc( "testLinkNonArg" );
        String expected =
            "Test of <code><a href='" + JAVADOC_URL + "/org/codehaus/mojo/servicedocgen/JavaDocHelperTest.html"
                + "#testLinkNonArg--'>link tag to non-arg method</a></code>.";
        assertThat( parsedJavaDoc ).isEqualTo( expected );
    }

    /**
     * Test of {@linkplain JavaDocHelper#parseJavaDoc(JavaClass, String)} with linkplain tag.
     */
    @Test
    public void testLinkplain()
    {
        String parsedJavaDoc = parseMethodJavaDoc( "testLinkplain" );
        String expected =
            "Test of <a href='" + JAVADOC_URL + "/org/codehaus/mojo/servicedocgen/JavaDocHelper.html"
                + "#parseJavaDoc-com.thoughtworks.qdox.model.JavaClass-java.lang.String-'>"
                + "JavaDocHelper.parseJavaDoc(JavaClass, String)</a> with linkplain tag.";
        assertThat( parsedJavaDoc ).isEqualTo( expected );
    }

    /**
     * Test of {@code JavaDocHelper.parseJavaDoc(JavaClass, String)} with code tag.
     */
    @Test
    public void testCode()
    {
        String parsedJavaDoc = parseMethodJavaDoc( "testCode" );
        String expected = "Test of <code>JavaDocHelper.parseJavaDoc(JavaClass, String)</code> with code tag.";
        assertThat( parsedJavaDoc ).isEqualTo( expected );
    }

    /**
     * Test of {@literal JavaDocHelper.parseJavaDoc<JavaClass, String>} with literal tag.
     */
    @Test
    public void testLiteral()
    {
        String parsedJavaDoc = parseMethodJavaDoc( "testLiteral" );
        String expected = "Test of <code>JavaDocHelper.parseJavaDoc&lt;JavaClass, String&gt;</code> with literal tag.";
        assertThat( parsedJavaDoc ).isEqualTo( expected );
    }

    /**
     * Test with value tag {@value #JAVADOC_URL} and {@value JavaDocHelper#JAVADOC_JAVASE_URL}.
     */
    @Test
    public void testValue()
    {
        String parsedJavaDoc = parseMethodJavaDoc( "testValue" );
        String expected = "Test with value tag " + JAVADOC_URL + " and " + JavaDocHelper.JAVADOC_JAVASE_URL + ".";
        assertThat( parsedJavaDoc ).isEqualTo( expected );
    }
}
