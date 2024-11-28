/*
 * Copyright (c) 2012, 2022, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package de.ehealth.evek.util;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A container object which may or may not contain a non-{@code null} value.
 * If a value is present, {@code isPresent()} returns {@code true}. If no
 * value is present, the object is considered <i>empty</i> and
 * {@code isPresent()} returns {@code false}.
 *
 * <p>Additional methods that depend on the presence or absence of a contained
 * value are provided, such as {@link #orElse(Object) orElse()}
 * (returns a default value if no value is present) and
 * {@link #ifPresent(Consumer) ifPresent()} (performs an
 * action if a value is present).
 *
 * <p>This is a <a href="{@docRoot}/java.base/java/lang/doc-files/ValueBased.html">value-based</a>
 * class; programmers should treat instances that are
 * {@linkplain #equals(Object) equal} as interchangeable and should not
 * use instances for synchronization, or unpredictable behavior may
 * occur. For example, in a future release, synchronization may fail.
 *
 * @apiNote
 * {@code COptional} is primarily intended for use as a method return type where
 * there is a clear need to represent "no result," and where using {@code null}
 * is likely to cause errors. A variable whose type is {@code COptional} should
 * never itself be {@code null}; it should always point to an {@code COptional}
 * instance.
 *
 * @param <T> the type of value
 * @since 1.8
 */
public final class COptional<T> implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1882966017004068313L;

	/**
     * Common instance for {@code empty()}.
     */
    private static final COptional<?> EMPTY = new COptional<>(null);

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;

    /**
     * Returns an empty {@code COptional} instance.  No value is present for this
     * {@code COptional}.
     *
     * @apiNote
     * Though it may be tempting to do so, avoid testing if an object is empty
     * by comparing with {@code ==} or {@code !=} against instances returned by
     * {@code COptional.empty()}.  There is no guarantee that it is a singleton.
     * Instead, use {@link #isEmpty()} or {@link #isPresent()}.
     *
     * @param <T> The type of the non-existent value
     * @return an empty {@code COptional}
     */
    public static<T> COptional<T> empty() {
        @SuppressWarnings("unchecked")
        COptional<T> t = (COptional<T>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the described value.
     *
     * @param value the value to describe; it's the caller's responsibility to
     *        ensure the value is non-{@code null} unless creating the singleton
     *        instance returned by {@code empty()}.
     */
    private COptional(T value) {
        this.value = value;
    }

    /**
     * Returns an {@code COptional} describing the given non-{@code null}
     * value.
     *
     * @param value the value to describe, which must be non-{@code null}
     * @param <T> the type of the value
     * @return an {@code COptional} with the value present
     * @throws NullPointerException if value is {@code null}
     */
    public static <T> COptional<T> of(T value) {
        return new COptional<>(Objects.requireNonNull(value));
    }

    /**
     * Returns an {@code COptional} describing the given value, if
     * non-{@code null}, otherwise returns an empty {@code COptional}.
     *
     * @param value the possibly-{@code null} value to describe
     * @param <T> the type of the value
     * @return an {@code COptional} with a present value if the specified value
     *         is non-{@code null}, otherwise an empty {@code COptional}
     */
    @SuppressWarnings("unchecked")
    public static <T> COptional<T> ofNullable(T value) {
        return value == null ? (COptional<T>) EMPTY
                             : new COptional<>(value);
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @apiNote
     * The preferred alternative to this method is {@link #orElseThrow()}.
     *
     * @return the non-{@code null} value described by this {@code COptional}
     * @throws NoSuchElementException if no value is present
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * If a value is present, returns {@code true}, otherwise {@code false}.
     *
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * If a value is  not present, returns {@code true}, otherwise
     * {@code false}.
     *
     * @return  {@code true} if a value is not present, otherwise {@code false}
     * @since   11
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     *
     * @param action the action to be performed, if a value is present
     * @throws NullPointerException if value is present and the given action is
     *         {@code null}
     */
    public void ifPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the given empty-based action.
     *
     * @param action the action to be performed, if a value is present
     * @param emptyAction the empty-based action to be performed, if no value is
     *        present
     * @throws NullPointerException if a value is present and the given action
     *         is {@code null}, or no value is present and the given empty-based
     *         action is {@code null}.
     * @since 9
     */
    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (value != null) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns an {@code COptional} describing the value, otherwise returns an
     * empty {@code COptional}.
     *
     * @param predicate the predicate to apply to a value, if present
     * @return an {@code COptional} describing the value of this
     *         {@code COptional}, if a value is present and the value matches the
     *         given predicate, otherwise an empty {@code COptional}
     * @throws NullPointerException if the predicate is {@code null}
     */
    public COptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (isEmpty()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }

    /**
     * If a value is present, returns an {@code COptional} describing (as if by
     * {@link #ofNullable}) the result of applying the given mapping function to
     * the value, otherwise returns an empty {@code COptional}.
     *
     * <p>If the mapping function returns a {@code null} result then this method
     * returns an empty {@code COptional}.
     *
     * @apiNote
     * This method supports post-processing on {@code COptional} values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of URIs, selects one that has not
     * yet been processed, and creates a path from that URI, returning
     * an {@code COptional<Path>}:
     *
     * <pre>{@code
     *     COptional<Path> p =
     *         uris.stream().filter(uri -> !isProcessedYet(uri))
     *                       .findFirst()
     *                       .map(Paths::get);
     * }</pre>
     *
     * Here, {@code findFirst} returns an {@code COptional<URI>}, and then
     * {@code map} returns an {@code COptional<Path>} for the desired
     * URI if one exists.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U> The type of the value returned from the mapping function
     * @return an {@code COptional} describing the result of applying a mapping
     *         function to the value of this {@code COptional}, if a value is
     *         present, otherwise an empty {@code COptional}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> COptional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return empty();
        } else {
            return COptional.ofNullable(mapper.apply(value));
        }
    }

    /**
     * If a value is present, returns the result of applying the given
     * {@code COptional}-bearing mapping function to the value, otherwise returns
     * an empty {@code COptional}.
     *
     * <p>This method is similar to {@link #map(Function)}, but the mapping
     * function is one whose result is already an {@code COptional}, and if
     * invoked, {@code flatMap} does not wrap it within an additional
     * {@code COptional}.
     *
     * @param <U> The type of value of the {@code COptional} returned by the
     *            mapping function
     * @param mapper the mapping function to apply to a value, if present
     * @return the result of applying an {@code COptional}-bearing mapping
     *         function to the value of this {@code COptional}, if a value is
     *         present, otherwise an empty {@code COptional}
     * @throws NullPointerException if the mapping function is {@code null} or
     *         returns a {@code null} result
     */
    public <U> COptional<U> flatMap(Function<? super T, ? extends COptional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return empty();
        } else {
            @SuppressWarnings("unchecked")
            COptional<U> r = (COptional<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a value is present, returns an {@code COptional} describing the value,
     * otherwise returns an {@code COptional} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code COptional}
     *        to be returned
     * @return returns an {@code COptional} describing the value of this
     *         {@code COptional}, if a value is present, otherwise an
     *         {@code COptional} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     * @since 9
     */
    public COptional<T> or(Supplier<? extends COptional<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (isPresent()) {
            return this;
        } else {
            @SuppressWarnings("unchecked")
            COptional<T> r = (COptional<T>) supplier.get();
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @apiNote
     * This method can be used to transform a {@code Stream} of COptional
     * elements to a {@code Stream} of present value elements:
     * <pre>{@code
     *     Stream<COptional<T>> os = ..
     *     Stream<T> s = os.flatMap(COptional::stream)
     * }</pre>
     *
     * @return the COptional value as a {@code Stream}
     * @since 9
     */
    public Stream<T> stream() {
        if (isEmpty()) {
            return Stream.empty();
        } else {
            return Stream.of(value);
        }
    }

    /**
     * If a value is present, returns the value, otherwise returns
     * {@code other}.
     *
     * @param other the value to be returned, if no value is present.
     *        May be {@code null}.
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     *
     * @param supplier the supplying function that produces a value to be returned
     * @return the value, if present, otherwise the result produced by the
     *         supplying function
     * @throws NullPointerException if no value is present and the supplying
     *         function is {@code null}
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return value != null ? value : supplier.get();
    }

    /**
     * If a value is present, returns the value, otherwise throws
     * {@code NoSuchElementException}.
     *
     * @return the non-{@code null} value described by this {@code COptional}
     * @throws NoSuchElementException if no value is present
     * @since 10
     */
    public T orElseThrow() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     *
     * @apiNote
     * A method reference to the exception constructor with an empty argument
     * list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier the supplying function that produces an
     *        exception to be thrown
     * @return the value, if present
     * @throws X if no value is present
     * @throws NullPointerException if no value is present and the exception
     *          supplying function is {@code null}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Indicates whether some other object is "equal to" this {@code COptional}.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@code COptional} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object
     *         otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof COptional<?> other
                && Objects.equals(value, other.value);
    }

    /**
     * Returns the hash code of the value, if present, otherwise {@code 0}
     * (zero) if no value is present.
     *
     * @return hash code value of the present value or {@code 0} if no value is
     *         present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a non-empty string representation of this {@code COptional}
     * suitable for debugging.  The exact presentation format is unspecified and
     * may vary between implementations and versions.
     *
     * @implSpec
     * If a value is present the result must include its string representation
     * in the result.  Empty and present {@code COptional}s must be unambiguously
     * differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return value != null
            ? ("COptional[" + value + "]")
            : "COptional.empty";
    }
}
