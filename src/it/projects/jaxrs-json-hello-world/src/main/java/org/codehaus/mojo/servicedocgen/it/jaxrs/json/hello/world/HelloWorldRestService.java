/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package org.codehaus.mojo.servicedocgen.it.jaxrs.json.hello.world;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This is a REST-Service to say hello to the world.
 *
 * @author hohwille
 */
@Path( "/hello-world/v1" )
@Consumes( MediaType.APPLICATION_JSON )
@Produces( MediaType.APPLICATION_JSON )
public interface HelloWorldRestService
{
    /**
     * Says hello to the world.
     *
     * @return the String "hello-world".
     */
    @GET
    @Path( "/world" )
    String helloWorld();

    /**
     * Echos the given message.
     *
     * @param message the message to echo.
     * @return the given message.
     * @throws IllegalArgumentException in the given message is invalid (e.g. {@code null}).
     */
    @POST
    @Path( "/echo" )
    String echo( String message )
        throws IllegalArgumentException;

    /**
     * Deletes the world.<br/>
     * <b>ATTENTION:</b> This method will delete the entire world including yourself. Never call this method!
     */
    @DELETE
    @Path( "/world" )
    void deleteWorld();

}
