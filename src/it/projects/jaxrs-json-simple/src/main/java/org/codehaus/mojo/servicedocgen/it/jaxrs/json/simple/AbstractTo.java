/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package org.codehaus.mojo.servicedocgen.it.jaxrs.json.simple;

/**
 * @author hohwille
 */
public class AbstractTo
{

    private Long id;

    private int version;

    /**
     * @return the id
     */
    public Long getId()
    {
        return this.id;
    }

    /**
     * @param id is the id to set
     */
    public void setId( Long id )
    {
        this.id = id;
    }

    /**
     * @return the version
     */
    public int getVersion()
    {
        return this.version;
    }

    /**
     * @param version is the version to set
     */
    public void setVersion( int version )
    {
        this.version = version;
    }

}
