/**
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id: TextFieldBean.java 4496 2006-02-08 20:27:04Z wehrens $
 */
package org.gridlab.gridsphere.provider.portletui.beans;

/**
 * The <code>TextFieldBean</code> represents a text field element
 */
public class TextFieldBean extends InputBean implements TagBean {

    public static final String NAME = "tf";

    /**
     * Constructs a default text field bean
     */
    public TextFieldBean() {
        super(NAME);
        this.inputtype = "text";
    }

    /**
     * Constructs a text field bean using the supplied bean name
     *
     * @param beanId the bean identifier
     */
    public TextFieldBean(String beanId) {
        super(NAME);
        this.beanId = beanId;
        this.inputtype = "text";
    }

    /**
     * Constructs a text field bean using the supplied bean name and identifier
     *
     * @param vbName   the visual bean name, a 2 character identifier
     * @param beanId the bean identifier
     */
    public TextFieldBean(String vbName, String beanId) {
        super(vbName);
        this.inputtype = "text";
        this.beanId = beanId;
    }

    public String toStartString() {
        return super.toStartString();
    }

}
