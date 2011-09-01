/*
 * @author <a href="mailto:novotny@aei.mpg.de">Jason Novotny</a>
 * @version $Id: PortletURLImpl.java 4895 2006-06-30 17:20:26Z novotny $
 */
package org.gridlab.gridsphere.portlet.jsrimpl;

import org.gridlab.gridsphere.portlet.PortletWindow;
import org.gridlab.gridsphere.portlet.impl.SportletProperties;
import org.gridlab.gridsphere.portletcontainer.GridSphereConfig;

import javax.portlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


/**
 * The <CODE>PortletURL</CODE> interface represents a URL
 * that reference the portlet itself.
 * <p/>
 * A PortletURL is created through the <CODE>RenderResponse</CODE>.
 * Parameters, a portlet mode, a window state and a security level
 * can be added to <CODE>PortletURL</CODE> objects. The PortletURL
 * must be converted to a String in order to embed it into
 * the markup generated by the portlet.
 * <P>
 * There are two types of PortletURLs:
 * <ul>
 * <li>Action URLs, they are created with <CODE>RenderResponse.createActionURL</CODE>, and
 * trigger an action request followed by a render request.
 * <li>Render URLs, they are created with <CODE>RenderResponse.createRenderURL</CODE>, and
 * trigger a render request.
 * </ul>
 * <p/>
 * The string reprensentation of a PortletURL does not need to be a valid
 * URL at the time the portlet is generating its content. It may contain
 * special tokens that will be converted to a valid URL, by the portal,
 * before the content is returned to the client.
 */
public class PortletURLImpl implements PortletURL {

    private HttpServletResponse res = null;
    private HttpServletRequest req = null;
    private boolean isSecure = false;
    private Map store = new HashMap();
    private boolean redirect = true;
    private String contextPath = null;
    private String servletPath = null;
    private PortalContext context = null;

    private String action = null;
    private String cid = null;
    private PortletMode mode = null;
    private WindowState state = null;

    private boolean isRender = false;

    private PortletURLImpl() {
        this.store = new HashMap();
    }

    /**
     * Constructs a PortletURL from a servlet request and response
     *
     * @param req the servlet request
     * @param res the servlet response
     */
    public PortletURLImpl(HttpServletRequest req, HttpServletResponse res, PortalContext context, boolean isRender) {
        this();
        this.res = res;
        this.req = req;
        this.context = context;
        this.contextPath = (String)req.getAttribute(SportletProperties.CONTEXT_PATH); // contextPath;
        this.servletPath = (String)req.getAttribute(SportletProperties.SERVLET_PATH); 
        this.isSecure = req.isSecure();
        this.isRender = isRender;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setComponentID(String cid) {
        this.cid = cid;
    }

    /**
     * Indicates the window state the portlet should be in, if this
     * portlet URL triggers a request.
     * <p/>
     * A URL can not have more than one window state attached to it.
     * If more than one window state is set only the last one set
     * is attached to the URL.
     *
     * @param windowState the portlet window state
     * @throws WindowStateException if the portlet cannot switch to this state,
     *                              because the portal does not support this state, the portlet has not
     *                              declared in its deployment descriptor that it supports this state, or the current
     *                              user is not allowed to switch to this state.
     *                              The <code>PortletRequest.isWindowStateAllowed()</code> method can be used
     *                              to check if the portlet can set a given window state.
     * @see PortletRequest#isWindowStateAllowed
     */
    public void setWindowState(WindowState windowState)
            throws WindowStateException {
        if (windowState == null) throw new IllegalArgumentException("Window state cannot be null");
        boolean isSupported = false;
        Enumeration e = context.getSupportedWindowStates();
        while (e.hasMoreElements()) {
            WindowState supported = (WindowState) e.nextElement();
            if (supported.equals(windowState)) {
                isSupported = true;
                break;
            }
        }
        if (windowState.equals(WindowState.NORMAL)) windowState = new WindowState(PortletWindow.State.RESIZING.toString());
        if (isSupported) {
            state = windowState;
        } else {
            throw new WindowStateException("Illegal window state", windowState);
        }

    }

    /**
     * Indicates the portlet mode the portlet must be in, if this
     * portlet URL triggers a request.
     * <p/>
     * A URL can not have more than one portlet mode attached to it.
     * If more than one portlet mode is set only the last one set
     * is attached to the URL.
     *
     * @param portletMode the portlet mode
     * @throws PortletModeException if the portlet cannot switch to this mode,
     *                              because the portal does not support this mode, the portlet has not
     *                              declared in its deployment descriptor that it supports this mode for the current markup,
     *                              or the current user is not allowed to switch to this mode.
     *                              The <code>PortletRequest.isPortletModeAllowed()</code> method can be used
     *                              to check if the portlet can set a given portlet mode.
     * @see PortletRequest#isPortletModeAllowed
     */
    public void setPortletMode(PortletMode portletMode)
            throws PortletModeException {
        if (portletMode == null) throw new IllegalArgumentException("Portlet mode cannot be null");
        List allowedModes = (List) req.getAttribute(SportletProperties.ALLOWED_MODES);
        if (allowedModes.contains(portletMode.toString())) {
            mode = portletMode;
            // hack to handle config mode
            if (mode.toString().equals("config")) mode = new PortletMode("configure");
        } else {
            throw new PortletModeException("Illegal portlet mode", portletMode);
        }
    }

    /**
     * Sets the given String parameter to this URL.
     * <p/>
     * This method replaces all parameters with the given key.
     * <p/>
     * The <code>PortletURL</code> implementation 'x-www-form-urlencoded' encodes
     * all  parameter names and values. Developers should not encode them.
     * <p/>
     * A portlet container may prefix the attribute names internally
     * in order to preserve a unique namespace for the portlet.
     *
     * @param name  the parameter name
     * @param value the parameter value
     * @throws IllegalArgumentException if name or value are <code>null</code>.
     */
    public void setParameter(String name, String value) {
        if ((name == null) || !(name instanceof String)) throw new IllegalArgumentException("name must be a non-null string");
        if (value == null) throw new IllegalArgumentException("value is NULL");
        if (isRender) {
            store.put(SportletProperties.RENDER_PARAM_PREFIX + name, value);
        } else {
            store.put(name, value);
        }
    }

    /**
     * Sets the given String array parameter to this URL.
     * <p/>
     * This method replaces all parameters with the given key.
     * <p/>
     * The <code>PortletURL</code> implementation 'x-www-form-urlencoded' encodes
     * all  parameter names and values. Developers should not encode them.
     * <p/>
     * A portlet container may prefix the attribute names internally
     * in order to preserve a unique namespace for the portlet.
     *
     * @param name   the parameter name
     * @param values the parameter values
     * @throws IllegalArgumentException if name or values are <code>null</code>.
     */
    public void setParameter(String name, String[] values) {
        if ((name == null) || !(name instanceof String)) throw new IllegalArgumentException("name must be a non-null string");
        if (values == null) throw new IllegalArgumentException("values is NULL");
        if (values.length == 0) throw new IllegalArgumentException("values is NULL");

        if (isRender) {
            store.put(SportletProperties.RENDER_PARAM_PREFIX + name, values);
        } else {
            store.put(name, values);
        }
    }

    /**
     * Sets a parameter map for this URL.
     * <p/>
     * All previously set parameters are cleared.
     * <p/>
     * The <code>PortletURL</code> implementation 'x-www-form-urlencoded' encodes
     * all  parameter names and values. Developers should not encode them.
     * <p/>
     * A portlet container may prefix the attribute names internally,
     * in order to preserve a unique namespace for the portlet.
     *
     * @param parameters Map containing parameter names for
     *                   the render phase as
     *                   keys and parameter values as map
     *                   values. The keys in the parameter
     *                   map must be of type String. The values
     *                   in the parameter map must be of type
     *                   String array (<code>String[]</code>).
     * @exception	IllegalArgumentException if parameters is <code>null</code>, if
     * any of the key/values in the Map are <code>null</code>,
     * if any of the keys is not a String, or if any of
     * the values is not a String array.
     */
    public void setParameters(java.util.Map parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters is NULL");
        }
        Iterator it = parameters.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            if (key == null) throw new IllegalArgumentException("a parameters key is NULL");
            if (key instanceof String) {
                Object values = parameters.get(key);
                if (values == null) throw new IllegalArgumentException("a parameters value is NULL");
                if (!(values instanceof String[])) {
                    throw new IllegalArgumentException("a parameters value element must be a string array");
                }
                this.setParameter((String) key, (String[]) values);
            } else {
                throw new IllegalArgumentException("parameter key must be a string");
            }

        }
    }


    /**
     * Indicated the security setting for this URL.
     * <p/>
     * Secure set to <code>true</code> indicates that the portlet requests
     * a secure connection between the client and the portlet window for
     * this URL. Secure set to <code>false</code> indicates that the portlet
     * does not need a secure connection for this URL. If the security is not
     * set for a URL, it will stay the same as the current request.
     *
     * @param secure true, if portlet requests to have a secure connection
     *               between its portlet window and the client; false, if
     *               the portlet does not require a secure connection.
     * @throws PortletSecurityException if the run-time environment does
     *                                  not support the indicated setting
     */
    public void setSecure(boolean secure) throws PortletSecurityException {
        this.isSecure = secure;
    }

    /**
     * Returns the portlet URL string representation to be embedded in the
     * markup.<br>
     * Note that the returned String may not be a valid URL, as it may
     * be rewritten by the portal/portlet-container before returning the
     * markup to the client.
     *
     * @return the encoded URL as a string
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        String port = null;
        if (req.isSecure() || isSecure || (req.getAttribute(SportletProperties.SSL_REQUIRED) != null)) {
            s.append("https://");
            port = GridSphereConfig.getProperty("gridsphere.port.https");
        } else {
            s.append("http://");
            port = GridSphereConfig.getProperty("gridsphere.port.http");
        }
        s.append(req.getServerName());
        s.append(":");
        s.append((!port.equals("")) ? port : String.valueOf(req.getServerPort()));

        String url = contextPath;

        //System.err.println("\n\n\nContext path=" + contextPath);
        String newURL;

        Set set = store.keySet();


        url = contextPath + servletPath + "?";

        if (cid != null) {
            url += "cid=" + cid;
        }

        if (mode != null) {
            url += "&" + SportletProperties.PORTLET_MODE + "=" + mode.toString();
        }

        // if underlying window state is floating then set it in the URI
        if (req.getAttribute(SportletProperties.FLOAT_STATE) != null) state = new WindowState(PortletWindow.State.FLOATING.toString());

        if (state != null) {
            url += "&" + SportletProperties.PORTLET_WINDOW + "=" + state.toString();
        }
        if (action != null) {
            try {
                //System.out.println("Encoding action " + action);
                String enaction = URLEncoder.encode(action, "UTF-8");
                //System.out.println("Encoded action = " + enaction);
                url += "&" + SportletProperties.DEFAULT_PORTLET_ACTION + "=" + enaction;
            } catch (UnsupportedEncodingException e) {
                System.err.println("Unable to support UTF-8 encoding!");
                url += "&" + SportletProperties.DEFAULT_PORTLET_ACTION + "=" + action;
            }
        }

        Iterator it = set.iterator();
        try {
            while (it.hasNext()) {
                url += "&";
                String name = (String) it.next();

                String encname = null;
                encname = URLEncoder.encode(name, "UTF-8");

                Object val = store.get(name);
                if (val instanceof String[]) {
                    String[] vals = (String[]) val;
                    for (int j = 0; j < vals.length - 1; j++) {
                        String encvalue = URLEncoder.encode(vals[j], "UTF-8");
                        url += encname + "=" + encvalue + "&";
                    }
                    String encvalue = URLEncoder.encode(vals[vals.length - 1], "UTF-8");
                    url += encname + "=" + encvalue;
                } else if (val instanceof String) {
                    String aval = (String) store.get(name);
                    if ((aval != null) && (!aval.equals(""))) {
                        String encvalue = URLEncoder.encode(aval, "UTF-8");
                        url += encname + "=" + encvalue;
                    } else {
                        url += encname;
                    }
                }
            }

        } catch (UnsupportedEncodingException e) {
            System.err.println("Unable to support UTF-8 encoding!");
        }
        if (redirect) {
            newURL = res.encodeRedirectURL(url);
        } else {
            newURL = res.encodeURL(url);
        }
        s.append(newURL);
        //System.err.println("created URL= " + s.toString());
        return s.toString();
    }

}
