package org.esigate.api;

import java.io.InputStream;

public interface HttpSession {
    /**
     * Returns a string containing the unique identifier assigned to this
     * session. The identifier is assigned by the servlet container and is
     * implementation dependent.
     *
     * @return a string specifying the identifier assigned to this session
     *
     * @exception IllegalStateException if this method is called on an
     * invalidated session
     */
    public String getId();

    /**
     * Returns the object bound with the specified name in this session, or
     * <code>null</code> if no object is bound under the name.
     *
     * @param name a string specifying the name of the object
     *
     * @return the object with the specified name
     *
     * @exception IllegalStateException if this method is called on an
     * invalidated session
     */
    public Object getAttribute(String name);

    /**
     * Binds an object to this session, using the name specified. If an object
     * of the same name is already bound to the session, the object is
     * replaced.
     *
     * <p>After this method executes, and if the new object implements
     * <code>HttpSessionBindingListener</code>, the container calls
     * <code>HttpSessionBindingListener.valueBound</code>. The container then
     * notifies any <code>HttpSessionAttributeListener</code>s in the web
     * application.
     *
     * <p>If an object was already bound to this session of this name
     * that implements <code>HttpSessionBindingListener</code>, its
     * <code>HttpSessionBindingListener.valueUnbound</code> method is called.
     *
     * <p>If the value passed in is null, this has the same effect as calling
     * <code>removeAttribute()<code>.
     *
     * @param name the name to which the object is bound; cannot be null
     *
     * @param value the object to be bound
     *
     * @exception IllegalStateException if this method is called on an
     * invalidated session
     */
    public void setAttribute(String name, Object value);
    
    /**
     * Returns the resource located at the named path as an InputStream object.
     *
     * The data in the InputStream can be of any type or length. The path must be specified according to the rules given in getResource.
     * This method returns null if no resource exists at the specified path.

     * Meta-information such as content length and content type that is available via getResource method is lost when using this method.
     *
     * @param template
	 *            The path to the resource template, relative to the context root
     **/
    public InputStream getResourceTemplate(String template);
}
