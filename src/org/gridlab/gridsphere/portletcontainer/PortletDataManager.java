/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id: PortletDataManager.java 4496 2006-02-08 20:27:04Z wehrens $
 */
package org.gridlab.gridsphere.portletcontainer;

import org.gridlab.gridsphere.core.persistence.PersistenceManagerException;
import org.gridlab.gridsphere.portlet.PortletData;
import org.gridlab.gridsphere.portlet.User;

/**
 * The <code>PortletDataManager</code> is a singleton used for loading and storing <code>PortletData</code>,
 * persistent user data maintained for each concrete portlet instance.
 */
public interface PortletDataManager {

    /**
     * Returns the users portlet data for the specified portlet
     *
     * @param user      the <code>User</code>
     * @param portletID the concrete portlet id
     * @return the PortletData for this portlet or null if none exists
     * @throws PersistenceManagerException if a persistence error occurs
     */
    PortletData getPortletData(User user, String portletID) throws PersistenceManagerException;

}
