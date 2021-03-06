/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id: PortletServiceException.java 4496 2006-02-08 20:27:04Z wehrens $
 */
package org.gridlab.gridsphere.portlet.service;

import org.gridlab.gridsphere.portlet.PortletException;

/**
 * The <code>PortletServiceException</code> is the base class of all
 * checked exceptions thrown by portlet services.
 */
public class PortletServiceException extends PortletException {

    /**
     * Constructs a new portlet exception.
     */
    public PortletServiceException() {
        super();
    }

    /**
     * Creates a new exception with the sepcified detail message.
     *
     * @param message a string indicating why this exception is thrown.
     */
    public PortletServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new portlet exception with the given text.
     * The portlet container may use the text write it to a log.
     *
     * @param text  the exception text
     * @param cause the root cause
     */
    public PortletServiceException(String text, Throwable cause) {
        super(text, cause);
    }

    /**
     * Constructs a new portlet exception when the portlet needs to throw an exception.
     * The exception's message is based on the localized message of the underlying exception.
     *
     * @param cause the root cause
     */
    public PortletServiceException(Throwable cause) {
        super(cause);
    }

}
