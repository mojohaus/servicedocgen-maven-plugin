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

/**
 * Interface for a descriptor with meta-data about services.
 *
 * @see ServicesDescriptor
 * @author hohwille
 */
public interface Descriptor
{

    /** HTTP GET method. */
    String HTTP_METHOD_GET = "get";

    /** HTTP PUT method. */
    String HTTP_METHOD_PUT = "put";

    /** HTTP POST method. */
    String HTTP_METHOD_POST = "post";

    /** HTTP DELETE method. */
    String HTTP_METHOD_DELETE = "delete";

    /** HTTP OPTIONS method. */
    String HTTP_METHOD_OPTIONS = "options";

    /** HTTP HEAD method. */
    String HTTP_METHOD_HEAD = "head";

    /** HTTP PATCH method. */
    String HTTP_METHOD_PATCH = "patch";

    /** HTTP QUERY method. */
    String LOCATION_QUERY = "query";

    /** HTTP BODY method. */
    String LOCATION_BODY = "body";

    /** HTTP HEADER method. */
    String LOCATION_HEADER = "header";

    /** REST parameter location for URL path. */
    String LOCATION_PATH = "path";

    /** REST parameter location for form data. */
    String LOCATION_FORM_DATA = "formData";

    /** REST parameter location for cookie. */
    String LOCATION_COOKIE = "cookie";

    /** Protocol scheme for HTTP (HypterText Transfer Protocol). */
    String SCHEME_HTTP = "http";

    /** Protocol scheme for HTTPS (HTTP Secured). */
    String SCHEME_HTTPS = "https";

    /** Protocol scheme for WS (Web Socket). */
    String SCHEME_WS = "ws";

    /** Protocol scheme for WSS (Web Socket Secured). */
    String SCHEME_WSS = "wss";

    /** HTTP status code for success with content. */
    String STATUS_CODE_SUCCESS = "200";

    /** HTTP status code for success without content. */
    String STATUS_CODE_NO_CONTENT = "204";

    /** HTTP status code for unauthorized (authentication required). */
    String STATUS_CODE_UNAUTHORIZED = "401";

    /** HTTP status code for unauthorized (permission denied). */
    String STATUS_CODE_FORBIDDEN = "403";

    /** HTTP status code for conflict. */
    String STATUS_CODE_CONFLICT = "409";

    /** HTTP status code for internal server error. */
    String STATUS_CODE_INTERNAL_SERVER_ERROR = "500";

}
