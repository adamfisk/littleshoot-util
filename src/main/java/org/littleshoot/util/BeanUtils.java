package org.littleshoot.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java bean utilities class.
 */
public final class BeanUtils
    {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(BeanUtils.class);
    
    private BeanUtils()
        {
        // Should not be constructed.
        }

    /**
     * Creates a {@link Map} from all bean data.
     * 
     * @param bean The bean with data to extract.
     * @return The map of bean data.
     */
    public static Map<String, String> mapBean(final Object bean)
        {
        return mapBean(bean, "");
        }

    /**
     * Creates a {@link Map} from all bean data get methods except the one
     * specified to exclude.  Note that "getClass" is always excluded.
     * 
     * @param bean The bean with data to extract.
     * @param exclude A method name to exclude.
     * @return The map of bean data.
     */
    public static Map<String, String> mapBean(final Object bean,
        final String exclude)
        {
        final Set<String> excludes = new HashSet<String>();
        if (!StringUtils.isBlank(exclude))
            {
            excludes.add(exclude);
            }
        return mapBean(bean, excludes);
        }

    /**
     * Creates a {@link Map} from all bean data get methods except the ones
     * specified to exclude.  Note that "getClass" is always excluded.
     * 
     * @param bean The bean with data to extract.
     * @param excludes The method names to exclude.
     * @return The map of bean data.
     */
    public static Map<String, String> mapBean(final Object bean, 
        final Set<String> excludes)
        {
        excludes.add("getClass");
        final Object[] emptyParams = new Object[0];
        final Method[] methods = bean.getClass().getMethods();
        
        final Map<String, String> fields = new HashMap<String, String>();
        for (final Method method : methods) {
            if (method.getParameterTypes().length > 0) {
                continue;
            }
            final String name = method.getName();
            if (name.startsWith("get") && !excludes.contains(name))  {
                LOG.debug("Calling method: {}", name);
                try {
                    final Object returnVal = method.invoke(bean, emptyParams);
                    LOG.debug("Got: {}", returnVal);
                    final String beanValue = String.valueOf(returnVal);
                    final String beanData = 
                        StringUtils.substringAfter(name, "get");
                    fields.put(StringUtils.uncapitalize(beanData), beanValue);
                }
                catch (final IllegalAccessException e) {
                    LOG.debug("Could not access method: "+
                        method.toGenericString(), e);
                }
                catch (final InvocationTargetException e) {
                    LOG.debug("Could not invoke method: "+
                        method.toGenericString(), e);
                }
            }
        }
        return fields;
    }

    /**
     * Populates the specified bean with data from the specified map.
     * 
     * @param bean The object to populate.
     * @param params The map to populate the bean with.
     */
    public static void populateBean(final Object bean, 
        final Map<String, String> params) {
        final Method[] methods = bean.getClass().getMethods();
        for (final Method method : methods) {
            final Class<?>[] types = method.getParameterTypes();
            final int length = types.length;
            if (length != 1) {
                continue;
            }
            final String name = method.getName();
            if (!name.startsWith("set")) {
                continue;
            }
            final String fieldName = name.substring(3).toLowerCase();
            final String str = params.get(fieldName);
            if (str == null) {
                continue;
            }

            final String val = str.trim();
            
            final Class<?> type = types[0];
            
            final Object toAssign;
            if (type.isAssignableFrom(String.class)) {
                toAssign = val;
            }
            else if (type.isAssignableFrom(int.class)) {
                toAssign = Integer.parseInt(val);
            }
            else if (type.isAssignableFrom(boolean.class)) {
                toAssign = toBoolean(val);
            }
            else if (type.isAssignableFrom(Boolean.class)) {
                toAssign = toBoolean(val);
            }
            else if (type.isAssignableFrom(File.class)) {
                toAssign = new File(val);
            }
            else if (type.isAssignableFrom(URI.class)) {
                try {
                    toAssign = new URI(val);
                } catch (final URISyntaxException e) {
                    LOG.error("Error building bean", e);
                    throw new IllegalArgumentException("Error building bean",e);
                }
            }
            else if (type.isAssignableFrom(Integer.class)) {
                toAssign = Integer.parseInt(val);
            }
            else if (type.isAssignableFrom(int.class)) {
                toAssign = Integer.parseInt(val);
            }
            else if (type.isAssignableFrom(Float.class)) {
                toAssign = Float.parseFloat(val);
            }
            else if (type.isAssignableFrom(float.class)) {
                toAssign = Float.parseFloat(val);
            }
            else if (type.isAssignableFrom(Double.class)) {
                toAssign = Double.parseDouble(val);
            }
            else if (type.isAssignableFrom(double.class)) {
                toAssign = Double.parseDouble(val);
            }
            else if (type.isAssignableFrom(Long.class)) {
                toAssign = Long.parseLong(val);
            }
            else if (type.isAssignableFrom(long.class)) {
                toAssign = Long.parseLong(val);
            }
            else {
                final String msg = "Could not handle type: {}"+type.getName()+
                    " key: "+fieldName;
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }
            try {
                method.invoke(bean, toAssign);
            } catch (final IllegalArgumentException e) {
                LOG.error("Could not invoke method", e);
            } catch (final IllegalAccessException e) {
                LOG.error("Could not invoke method", e);
            } catch (final InvocationTargetException e) {
                LOG.error("Could not invoke method", e);
            }
        }
    }

    private static Object toBoolean(final String val) {
        if (val.equalsIgnoreCase("on")) {
            return Boolean.TRUE;
        }
        else if (val.equalsIgnoreCase("off")) {
            return Boolean.FALSE;
        }
        return Boolean.parseBoolean(val);
    }
}
