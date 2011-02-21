package org.littleshoot.util;

import java.util.Collection;
import java.util.LinkedList;

/**
 * An implementation of the <code>OptionalUtils</code> interface.
 */
public final class OptionalUtilsImpl implements OptionalUtils
    {
    /**
     * {@inheritDoc}
     */
    public <T> void executeOnSome
            (final Optional<T> optional,
             final Closure<T> closure)
        {
        final OptionalVisitor<Void,T> visitor =
                new OptionalVisitor<Void,T> ()
            {
            public Void visitNone
                    (final None<T> none)
                {
                // Do nothing.
                return null;
                }
            
            public Void visitSome
                    (final Some<T> some)
                {
                closure.execute (some.object ());
                return null;
                }
            };
            
        optional.accept (visitor);
        }
    
    /**
     * {@inheritDoc}
     */
    public <T> Collection<T> filterNones
            (final Collection<Optional<T>> c)
        {
        final Collection<T> result = new LinkedList<T> ();
        
        final OptionalVisitor<Void,T> visitor = new OptionalVisitor<Void,T> ()
            {
            public Void visitNone
                    (final None<T> none)
                {
                // Do nothing.
                return null;
                }
            
            public Void visitSome
                    (final Some<T> some)
                {
                result.add (some.object ());
                return null;
                }
            };
        
        for (final Optional<T> current : c)
            {
            current.accept (visitor);
            }
        
        return result;
        }
    
    /**
     * {@inheritDoc}
     */
    public Optional optionalOf
            (final Object object)
        {
        if (object == null)
            {
            return (new NoneImpl ());
            }
        else
            {
            return (new SomeImpl (object));
            }
        }

    /**
     * {@inheritDoc}
     */
    public <T1,T2> Optional<T2> transformSome
            (final Optional<T1> optional,
             final Transformer<T1,T2> transformer)
        {
        final OptionalVisitor<Optional<T2>,T1> visitor =
                new OptionalVisitor<Optional<T2>,T1> ()
            {
            public Optional<T2> visitNone
                    (final None<T1> none)
                {
                return new NoneImpl<T2> ();
                }

            public Optional<T2> visitSome
                    (final Some<T1> some)
                {
                final T1 original = some.object ();
                final T2 transformed = transformer.transform (original);
                
                return new SomeImpl<T2> (transformed);
                }
            };

        return optional.accept (visitor);
        }

    /**
     * {@inheritDoc}
     */
    public <T> boolean isNone
            (final Optional<T> optional)
        {
        final OptionalVisitor<Boolean,T> visitor =
                new OptionalVisitor<Boolean,T> ()
            {
            public Boolean visitNone
                    (final None<T> none)
                {
                return true;
                }

            public Boolean visitSome
                    (final Some<T> some)
                {
                return false;
                }
            };

        return optional.accept(visitor);
        }

    /**
     * {@inheritDoc}
     */
    public boolean isSome
            (final Optional optional)
        {
        return (!isNone (optional));
        }

    /**
     * {@inheritDoc}
     */
    public <T> T someObject
            (final Optional<T> optional) throws IllegalArgumentException
        {
        final ObjectRef objectRef = new ObjectRefImpl ();

        final OptionalVisitor<T,T> visitor = new OptionalVisitor<T,T> ()
            {
            public T visitNone
                    (final None<T> none)
                {
                throw new IllegalArgumentException ("Must be Some");
                }

            public T visitSome
                    (final Some<T> some)
                {
                return some.object ();
                }
            };

        return optional.accept (visitor);
        }
    }
