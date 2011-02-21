package org.littleshoot.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

/**
 * A base class for tests that test standard exception constructors.
 */
public abstract class ExceptionConstructorTest extends TestCase
    {
    /**
     * Tests the default constructor.
     * 
     * @throws IllegalAccessException If the constructor is inaccessible.
     * @throws InstantiationException If the exception class cannot be
     *                                instantiated.
     */
    public void testDefault ()
            throws IllegalAccessException, InstantiationException
        {
        // Call the constructor to make sure nothing explodes.
        newException ();
        }
    
    /**
     * Tests the constructor that takes a message.
     * 
     * @throws NoSuchMethodException     If the constructor does not exist.
     * @throws IllegalAccessException    If the constructor is inaccessible.
     * @throws InstantiationException    If the exception class cannot be
     *                                   instantiated.
     * @throws InvocationTargetException If the constructor throws an
     *                                   exception.
     */
    public void testMessage ()
            throws NoSuchMethodException,
                   IllegalAccessException,
                   InstantiationException,
                   InvocationTargetException
        {
        final Exception exception;
        
        // Make sure the message passed in the exception are returned by
        // getMessage (..).
        exception = newException ("testMessage");
        
        assertEquals ("testMessage", exception.getMessage ());
        }
    
    /**
     * Tests the constructor that takes a cause.
     * 
     * @throws NoSuchMethodException     If the constructor does not exist.
     * @throws IllegalAccessException    If the constructor is inaccessible.
     * @throws InstantiationException    If the exception class cannot be
     *                                   instantiated.
     * @throws InvocationTargetException If the constructor throws an
     *                                   exception.
     */
    public void testCause ()
            throws NoSuchMethodException,
                   IllegalAccessException,
                   InstantiationException,
                   InvocationTargetException
        {
        final RuntimeException cause;
        final Exception exception;
        
        // Make sure the cause passed in the exception are returned by
        // getCause (..).
        cause = new RuntimeException ("theCause");
        
        exception = newException (cause);
        
        assertEquals (cause, exception.getCause ());
        }
    
    /**
     * Tests the constructor that takes a cause.
     * 
     * @throws NoSuchMethodException     If the constructor does not exist.
     * @throws IllegalAccessException    If the constructor is inaccessible.
     * @throws InstantiationException    If the exception class cannot be
     *                                   instantiated.
     * @throws InvocationTargetException If the constructor throws an
     *                                   exception.
     */
    public void testMessageCause ()
            throws NoSuchMethodException,
                   IllegalAccessException,
                   InstantiationException,
                   InvocationTargetException
        {
        final RuntimeException cause;
        final Exception exception;
        
        cause = new RuntimeException ("theCause");
        
        // Make sure the message and cause passed in the exception are
        // returned by getMessage (..) and getCause (..), respectively.
        exception = newException ("testMessage", cause);

        assertEquals ("testMessage", exception.getMessage ());
        assertEquals (cause, exception.getCause ());
        }
    
    /**
     * Returns the Class of the exception to test.
     * 
     * @return The Class of the exception to test.
     */
    protected abstract Class exceptionClass ();
    
    /**
     * Returns a new exception constructed with the default constructor.
     * 
     * @return A new exception constructed with the default constructor.
     * 
     * @throws IllegalAccessException If the constructor is inaccessible.
     * @throws InstantiationException If the exception class cannot be
     *                                instantiated.
     */
    private Exception newException ()
            throws IllegalAccessException, InstantiationException
        {
        return ((Exception) exceptionClass ().newInstance ());
        }
    
    /**
     * Returns a new exception constructed with the constructor that takes
     * a message.
     * 
     * @param message The message to be used in the exception constructor.
     * 
     * @return A new exception.
     * 
     * @throws NoSuchMethodException     If the constructor does not exist.
     * @throws IllegalAccessException    If the constructor is inaccessible.
     * @throws InstantiationException    If the exception class cannot be
     *                                   instantiated.
     * @throws InvocationTargetException If the constructor throws an
     *                                   exception.
     */
    private Exception newException (final String message)
            throws NoSuchMethodException,
                   IllegalAccessException,
                   InstantiationException,
                   InvocationTargetException
        {
        final Class[] argumentsSpec;
        final Constructor constructor;
        final Object[] arguments;
        
        argumentsSpec = new Class[] {String.class};
        
        constructor = exceptionClass ().getConstructor (argumentsSpec);
        
        arguments = new Object[] {message};
        
        return ((Exception) constructor.newInstance (arguments));
        }
    
    /**
     * Returns a new exception constructed with the constructor that takes
     * a cause.
     * 
     * @param cause The Throwable to be used in the exception constructor.
     * 
     * @return A new exception.
     * 
     * @throws NoSuchMethodException     If the constructor does not exist.
     * @throws IllegalAccessException    If the constructor is inaccessible.
     * @throws InstantiationException    If the exception class cannot be
     *                                   instantiated.
     * @throws InvocationTargetException If the constructor throws an
     *                                   exception.
     */
    private Exception newException (final Throwable cause)
            throws NoSuchMethodException,
                   IllegalAccessException,
                   InstantiationException,
                   InvocationTargetException
        {
        final Class[] argumentsSpec;
        final Constructor constructor;
        final Object[] arguments;
        
        argumentsSpec = new Class[] {Throwable.class};
        
        constructor = exceptionClass ().getConstructor (argumentsSpec);
        
        arguments = new Object[] {cause};
        
        return ((Exception) constructor.newInstance (arguments));
        }
    
    /**
     * Returns a new exception constructed with the constructor that takes
     * a cause.
     * 
     * @param message The message to be used in the exception constructor.
     * @param cause   The Throwable to be used in the exception constructor.
     * 
     * @return A new exception.
     * 
     * @throws NoSuchMethodException     If the constructor does not exist.
     * @throws IllegalAccessException    If the constructor is inaccessible.
     * @throws InstantiationException    If the exception class cannot be
     *                                   instantiated.
     * @throws InvocationTargetException If the constructor throws an
     *                                   exception.
     */
    private Exception newException (final String message,
                                    final Throwable cause)
            throws NoSuchMethodException,
                   IllegalAccessException,
                   InstantiationException,
                   InvocationTargetException
        {
        final Class[] argumentsSpec;
        final Constructor constructor;
        final Object[] arguments;
        
        argumentsSpec = new Class[] {String.class, Throwable.class};
        
        constructor = exceptionClass ().getConstructor (argumentsSpec);
        
        arguments = new Object[] {message, cause};
        
        return ((Exception) constructor.newInstance (arguments));
        }
    }
