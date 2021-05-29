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
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.reflect.api.ReflectionUtil;

import org.codehaus.mojo.servicedocgen.Util;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * Rich representation of a {@link Method} with both byte-code and source-code analysis information.
 *
 * @author hohwille
 */
public class JMethod
{

    private final JType type;

    private final JavaMethod sourceMethod;

    private final Method byteMethod;

    private final List<JParameter> parameters;

    private final List<JException> exceptions;

    private final JReturn returns;

    private final String comment;

    /**
     * The constructor.
     *
     * @param byteMethod - see {@link #getByteMethod()}.
     * @param type - see {@link #getType()}.
     */
    public JMethod( Method byteMethod, JType type )
    {
        this( byteMethod, type, byteMethod );
    }

    /**
     * The constructor.
     *
     * @param byteMethod - see {@link #getByteMethod()}.
     * @param type - see {@link #getType()}.
     * @param annotatedParentMethod the {@link Method} containing the JAX-RS annotation that may be a parent
     *            {@link Method} of <code>byteMethod</code>.
     */
    public JMethod( Method byteMethod, JType type, Method annotatedParentMethod )
    {
        super();
        this.type = type;
        this.byteMethod = byteMethod;
        JavaClass sourceType = type.getSourceType();
        GenericType<?> byteType = type.getByteType();
        ReflectionUtil reflectionUtil = type.getReflectionUtil();
        JavaDocHelper javaDocHelper = type.getJavaDocHelper();
        this.sourceMethod = findSourceMethod( sourceType, byteMethod );

        // get source information
        List<JavaParameter> parameterSourceInfos = null;
        Map<String, String> parameterMap = null;
        List<JavaClass> exceptionSourceTypes = null;
        Map<String, String> exceptionMap = null;
        JavaClass returnSourceType = null;
        String returnComment = "";
        if ( this.sourceMethod != null )
        {
            this.comment = javaDocHelper.parseJavaDoc( sourceType, byteType, this.sourceMethod.getComment() );
            parameterSourceInfos = this.sourceMethod.getParameters();
            parameterMap = getTagListAsMap( this.sourceMethod.getTagsByName( "param" ), false );
            exceptionSourceTypes = this.sourceMethod.getExceptions();
            exceptionMap = getTagListAsMap( this.sourceMethod.getTagsByName( "throws" ), true );
            DocletTag returnTag = this.sourceMethod.getTagByName( "return" );
            if ( returnTag != null )
            {
                returnComment = javaDocHelper.parseJavaDoc( sourceType, byteType, returnTag.getValue() );
            }
            returnSourceType = this.sourceMethod.getReturns();
        }
        else
        {
            this.comment = "";
        }

        // create parameters
        Type[] parameterByteTypes = byteMethod.getGenericParameterTypes();
        Annotation[][] parameterAnnotations = annotatedParentMethod.getParameterAnnotations();
        this.parameters = new ArrayList<JParameter>( parameterByteTypes.length );
        for ( int i = 0; i < parameterByteTypes.length; i++ )
        {
            GenericType<?> parameterByteType = reflectionUtil.createGenericType( parameterByteTypes[i], byteType );
            JavaParameter parameterSourceType = get( parameterSourceInfos, i );
            String parameterComment = "";
            if ( ( parameterSourceType != null ) && ( parameterMap != null ) )
            {
                // QDox bug - not working...
                // comment = sourceParameter.getComment();
                parameterComment =
                    javaDocHelper.parseJavaDoc( sourceType, byteType, parameterMap.get( parameterSourceType.getName() ) );
            }
            this.parameters.add( new JParameter( parameterByteType, parameterAnnotations[i], parameterSourceType,
                                                 parameterComment, i ) );
        }

        // create exceptions
        Type[] exceptionByteTypes = byteMethod.getGenericExceptionTypes();
        this.exceptions = new ArrayList<JException>( exceptionByteTypes.length );
        for ( int i = 0; i < exceptionByteTypes.length; i++ )
        {
            JavaClass exceptionSourceType = get( exceptionSourceTypes, i );
            GenericType<?> exceptionByteType = reflectionUtil.createGenericType( exceptionByteTypes[i], byteType );
            String exceptionComment = "";
            if ( exceptionMap != null )
            {
                exceptionComment =
                    javaDocHelper.parseJavaDoc( sourceType,
                                                byteType,
                                                exceptionMap.get( exceptionByteType.getAssignmentClass().getSimpleName() ) );
            }
            this.exceptions.add( new JException( exceptionByteType, exceptionSourceType, exceptionComment ) );
        }

        // create return
        GenericType<?> returnByteType = reflectionUtil.createGenericType( byteMethod.getGenericReturnType(), byteType );
        this.returns = new JReturn( returnByteType, returnSourceType, returnComment );
    }

    private static <T> T get( List<T> list, int index )
    {

        if ( list == null )
        {
            return null;
        }
        if ( index >= list.size() )
        {
            return null;
        }
        return list.get( index );
    }

    private static JavaMethod findSourceMethod( JavaClass sourceClass, Method byteMethod )
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

    private static Map<String, String> getTagListAsMap( List<DocletTag> tagList, boolean simpleName )
    {
        Map<String, String> map = new HashMap<String, String>();
        for ( DocletTag tag : tagList )
        {
            String value = tag.getValue();
            int firstSpace = value.indexOf( ' ' );
            if ( firstSpace > 0 )
            {
                String subTag = value.substring( 0, firstSpace );
                if ( simpleName )
                {
                    subTag = Util.getSimpleName( subTag );
                }
                String comment = value.substring( firstSpace + 1 );
                map.put( subTag, comment );
            }
        }
        return map;
    }

    /**
     * @return the type
     */
    public JType getType()
    {
        return this.type;
    }

    /**
     * @return the sourceMethod
     */
    public JavaMethod getSourceMethod()
    {
        return this.sourceMethod;
    }

    /**
     * @return the byteMethod
     */
    public Method getByteMethod()
    {
        return this.byteMethod;
    }

    /**
     * @return the parameters
     */
    public List<JParameter> getParameters()
    {
        return this.parameters;
    }

    /**
     * @return the exceptions
     */
    public List<JException> getExceptions()
    {
        return this.exceptions;
    }

    /**
     * @return the returns
     */
    public JReturn getReturns()
    {
        return this.returns;
    }

    /**
     * @return the comment
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * @return the {@link Method#getName() name} of the {@link Method}.
     */
    public String getName()
    {

        return this.byteMethod.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return this.byteMethod.toString();
    }

}
