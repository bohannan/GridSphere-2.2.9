/**
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id: PasswordBean.java 4496 2006-02-08 20:27:04Z wehrens $
 */

package org.gridlab.gridsphere.provider.portletui.beans;

/**
 * A <code>PasswordBean</code> represents a password input element
 */
public class PasswordBean extends TextFieldBean {

    public static final String NAME = "pb";

    /**
     * Constructs a default password bean
     */
    public PasswordBean() {
        this.vbName = NAME;
        this.inputtype = "password";
    }

    /**
     * Constructs a password bean using a supplied portlet request and bean identifier
     *
     * @param beanId the bean identifier
     */
    public PasswordBean(String beanId) {
        super(NAME, beanId);
        this.inputtype = "password";
    }

    public String toStartString() {
        return super.toStartString();
    }
}
