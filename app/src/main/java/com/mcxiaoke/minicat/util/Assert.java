package com.mcxiaoke.minicat.util;

import java.util.Collection;

/**
 * @author mcxiaoke
 * @version 2.0 2012-2-23 下午12:45:21
 */
public final class Assert {

    /**
     * Assert a boolean expression, throwing
     * <code>IllegalArgumentException</code> if the test result is
     * <code>false</code>.
     * <p/>
     * <pre class="code">
     * Assert.isTrue(i &gt; 0, &quot;The value must be greater than zero&quot;);
     * </pre>
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert a boolean expression, throwing
     * <code>IllegalArgumentException</code> if the test result is
     * <code>false</code>.
     * <p/>
     * <pre class="code">
     * Assert.isTrue(i &gt; 0);
     * </pre>
     *
     * @param expression a boolean expression
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    /**
     * Assert that an object is not <code>null</code> .
     * <p/>
     * <pre class="code">
     * Assert.notNull(clazz, &quot;The class must not be null&quot;);
     * </pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is not <code>null</code> .
     * <p/>
     * <pre class="code">
     * Assert.notNull(clazz);
     * </pre>
     *
     * @param object the object to check
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object) {
        notNull(object,
                "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void notEmpty(Collection<?> e, String message) {
        if (e == null || e.isEmpty()) {
            throw new NullPointerException(message);
        }
    }

    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new NullPointerException(message);
        }
    }

    public static void notEmpty(Collection<?> e) {
        notEmpty(e,
                "[Assertion failed] - this collection must not be null or empty");
    }

    public static void notEmpty(Object[] array) {
        notEmpty(array,
                "[Assertion failed] - this array must not be null or empty");
    }

    /**
     * Assert that the given String is not empty; that is, it must not be
     * <code>null</code> and not the empty String.
     * <p/>
     * <pre class="code">
     * Assert.hasLength(name, &quot;Name must not be empty&quot;);
     * </pre>
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     * @see StringUtils#hasLength
     */
    public static void notEmpty(String text, String message) {
        if (!StringUtils.hasLength(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that the given String is not empty; that is, it must not be
     * <code>null</code> and not the empty String.
     * <p/>
     * <pre class="code">
     * Assert.hasLength(name);
     * </pre>
     *
     * @param text the String to check
     * @see StringUtils#hasLength
     */
    public static void notEmpty(String text) {
        notEmpty(
                text,
                "[Assertion failed] - this String argument must have length; it must not be null or empty");
    }

    /**
     * Assert that an array has no null elements. Note: Does not complain if the
     * array is empty!
     * <p/>
     * <pre class="code">
     * Assert.noNullElements(array, &quot;The array must have non-null elements&quot;);
     * </pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object array contains a <code>null</code> element
     */
    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    /**
     * Assert that an array has no null elements. Note: Does not complain if the
     * array is empty!
     * <p/>
     * <pre class="code">
     * Assert.noNullElements(array);
     * </pre>
     *
     * @param array the array to check
     * @throws IllegalArgumentException if the object array contains a <code>null</code> element
     */
    public static void noNullElements(Object[] array) {
        noNullElements(array,
                "[Assertion failed] - this array must not contain any null elements");
    }

    /**
     * Assert that the provided object is an instance of the provided class.
     * <p/>
     * <pre class="code">
     * Assert.instanceOf(Foo.class, foo);
     * </pre>
     *
     * @param clazz the required class
     * @param obj   the object to check
     * @throws IllegalArgumentException if the object is not an instance of clazz
     * @see Class#isInstance
     */
    public static void isInstanceOf(Class<?> clazz, Object obj) {
        isInstanceOf(clazz, obj, "");
    }

    /**
     * Assert that the provided object is an instance of the provided class.
     * <p/>
     * <pre class="code">
     * Assert.instanceOf(Foo.class, foo);
     * </pre>
     *
     * @param type    the type to check against
     * @param obj     the object to check
     * @param message a message which will be prepended to the message produced by
     *                the function itself, and which may be used to provide context.
     *                It should normally end in a ": " or ". " so that the function
     *                generate message looks ok when prepended to it.
     * @throws IllegalArgumentException if the object is not an instance of clazz
     * @see Class#isInstance
     */
    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(message + "Object of class ["
                    + (obj != null ? obj.getClass().getName() : "null")
                    + "] must be an instance of " + type);
        }
    }

}
