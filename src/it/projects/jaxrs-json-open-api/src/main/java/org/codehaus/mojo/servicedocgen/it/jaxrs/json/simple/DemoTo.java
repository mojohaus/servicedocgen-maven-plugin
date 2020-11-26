/* Copyright (c) The m-m-m Team, Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0 */
package org.codehaus.mojo.servicedocgen.it.jaxrs.json.simple;

/**
 * @author hohwille
 */
public class DemoTo<T>
    extends AbstractTo
{

    private T type;

    private DemoTo<T> parent;

    /**
     * @return the type
     */
    public T getType()
    {
        return this.type;
    }

    /**
     * @param type is the type to set
     */
    public void setType( T type )
    {
        this.type = type;
    }

    /**
     * @return the parent
     */
    public DemoTo<T> getParent()
    {
        return this.parent;
    }

    /**
     * @param parent is the parent to set
     */
    public void setParent( DemoTo<T> parent )
    {
        this.parent = parent;
    }

}
