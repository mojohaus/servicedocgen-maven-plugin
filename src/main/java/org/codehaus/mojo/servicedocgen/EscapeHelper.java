package org.codehaus.mojo.servicedocgen;

/**
 * Class with static methods for escaping strings in velocity templates.
 *
 * @author jguenther
 */
public class EscapeHelper
{

    /**
    * @param jsonString the json string to escape
    * @return the escaped json string
    */
    public static String escapeJson( String jsonString )
    {
        return jsonString.replace( "\"", "\\\"" );
    }
}
