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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.mojo.servicedocgen.descriptor.ServicesDescriptor;

/**
 * {@link AbstractMojo Maven Plugin} to automatically generate OpenAPI specification documentation for services of the current project.
 * <ol>
 * <li>Scans the current projects source code for (JAX-RS annotated) services that match the RegEx configured by
 * <code>classnameRegex</code>.</li>
 * <li>Analyzes the services from source-code (extract JavaDoc, etc.) and byte-code (resolve generic parameters, etc.)
 * and create intermediate meta-data as {@link ServicesDescriptor}.</li>
 * <li>Generates the OpenAPI specification from the collected meta-data as JSON file from a velocity template.</li>
 * </ol>
 *
 * @author jguenther
 */
@Mojo(name = "openapi_json", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresProject = true, requiresDirectInvocation = false, executionStrategy = "once-per-session", requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ServiceOpenApiJsonReport extends AbstractReportGen {
  @Parameter(defaultValue = "OpenApi.json.vm")
  private String templateName;

  @Parameter(defaultValue = "openapi.json")
  private String outputName;

  /**
   * The constructor.
   *
   */
  public ServiceOpenApiJsonReport() {

  }

  @Override
  public String getTemplateName() {

    return this.templateName;
  }

  @Override
  public String getOutputName() {

    return this.outputName;
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.OPENAPI_JSON;
  }

}