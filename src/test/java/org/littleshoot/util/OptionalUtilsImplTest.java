package org.littleshoot.util;

import junit.framework.TestCase;

/**
 * A test for the optional utilities implementation.
 */
public final class OptionalUtilsImplTest extends TestCase
    {
    /**
     * Returns optional utilities to test.
     *
     * @return
     *      Optional utilities to test.
     */
    private static OptionalUtils optionalUtils
            ()
        {
        return (new OptionalUtilsImpl ());
        }

    /**
     * Tests the 'optionalOf' method with a null object.
     */
    public void testOptionalOfWithNull
            ()
        {
        assertEquals (optionalUtils ().optionalOf (null),
                      new NoneImpl<Object> ());
        }

    /**
     * Tests the 'optionalOf' method with a non-null object.
     */
    public void testOptionalOfWithNonNull
            ()
        {
        final Object object = new Object ();

        assertEquals (optionalUtils ().optionalOf (object),
                      new SomeImpl<Object> (object));
        }

    /**
     * Tests the 'transformSome' method with a <code>None</code> object.
     */
    public void testTransformSomeWithNone
            ()
        {
        // It does not matter what this transformer is.  It should not be
        // called.
        final Transformer<Integer,Integer> transformer =
                new Transformer<Integer,Integer> ()
            {
            public Integer transform
                    (final Integer object)
                {
                fail ("Transformer should not even be called");
                return 0;
                }
            };

        // Start with a None original.
        final Optional<Integer> original = new NoneImpl<Integer> ();

        // Perform the transformation.
        final Optional<Integer> result =
                optionalUtils ().transformSome (original, transformer);

        // Make sure that the new result is what we expect.
        final OptionalVisitor<Void,Integer> visitor =
                new OptionalVisitor<Void,Integer> ()
            {
            public Void visitNone
                    (final None<Integer> none)
                {
                // This is what we should end up with.
                return null;
                }

            public Void visitSome
                    (final Some<Integer> some)
                {
                // We started with a None object.  We should end up with a None
                // object.
                fail ("Should be None object");
                return null;
                }
            };

        result.accept (visitor);
        }

    /**
     * Tests the 'transformSome' method with a <code>Some</code> object.
     */
    public void testTransformSomeWithSome
            ()
        {
        // We create a transformer that takes an Integer and transforms into
        // another integer whose value is one less than the original Integer.
        final Transformer<Integer,Integer> transformer =
                new Transformer<Integer,Integer> ()
            {
            public Integer transform
                    (final Integer object)
                {
                return object - 1;
                }
            };

        // Construct the original Some object that we will transform.
        final Optional<Integer> original =
                new SomeImpl<Integer> (new Integer (7));

        // Perform the transformation.
        final Optional<Integer> result =
                optionalUtils ().transformSome (original, transformer);

        // Make sure that the new result is what we expect.
        final OptionalVisitor<Void,Integer> visitor =
                new OptionalVisitor<Void,Integer> ()
            {
            public Void visitNone
                    (final None<Integer> none)
                {
                // We started with a Some.  We should not end up with a None.
                fail ("Should be Some object");
                return null;
                }

            public Void visitSome
                    (final Some<Integer> some)
                {
                // We should get our transformed integer.  We started with 7.
                // We should now have 6.
                final Integer i = some.object ();

                assertEquals (6, i.intValue ());
                return null;
                }
            };

        result.accept (visitor);
        }

    /**
     * Tests the 'isNone' method.
     */
    public void testIsNone
            ()
        {
        final Optional<Object> none = new NoneImpl<Object> ();

        assertTrue (optionalUtils ().isNone (none));

        final Optional<Object> some = new SomeImpl<Object> (new Object ());

        assertFalse (optionalUtils ().isNone (some));
        }

    /**
     * Tests the 'isSome' method.
     */
    public void testIsSome
            ()
        {
        final Optional<Object> none = new NoneImpl<Object> ();

        assertFalse (optionalUtils ().isSome (none));

        final Optional<Object> some = new SomeImpl<Object> (new Object ());

        assertTrue (optionalUtils ().isSome (some));
        }

    /**
     * Tests the 'someObject' method with a <code>None</code> object.
     */
    public void testSomeObjectWithNone
            ()
        {
        final Optional<Object> none = new NoneImpl<Object> ();

        boolean exceptionThrown = false;

        try
            {
            optionalUtils ().someObject (none);
            }
        catch (final IllegalArgumentException illegalArgumentException)
            {
            // We expect that when 'someObject' is called with a None object,
            // an illegal argument exception is thrown.
            exceptionThrown = true;
            }

        assertTrue (exceptionThrown);
        }

    /**
     * Tests the 'someObject' method with a <code>Some</code> object.
     */
    public void testSomeObjectWithSome
            ()
        {
        final Object object = new Object ();
        final Optional<Object> some = new SomeImpl<Object> (object);

        final Object actual = optionalUtils ().someObject (some);

        assertSame (object, actual);
        }
    }
