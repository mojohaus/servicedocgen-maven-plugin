package org.codehaus.mojo.servicedocgen;

/**
 * {@link ServiceDocGenTemplate} is a template bean representing the templates passed into {@link ServiceDocGenReport}
 *
 * @author jguenther
 */
public class ServiceDocGenTemplate
{

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
     * @return the filename of the velocity template used for generation.
     */
    public String getTemplateName()
    {
        return this.templateName;
    }

    /**
     * @param templateName is the new value of {@link #getTemplateName()}.
     */
    public void setTemplateName( String templateName )
    {
        this.templateName = templateName;
    }

    /**
     * @return the filename of the file to generate from the template.
     */
    public String getOutputName()
    {
        return this.outputName;
    }

    /**
     * @param outputName is the new value of {@link #getOutputName()}.
     */
    public void setOutputName( String outputName )
    {

        this.outputName = outputName;
    }

    /**
     * @return the filename of the file to generate from the template. If {@link #getOutputName() output name} is not
     *         {@link #setOutputName(String) set}, it will fallback to the {@link #getTemplateName() template name}
     *         excluding the ".vm" extension.
     * @see #getOutputName()
     */
    public String getOutputNameWithFallback()
    {

        if ((this.outputName == null) || this.outputName.isEmpty()) {
            if ((this.templateName != null) && this.templateName.endsWith(".vm")) {
                return this.templateName.substring(0, this.templateName.length() - 3);
            }
        }
        return this.outputName;
    }

}
