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
 * @author hohwille
 */
public interface Descriptor
{

    String JS_TYPE_NUMBER = "number";

    String JS_TYPE_INTEGER = "integer";

    String JS_TYPE_BOOLEAN = "boolean";

    String JS_TYPE_ARRAY = "array";

    String JS_TYPE_STRING = "string";

    String JS_TYPE_OBJECT = "object";

    String HTTP_METHOD_GET = "get";

    String HTTP_METHOD_PUT = "put";

    String HTTP_METHOD_POST = "post";

    String HTTP_METHOD_DELETE = "delete";

    String HTTP_METHOD_OPTIONS = "options";

    String HTTP_METHOD_HEAD = "head";

    String HTTP_METHOD_PATCH = "patch";

    String LOCATION_QUERY = "query";

    String LOCATION_BODY = "body";

    String LOCATION_HEADER = "header";

    String LOCATION_PATH = "path";

    String LOCATION_FORM_DATA = "formData";

    String LOCATION_COOKIE = "cookie";

    String SCHEME_HTTP = "http";

    String SCHEME_HTTPS = "https";

    String SCHEME_WS = "ws";

    String SCHEME_WSS = "wss";

    String STATUS_CODE_SUCCESS = "200";

    String STATUS_CODE_NO_CONTENT = "204";

}
