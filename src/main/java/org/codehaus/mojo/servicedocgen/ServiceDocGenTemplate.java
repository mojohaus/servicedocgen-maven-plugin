package org.codehaus.mojo.servicedocgen;

/**
 * {@link ServiceDocGenTemplate} is a template bean representing the templates passed into {@link ServiceDocGenReport}
 * @author jguenther
 */
public class ServiceDocGenTemplate {
    
    public ServiceDocGenTemplate()
    {
        
    }
    
    public ServiceDocGenTemplate( String templateName, String outputName )
    {
        this.templateName = templateName;
        this.outputName = outputName;
    }
    
    private String templateName;
    
    private String outputName;
    
    /**
     * @return the templateName
     */
    public String getTemplateName() 
    {
        return this.templateName;
    }
    
    /**
     * @param templateName is the templateName to set
     */
    public void setTemplateName( String templateName )
    {
        this.templateName = templateName;
    }
    
    /**
     * @return the outputName
     */
    public String getOutputName()
    {
        return this.outputName;
    }
    
    /**
     * @param outputName is the output name of the file to set
     */
    public void setOutputName( String outputName )
    {
        this.outputName = outputName;
    }

}
