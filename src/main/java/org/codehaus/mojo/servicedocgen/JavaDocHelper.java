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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.mmm.util.filter.api.CharFilter;
import net.sf.mmm.util.reflect.api.GenericType;
import net.sf.mmm.util.scanner.base.CharSequenceScanner;

import org.apache.velocity.tools.generic.EscapeTool;
import org.codehaus.mojo.servicedocgen.descriptor.JavaDocDescriptor;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Helper to parse JavaDoc tags and transform to regular HTML.
 *
 * @see #parseJavaDoc(JavaClass, GenericType, String)
 * @author hohwille
 */
public class JavaDocHelper
{

    private static final String CODE_START = "<code>";

    private static final String CODE_END = "</code>";

    private static final Pattern PATTERN_JAVADOC_TAG = Pattern.compile( "\\{@([a-zA-Z]+) ([^}]*)\\}" );

    private static final String TAG_LINK = "link";

    private static final String TAG_LINKPLAIN = "linkplain";

    private static final String TAG_CODE = "code";

    private static final String TAG_LITERAL = "literal";

    private static final String TAG_VALUE = "value";

    private static final Set<String> JAVA_LANG_TYPES =
        new HashSet<String>( Arrays.asList( "AbstractMethodError", "AbstractStringBuilder", "Appendable",
                                            "ArithmeticException", "ArrayIndexOutOfBoundsException",
                                            "ArrayStoreException", "AssertionError", "AutoCloseable", "Boolean",
                                            "BootstrapMethodError", "Byte", "Character", "CharSequence", "Class",
                                            "ClassCastException", "ClassCircularityError", "ClassFormatError",
                                            "ClassLoader", "ClassNotFoundException", "ClassValue", "Cloneable",
                                            "CloneNotSupportedException", "Comparable", "Compiler", "Deprecated",
                                            "Double", "Enum", "EnumConstantNotPresentException", "Error", "Exception",
                                            "ExceptionInInitializerError", "Float", "FunctionalInterface",
                                            "IllegalAccessError", "IllegalAccessException", "IllegalArgumentException",
                                            "IllegalMonitorStateException", "IllegalStateException",
                                            "IllegalThreadStateException", "IncompatibleClassChangeError",
                                            "IndexOutOfBoundsException", "InheritableThreadLocal",
                                            "InstantiationError", "InstantiationException", "Integer", "InternalError",
                                            "InterruptedException", "Iterable", "LinkageError", "Long", "Math",
                                            "NegativeArraySizeException", "NoClassDefFoundError", "NoSuchFieldError",
                                            "NoSuchFieldException", "NoSuchMethodError", "NoSuchMethodException",
                                            "NullPointerException", "Number", "NumberFormatException", "Object",
                                            "OutOfMemoryError", "Override", "Package", "Process", "ProcessBuilder",
                                            "Readable", "ReflectiveOperationException", "Runnable", "Runtime",
                                            "RuntimeException", "RuntimePermission", "SafeVarargs",
                                            "SecurityException", "SecurityManager", "Short", "StackOverflowError",
                                            "StackTraceElement", "StrictMath", "String", "StringBuffer",
                                            "StringBuilder", "StringIndexOutOfBoundsException", "SuppressWarnings",
                                            "System", "Thread", "ThreadDeath", "ThreadGroup", "ThreadLocal",
                                            "Throwable", "TypeNotPresentException", "UnknownError",
                                            "UnsatisfiedLinkError", "UnsupportedClassVersionError",
                                            "UnsupportedOperationException", "VerifyError", "VirtualMachineError",
                                            "Void" ) );

    public static final String JAVADOC_JAVASE_URL = "http://docs.oracle.com/javase/8/docs/api/";

    public static final String JAVADOC_JAVAEE_URL = "https://docs.oracle.com/javaee/7/api/";

    private static final List<JavaDocDescriptor> JAVADOCS =
        Arrays.asList( new JavaDocDescriptor( "java", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.accessibility", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.activation", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.activity", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.annotation.processing", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.crypto", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.imageio", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.jws", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.lang.model", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.management.j2ee", JAVADOC_JAVAEE_URL ),
                       new JavaDocDescriptor( "javax.management", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.naming", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.net", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.print", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.rmi", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.script", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.security.auth.message", JAVADOC_JAVAEE_URL ),
                       new JavaDocDescriptor( "javax.security.auth", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.security.cert", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.security.sasl", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.sound", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.sql", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.swing", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.tools", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.transaction", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.transaction.xa", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.bind", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.crypto", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.datatype", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.namespace", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.parsers", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.soap", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.stream", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.transform", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.validation", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.ws", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax.xml.xpath", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "javax", JAVADOC_JAVAEE_URL ),
                       new JavaDocDescriptor( "javafx", "https://docs.oracle.com/javafx/2/api/" ),
                       new JavaDocDescriptor( "org.w3c.dom", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "org.xml.sax", JAVADOC_JAVASE_URL ),
                       new JavaDocDescriptor( "org.springframework",
                                              "http://docs.spring.io/spring/docs/current/javadoc-api/" ),
                       new JavaDocDescriptor( "org.apache.http",
                                              "https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/" ),
                       new JavaDocDescriptor( "com.google.common", "docs.guava-libraries.googlecode.com/git/javadoc/" ) );

    private static final CharFilter NO_SPACE_OR_HASH = new CharFilter()
    {
        public boolean accept( char c )
        {
            if ( ( c == '#' ) || ( c == ' ' ) )
            {
                return false;
            }
            return true;
        }
    };

    private static final CharFilter NO_SPACE_OR_OPENING_BRACE = new CharFilter()
    {
        public boolean accept( char c )
        {
            if ( ( c == '(' ) || ( c == ' ' ) )
            {
                return false;
            }
            return true;
        }
    };

    private static final CharFilter NO_COMMA_OR_CLOSING_BRACE = new CharFilter()
    {
        public boolean accept( char c )
        {
            if ( ( c == ',' ) || ( c == ')' ) )
            {
                return false;
            }
            return true;
        }
    };

    private final List<JavaDocDescriptor> javaDocs;

    private final EscapeTool escapeTool;

    private final JavaProjectBuilder builder;

    private final ClassLoader classloader;

    /**
     * The constructor.
     *
     * @param builder
     * @param javaDocUrl
     */
    public JavaDocHelper( ClassLoader classloader, JavaProjectBuilder builder, List<JavaDocDescriptor> javaDocs )
    {
        super();
        this.classloader = classloader;
        this.builder = builder;
        this.javaDocs = new ArrayList<JavaDocDescriptor>();
        Map<String, JavaDocDescriptor> map = new HashMap<String, JavaDocDescriptor>();
        for ( JavaDocDescriptor javaDocDescriptor : javaDocs )
        {
            String url = javaDocDescriptor.getUrl();
            if ( !url.endsWith( "/" ) )
            {
                javaDocDescriptor.setUrl( url + "/" );
            }
            map.put( javaDocDescriptor.getPackagePrefix(), javaDocDescriptor );
        }
        for ( JavaDocDescriptor javaDocDescriptor : JAVADOCS )
        {
            JavaDocDescriptor overridden = map.remove( javaDocDescriptor.getPackagePrefix() );
            if ( overridden == null )
            {
                this.javaDocs.add( javaDocDescriptor );
            }
            else
            {
                this.javaDocs.add( overridden );
            }
        }
        for ( JavaDocDescriptor javaDocDescriptor : javaDocs )
        {
            if ( map.containsKey( javaDocDescriptor.getPackagePrefix() ) )
            {
                this.javaDocs.add( javaDocDescriptor );
            }
        }
        this.escapeTool = new EscapeTool();
    }

    public String getQualifiedName( JavaSource source, String simpleName )
    {
        for ( String importStatement : source.getImports() )
        {
            if ( simpleName.equals( getSimpleName( importStatement ) ) )
            {
                return importStatement;
            }
        }
        String packageName = source.getPackageName();
        if ( packageName.isEmpty() )
        {
            // default package...
            return simpleName;
        }
        else if ( JAVA_LANG_TYPES.contains( simpleName ) )
        {
            return "java.lang." + simpleName;
        }
        return packageName + "." + simpleName;
    }

    public String parseJavaDoc( JavaClass sourceClass, GenericType<?> byteClass, String jdoc )
    {
        if ( ( jdoc == null ) || jdoc.isEmpty() )
        {
            return "";
        }
        String javadoc = jdoc.trim().replace( "\n", " " ).replace( "\r", "" );
        Matcher matcher = PATTERN_JAVADOC_TAG.matcher( javadoc );
        StringBuffer buffer = new StringBuffer();
        while ( matcher.find() )
        {
            String tag = matcher.group( 1 );
            String text = matcher.group( 2 );
            String replacement;
            if ( TAG_LINK.equals( tag ) )
            {
                replacement = parseLink( sourceClass, text, false );
            }
            else if ( TAG_LINKPLAIN.equals( tag ) )
            {
                replacement = parseLink( sourceClass, text, true );
            }
            else if ( TAG_CODE.equals( tag ) )
            {
                replacement = CODE_START + text + CODE_END;
            }
            else if ( TAG_LITERAL.equals( tag ) )
            {
                replacement = CODE_START + this.escapeTool.html( text ) + CODE_END;
            }
            else if ( TAG_VALUE.equals( tag ) )
            {
                replacement = resolveValue( sourceClass, byteClass, text );
            }
            else
            {
                // unknown tag...
                replacement = text;
            }
            matcher.appendReplacement( buffer, replacement );
        }
        matcher.appendTail( buffer );
        return buffer.toString();
    }

    private String resolveValue( JavaClass sourceClass, GenericType<?> byteClass, String text )
    {
        int hashIndex = text.indexOf( '#' );
        if ( hashIndex == 0 )
        {
            String fieldName = text.substring( 1 );
            return getFieldValue( byteClass.getAssignmentClass(), fieldName );
        }
        else if ( hashIndex > 0 )
        {
            String className = text.substring( 0, hashIndex );
            String fieldName = text.substring( hashIndex + 1 );
            if ( !className.contains( "." ) )
            {
                className = getQualifiedName( sourceClass.getParentSource(), className );
            }
            try
            {
                Class<?> clazz = this.classloader.loadClass( className );
                return getFieldValue( clazz, fieldName );
            }
            catch ( ClassNotFoundException e )
            {
                return fieldName;
            }
        }

        return text;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private String getFieldValue( Class<?> clazz, String fieldName )
    {
        if ( clazz.isEnum() )
        {
            Enum<?> value = Enum.valueOf( (Class) clazz, fieldName );
            return toString( value, fieldName );
        }
        Field field = null;
        try
        {
            field = clazz.getField( fieldName );
        }
        catch ( Exception e )
        {
            while ( ( clazz != null ) && ( field == null ) )
            {
                for ( Field f : clazz.getDeclaredFields() )
                {
                    if ( f.getName().equals( fieldName ) )
                    {
                        field = f;
                        field.setAccessible( true );
                        break;
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
        if ( field == null )
        {
            return fieldName;
        }
        if ( Modifier.isStatic( field.getModifiers() ) )
        {
            try
            {
                Object value = field.get( null );
                return toString( value, fieldName );
            }
            catch ( Exception e )
            {
                // ignore
            }
        }

        return fieldName;
    }

    private String toString( Object value, String fallback )
    {

        if ( value == null )
        {
            return fallback;
        }
        return value.toString();
    }

    private String parseLink( JavaClass sourceClass, String text, boolean plain )
    {
        JavaSource parentSource = sourceClass.getParentSource();
        String ref = text.trim();
        CharSequenceScanner scanner = new CharSequenceScanner( ref );

        String className = scanner.readWhile( NO_SPACE_OR_HASH );
        String simpleName;
        String qualifiedName;
        String title;
        if ( className.isEmpty() )
        {
            title = "";
            simpleName = sourceClass.getName();
            qualifiedName = sourceClass.getFullyQualifiedName();
        }
        else if ( className.contains( "." ) )
        {
            qualifiedName = className;
            simpleName = getSimpleName( className );
            title = simpleName;
        }
        else
        {
            simpleName = className;
            qualifiedName = getQualifiedName( parentSource, simpleName );
            title = simpleName;
        }

        char separator = scanner.forceNext();
        String anchor = "";
        if ( separator == '#' )
        {
            StringBuilder titleBuffer = new StringBuilder( title );
            if ( !title.isEmpty() )
            {
                titleBuffer.append( '.' );
            }
            String elementName = scanner.readWhile( NO_SPACE_OR_OPENING_BRACE );
            StringBuilder anchorBuffer = new StringBuilder( "#" );
            anchorBuffer.append( elementName );
            titleBuffer.append( elementName );
            char elementSeparator = scanner.forceNext();
            if ( elementSeparator == '(' )
            {
                titleBuffer.append( '(' );
                anchorBuffer.append( '-' );
                String comma = "";
                do
                {
                    titleBuffer.append( comma );
                    String argType = scanner.readWhile( NO_COMMA_OR_CLOSING_BRACE ).trim();
                    elementSeparator = scanner.forceNext();
                    if ( argType.isEmpty() )
                    {
                        if ( elementSeparator == ')' )
                        {
                            anchorBuffer.append( '-' );
                            break;
                        }
                    }
                    else
                    {
                        if ( argType.contains( "." ) )
                        {
                            anchorBuffer.append( argType );
                            titleBuffer.append( getSimpleName( argType ) );
                        }
                        else
                        {
                            anchorBuffer.append( getQualifiedName( parentSource, argType ) );
                            titleBuffer.append( argType );
                        }
                        anchorBuffer.append( '-' );
                        comma = ", ";
                    }
                }
                while ( elementSeparator == ',' );
                titleBuffer.append( ')' );
            }
            title = titleBuffer.toString();
            anchor = anchorBuffer.toString();
        }
        scanner.skipWhile( ' ' );
        if ( scanner.hasNext() )
        {
            title = scanner.read( Integer.MAX_VALUE );
        }

        StringBuilder buffer = new StringBuilder();
        if ( !plain )
        {
            buffer.append( CODE_START );
        }
        String javadocUrl = findJavaDocUrl( qualifiedName );
        if ( javadocUrl == null )
        {
            buffer.append( title );
        }
        else
        {
            buffer.append( "<a href='" );
            buffer.append( javadocUrl );
            buffer.append( qualifiedName.replace( '.', '/' ) );
            buffer.append( ".html" );
            buffer.append( anchor );
            buffer.append( "'>" );
            buffer.append( title );
            buffer.append( "</a>" );
        }
        if ( !plain )
        {
            buffer.append( CODE_END );
        }
        return buffer.toString();
    }

    /**
     * @param qualifiedName
     * @return
     */
    private String findJavaDocUrl( String qualifiedName )
    {
        for ( JavaDocDescriptor javaDocDescriptor : this.javaDocs )
        {
            if ( qualifiedName.startsWith( javaDocDescriptor.getPackagePrefix() ) )
            {
                return javaDocDescriptor.getUrl();
            }
        }
        return null;
    }

    private String getSimpleName( String className )
    {

        int lastDot = className.lastIndexOf( '.' );
        if ( lastDot > 0 )
        {
            return className.substring( lastDot + 1 );
        }
        return className;
    }
}
