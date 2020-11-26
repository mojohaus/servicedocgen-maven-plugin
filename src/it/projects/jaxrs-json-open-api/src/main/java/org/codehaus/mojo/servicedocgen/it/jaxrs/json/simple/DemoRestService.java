/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package org.codehaus.mojo.servicedocgen.it.jaxrs.json.simple;

import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This is a cool REST-Service to demonstrate <code>servicedocgen-maven-plugin</code>.
 *
 * @author hohwille
 */
@Path( "/demo/v1" )
@Consumes( MediaType.APPLICATION_JSON )
@Produces( MediaType.APPLICATION_JSON )
public interface DemoRestService
{
    /**
     * Finds the {@link DemoTo} with the given <code>id</code>.
     *
     * @param id is the primary key of the requested object.
     * @return the object with the given id.
     */
    @GET
    @Path( "/string/{id}" )
    DemoTo<String> findString( @PathParam( "id" ) long id );

    /**
     * Saves the given {@link DemoTo}.
     *
     * @param object the object to save.
     * @return the updated object with id and version from database.
     * @throws ConstraintViolationException in case of a validation error.
     */
    @POST
    @Path( "/string" )
    DemoTo<String> saveString( DemoTo<String> object )
        throws ConstraintViolationException;

    /**
     * Deletes the {@link DemoTo} with the given <code>id</code>.
     *
     * @param id the primary key of the object to delete.
     */
    @DELETE
    @Path( "/string/{id}" )
    void deleteString( @PathParam( "id" ) long id );

    /**
     * Saves the given {@link DemoTo}s.
     *
     * @param objects the object to save.
     * @return the updated objects with id and version from database.
     */
    @POST
    @Path( "/longs" )
    List<DemoTo<Long>> saveLongs( List<DemoTo<Long>> objects );

    /**
     * Test operation that always throws an error.
     *
     * @throws java.lang.IllegalStateException in every case.
     */
    @GET
    @Path( "/error" )
    void testError()
        throws IllegalStateException;

}
