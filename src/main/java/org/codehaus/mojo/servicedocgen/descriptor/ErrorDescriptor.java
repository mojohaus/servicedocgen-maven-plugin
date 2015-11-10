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
package org.codehaus.mojo.servicedocgen.descriptor;

import java.util.regex.Pattern;

import net.sf.mmm.util.pattern.base.RegexInfixPatternCompiler;

/**
 * {@link Descriptor} for errors/exceptions (any kind of {@link Throwable}).
 *
 * @author hohwille
 */
public class ErrorDescriptor
    extends AbstractDescriptor
{

    /** Default for {@link #getJsonExample()}. */
    public static final String DEFAULT_JSON_EXAMPLE = "{\"message\": \"text\",\n" + //
        "  \"code\": \"text\",\n" + //
        "  \"uuid\": \"text\",\n" + //
        "  \"errors\": {\n" + //
        "    \"bean.property.path\": [\n" + //
        "       \"Error message 1\",\n" + //
        "       \"Error message 2\"\n" + //
        "    ], \n" + //
        "    ...\n" + //
        "  }\n" + //
        "}";

    /** Default for {@link #getXmlExample()}. */
    public static final String DEFAULT_XML_EXAMPLE = "<error code='text' uuid='text'>\n" + //
        "  <message>text</message>\n" + //
        "  <violations>\n" + //
        "    <violation path='bean.property.path'>\n" + //
        "      <message>Error message 1</message>\n" + //
        "      <message>Error message 2</message>\n" + //
        "    </violation>\n" + //
        "    ...\n" + //
        "  </violations>\n" + //
        "</error>";

    private String errorName;

    private Match match = Match.regex;

    private transient Pattern errorNamePattern;

    private transient Class<?> errorClass;

    private String jsonExample = DEFAULT_JSON_EXAMPLE;

    private String xmlExample = DEFAULT_XML_EXAMPLE;

    private String statusCode = STATUS_CODE_INTERNAL_SERVER_ERROR;

    private String comment = "";

    /**
     * @return the errorNameRegex
     */
    public String getErrorName()
    {
        if ( this.errorName == null )
        {
            return "";
        }
        return this.errorName;
    }

    /**
     * @param errorNameRegex is the errorNameRegex to set
     */
    public void setErrorName( String errorNameRegex )
    {
        this.errorName = errorNameRegex;
    }

    /**
     * @return the jsonExample
     */
    public String getJsonExample()
    {
        return this.jsonExample;
    }

    /**
     * @param jsonExample is the jsonExample to set
     */
    public void setJsonExample( String jsonExample )
    {
        this.jsonExample = jsonExample;
    }

    /**
     * @return the xmlExample
     */
    public String getXmlExample()
    {
        return this.xmlExample;
    }

    /**
     * @param xmlExample is the xmlExample to set
     */
    public void setXmlExample( String xmlExample )
    {
        this.xmlExample = xmlExample;
    }

    /**
     * @return the statusCode
     */
    public String getStatusCode()
    {
        return this.statusCode;
    }

    /**
     * @param statusCode is the statusCode to set
     */
    public void setStatusCode( String statusCode )
    {
        this.statusCode = statusCode;
    }

    /**
     * @return the comment to use if no more specific comment is available (esp. in case of {@link Match#always}).
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * @param comment is the comment to set
     */
    public void setComment( String comment )
    {
        this.comment = comment;
    }

    /**
     * @return the errorNamePattern
     */
    public Pattern getErrorNamePattern()
    {
        if ( ( this.errorNamePattern == null ) && ( this.errorName != null ) )
        {
            if ( this.errorName.isEmpty() )
            {
                this.errorNamePattern = Pattern.compile( ".*" );
            }
            else
            {
                this.errorNamePattern = RegexInfixPatternCompiler.INSTANCE.compile( this.errorName );
            }
        }
        return this.errorNamePattern;
    }

    /**
     * @return the match
     */
    public Match getMatch()
    {
        return this.match;
    }

    /**
     * @param match is the match to set
     */
    public void setMatch( Match match )
    {
        this.match = match;
    }

    /**
     * @return the errorClass
     */
    public Class<?> getErrorClass()
    {
        return this.errorClass;
    }

    /**
     * @param errorClass is the errorClass to set
     */
    public void setErrorClass( Class<?> errorClass )
    {
        this.errorClass = errorClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return getErrorName() + "[" + this.match + "]@" + this.statusCode;
    }

    /**
     * Mode to match {@link ErrorDescriptor#getErrorName() error name}.
     */
    public static enum Match
    {

        /** As {@link Pattern regular expression}. */
        regex,

        /** As {@link Class} using {@link Class#isAssignableFrom(Class)}. */
        assignable,

        /** This error is added as response to every operation independent of declared exceptions. */
        always
    }

}
