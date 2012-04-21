/*
 * Copyright 2012 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.yourcompany.yoursetting.test;

import android.location.Location;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Helper class to test serialization of Bundles. To use this class, call {@link #assertSerializable(Bundle)}.
 * <p>
 * This class is also a subclass of {@link AndroidTestCase} so that it can verify its own implementation details.
 */
public class BundleTestHelper extends AndroidTestCase
{
    /**
     * Tests that private serializable objects are detected by the bundle checking mechanism.
     */
    @SmallTest
    public static void testPrivateSerializable()
    {
        final Bundle bundle = new Bundle();
        bundle.putSerializable("test", new PrivateSerializable()); //$NON-NLS-1$
        try
        {
            checkBundle(bundle);
            fail();
        }
        catch (final RuntimeException e)
        {
            // expected exception
        }
    }

    /**
     * Tests that parcelable objects are detected by the bundle checking mechanism.
     */
    @SmallTest
    public static void testParcelable()
    {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("test", new Location((String) null)); //$NON-NLS-1$
        try
        {
            checkBundle(bundle);
            fail();
        }
        catch (final RuntimeException e)
        {
            // expected exception
        }
    }

    /**
     * Tests that Parcelable[] objects are detected by the bundle checking mechanism.
     */
    @SmallTest
    public static void testParcelableArray()
    {
        final Bundle bundle = new Bundle();
        bundle.putParcelableArray("test", new Location[] { new Location((String) null) }); //$NON-NLS-1$
        try
        {
            checkBundle(bundle);
            fail();
        }
        catch (final RuntimeException e)
        {
            // expected exception
        }
    }

    /**
     * Tests that Parcelable[] objects are detected by the bundle checking mechanism.
     */
    @SmallTest
    public static void testParcelableArrayList()
    {
        final Bundle bundle = new Bundle();
        ArrayList<Location> list = new ArrayList<Location>();
        list.add(new Location((String) null));
        bundle.putParcelableArrayList("test", list); //$NON-NLS-1$
        try
        {
            checkBundle(bundle);
            fail();
        }
        catch (final RuntimeException e)
        {
            // expected exception
        }
    }

    /**
     * Tests that private empty bundles are OK.
     */
    @SmallTest
    public static void testEmpty()
    {
        final Bundle bundle = new Bundle();

        checkBundle(bundle);
    }

    /**
     * Tests that null keys are OK.
     */
    @SmallTest
    public static void testNullKey()
    {
        final Bundle bundle = new Bundle();
        bundle.putString(null, "test"); //$NON-NLS-1$

        checkBundle(bundle);
    }

    /**
     * Tests that null values are OK.
     */
    @SmallTest
    public static void testNullValue()
    {
        final Bundle bundle = new Bundle();
        bundle.putString("test", null); //$NON-NLS-1$

        checkBundle(bundle);
    }

    /**
     * Tests that ordinary bundles are OK.
     */
    @SmallTest
    public static void testNormalBundle()
    {
        final Bundle bundle = new Bundle();
        bundle.putInt("test1", 5); //$NON-NLS-1$
        bundle.putString("test2", "test"); //$NON-NLS-1$ //$NON-NLS-2$
        bundle.putBoolean("test3", false); //$NON-NLS-1$
        bundle.putStringArray("test4", new String[] { "a", "b", "c" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        checkBundle(bundle);
    }

    /**
     * Asserts whether a Bundle is serializable across processes.
     *
     * @param bundle bundle to check. Cannot be null.
     */
    public static void assertSerializable(final Bundle bundle)
    {
        if (null == bundle)
        {
            throw new IllegalArgumentException("bundle cannot be null"); //$NON-NLS-1$
        }

        try
        {
            checkBundle(bundle);
        }
        catch (final Exception e)
        {
            fail(e.getMessage());
        }
    }

    /**
     * A helper method that attempts to serialize all of the objects inside the Bundle. This helps test that a Bundle only
     * contains objects that can be serialized and doesn't contain Parcelable objects.
     */
    private static void checkBundle(final Bundle bundle)
    {
        ObjectOutputStream objectOut = null; // needs to be closed
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream(); // closing has no effect
        try
        {
            try
            {
                objectOut = new ObjectOutputStream(byteOut);
            }
            catch (final IOException e)
            {
                throw new RuntimeException();
            }

            for (final String key : bundle.keySet())
            {
                final Object value = bundle.get(key);

                if (value instanceof Bundle)
                {
                    // recursively serialize
                    assertSerializable(bundle);
                }
                else if (value instanceof Serializable) // allows objects that implement both parcelable and serializable
                {
                    try
                    {
                        ClassLoader.getSystemClassLoader().loadClass(value.getClass().getName());
                    }
                    catch (final ClassNotFoundException e)
                    {
                        throw new RuntimeException(String.format("Object associated with key %s is not available to the Android ClassLoader", key)); //$NON-NLS-1$
                    }

                    try
                    {
                        objectOut.writeObject(bundle.get(key));
                    }
                    catch (final IOException e)
                    {
                        throw new RuntimeException(String.format("Object associated with key %s couldn't be serialized", key)); //$NON-NLS-1$
                    }

                }
                else if (null == value)
                {
                    // null values are acceptable
                }
                else
                {
                    throw new RuntimeException(
                                               String.format("Key \"%s\"'s value %s isn't Serializable.  Only primitives or objects implementing Serializable can be stored.  Parcelable is not stable for long-term storage.", key, bundle.get(key))); //$NON-NLS-1$                                                                                                                                                                                            .toString()));
                }
            }
        }
        finally
        {
            if (null != objectOut)
            {
                try
                {
                    objectOut.close();
                }
                catch (final IOException e)
                {
                    throw new RuntimeException("Internal failure for test case"); //$NON-NLS-1$
                }
            }
        }
    }

    /**
     * Empty class implementing a private Serializable interface.
     */
    private static final class PrivateSerializable implements Serializable
    {

        /**
         * Implements the Serializable interface
         */
        private static final long serialVersionUID = -7198550596726341062L;

    }
}