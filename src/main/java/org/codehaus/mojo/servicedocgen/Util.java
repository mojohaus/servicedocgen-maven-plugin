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

    public static String getSimpleName( String className )
    {

        int lastDot = className.lastIndexOf( '.' );
        if ( lastDot > 0 )
        {
            return className.substring( lastDot + 1 );
        }
        return className;
    }

    public static String toString( Object value, String fallback )
    {

        if ( value == null )
        {
            return fallback;
        }
        return value.toString();
    }

    public static boolean isEmpty( String value )
    {

        if ( value == null )
        {
            return true;
        }
        return value.isEmpty();
    }

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

    public static String getTrimmed( String value )
    {

        if ( value == null )
        {
            return "";
        }
        return value.trim();
    }

}
