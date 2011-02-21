package org.littleshoot.util;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing utility methods for JMX.
 */
public class JmxUtils
    {

    private static final Logger LOG = LoggerFactory.getLogger(JmxUtils.class);
    
    private JmxUtils()
        {
        // Should not be constructed.
        }
    
    /**
     * Returns an {@link ObjectName} for the specified object in standard
     * format, using the package as the domain.
     * 
     * @param <T> The type of class.
     * @param clazz The {@link Class} to create an {@link ObjectName} for.
     * @return The new {@link ObjectName}.
     */
    public static <T> ObjectName getObjectName(final Class<T> clazz)
        {
        final String domain = clazz.getPackage().getName();
        final String className = clazz.getSimpleName();
        final String objectName = domain+":type="+className;
        LOG.debug("Returning object name: {}", objectName);
        try
            {
            return new ObjectName(objectName);
            }
        catch (final MalformedObjectNameException e)
            {
            // This should never, ever happen.
            LOG.error("Invalid ObjectName: "+objectName, e);
            throw new RuntimeException("Could not create ObjectName?", e);
            }
        }
    
    /**
     * Registers the specified object as an MBean with the specified server
     * using standard conventions for the bean name.
     * 
     * @param mbs The {@link MBeanServer} to register with.
     * @param obj The {@link Object} to register as an MBean.
     */
    public static void register(final MBeanServer mbs, final Object obj)
        {
        final ObjectName mBeanName = JmxUtils.getObjectName(obj.getClass());
        try
            {
            mbs.registerMBean(obj, mBeanName);
            }
        catch (final InstanceAlreadyExistsException e)
            {
            LOG.error("Could not start JMX", e);
            }
        catch (final MBeanRegistrationException e)
            {
            LOG.error("Could not register", e);
            }
        catch (final NotCompliantMBeanException e)
            {
            LOG.error("MBean error", e);
            }
        }

    }
