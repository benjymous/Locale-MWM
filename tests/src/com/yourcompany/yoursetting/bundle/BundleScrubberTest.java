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

package com.yourcompany.yoursetting.bundle;

import android.content.Intent;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Tests the {@link BundleScrubber}.
 */
public final class BundleScrubberTest extends AndroidTestCase
{

    /*
     * Note: From within this unit test, it isn't possible to verify the BundleScrubber's behavior for an extra coming from
     * another process. The problem is that any private serializable put into the Bundle from within the same process is available
     * to the app's class loader since the app owns the class. These unit tests simply verify the behavior of the BundleScrubber
     * with valid inputs.
     */

    /**
     * Verifies that passing null to {@link BundleScrubber#scrub(Intent)} returns false.
     */
    @SmallTest
    public static void testScrubNullIntent()
    {
        assertFalse(BundleScrubber.scrub((Intent) null));
    }

    /**
     * Verifies that passing a valid Intent to {@link BundleScrubber#scrub(Intent)} returns false and does not mutate the Intent.
     */
    public static void testScrubValidIntent()
    {
        {
            /*
             * An empty Intent should be valid and should not be mutated
             */
            final Intent intent = new Intent();

            assertNull(intent.getExtras());
            assertFalse(BundleScrubber.scrub(intent));

            assertNull(intent.getExtras());
        }

        {
            /*
             * A non empty Intent should be valid and should not be mutated
             */
            final Intent intent = new Intent().putExtra("test", "test"); //$NON-NLS-1$ //$NON-NLS-2$
            assertFalse(BundleScrubber.scrub(intent));

            assertEquals(1, intent.getExtras().keySet().size());
            assertEquals("test", intent.getStringExtra("test")); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    /**
     * Verifies that passing null to {@link BundleScrubber#scrub(Bundle)} returns false.
     */
    @SmallTest
    public static void testScrubNullBundle()
    {
        assertFalse(BundleScrubber.scrub((Bundle) null));
    }

    /**
     * Verifies that passing a valid Bundle to {@link BundleScrubber#scrub(Bundle)} returns false and does not mutate the Bundle.
     */
    @SmallTest
    public static void testScrubValidBundle()
    {
        {
            /*
             * An empty Bundle should be valid and should not be mutated
             */
            final Bundle bundle = new Bundle();

            assertFalse(BundleScrubber.scrub(bundle));

            assertEquals(0, bundle.keySet().size());
        }

        {
            /*
             * A non empty Intent should be valid and should not be mutated
             */
            final Intent intent = new Intent().putExtra("test", "test"); //$NON-NLS-1$ //$NON-NLS-2$
            assertFalse(BundleScrubber.scrub(intent));

            assertEquals(1, intent.getExtras().keySet().size());
            assertEquals("test", intent.getStringExtra("test")); //$NON-NLS-1$//$NON-NLS-2$
        }
    }
}