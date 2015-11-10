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

import java.util.Set;

/**
 * Utility class with static helper methods.
 *
 * @author hohwille
 */
public final class Util
{

    /**
     * Construction forbidden.
     */
    private Util()
    {
        super();
    }

    /**
     * @param className a {@link Class#getName() class name}.
     * @return the {@link Class#getSimpleName() simple name}.
     */
    public static String getSimpleName( String className )
    {

        int lastDot = className.lastIndexOf( '.' );
        if ( lastDot > 0 )
        {
            return className.substring( lastDot + 1 );
        }
        return className;
    }

    /**
     * @param value the value to get as {@link String}. May be <code>null</code>.
     * @param fallback the fallback value.
     * @return {@link Object#toString()} of <code>value</code> or <code>fallback</code> if <code>value</code> is
     *         <code>null</code>.
     */
    public static String toString( Object value, String fallback )
    {

        if ( value == null )
        {
            return fallback;
        }
        return value.toString();
    }

    /**
     * @param value the {@link String} to check. May be <code>null</code>.
     * @return <code>true</code> if the given {@link String} is {@link String#isEmpty() empty} or <code>null</code>,
     *         <code>false</code> otherwise.
     */
    public static boolean isEmpty( String value )
    {

        if ( value == null )
        {
            return true;
        }
        return value.isEmpty();
    }

    /**
     * @param pathPrefix the base-bath.
     * @param path the path to append.
     * @return the concatenation of both paths with an intermediate slash ("/") if needed.
     */
    public static String appendPath( String pathPrefix, String path )
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

    /**
     * @param value the {@link String} to trim.
     * @return the {@link String#trim() trimmed} {@link String} or <code>null</code> if <code>null</code> was given.
     */
    public static String getTrimmed( String value )
    {

        if ( value == null )
        {
            return "";
        }
        return value.trim();
    }

    /**
     * @param set a {@link Set} of {@link String}s.
     * @param substring the substring to look for.
     * @return <code>true</code> if the given {@link Set} contains a {@link String} that
     *         {@link String#contains(CharSequence) contains} the given <code>substring</code>, <code>false</code>
     *         otherwise.
     */
    public static boolean containsSubstring( Set<String> set, String substring )
    {
        if ( set == null )
        {
            return false;
        }
        for ( String s : set )
        {
            if ( s.contains( substring ) )
            {
                return true;
            }
        }
        return false;
    }

}
