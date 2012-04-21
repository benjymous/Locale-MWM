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

package com.yourcompany.yoursetting.receiver;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.test.AndroidTestCase;

import com.yourcompany.yoursetting.bundle.PluginBundleManager;

/**
 * Tests the {@link FireReceiver}
 */
public final class ReceiverTest extends AndroidTestCase
{
    /*
     * These test cases perform sanity checks to verify the receiver doesn't crash when receiving unexpected inputs.
     * 
     * These tests are not very extensive and additional testing is required to verify the BroadcastReceiver works correctly. For
     * example, a human would need to manually verify that a Toast message appears when a correct Intent is sent to the receiver.
     * Depending on what your setting implements, you may be able to verify more easily that the setting triggered the desired
     * result via unit tests than this sample setting can.
     */

    /**
     * Tests sending an Intent with no Action
     */
    public void testNoAction()
    {
        BroadcastReceiver fireReceiver = new FireReceiver();

        /*
         * The receiver shouldn't crash if the Intent has no Action
         */
        fireReceiver.onReceive(getContext(), new Intent());
    }

    /**
     * Tests sending an Intent with no EXTRA_BUNDLE
     */
    public void testNoBundle()
    {
        BroadcastReceiver fireReceiver = new FireReceiver();

        /*
         * The receiver shouldn't crash if the Intent has no Bundle
         */
        fireReceiver.onReceive(getContext(), new Intent(com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING));
    }

    /**
     * Tests sending an Intent with null EXTRA_BUNDLE
     */
    public void testNullBundle()
    {
        BroadcastReceiver fireReceiver = new FireReceiver();

        /*
         * The receiver shouldn't crash if the Intent has a null Bundle
         */
        fireReceiver.onReceive(getContext(), new Intent(com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING).putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, (Bundle) null));
    }

    /**
     * Tests sending an Intent with EXTRA_BUNDLE of the wrong type
     */
    public void testWrongBundleType()
    {
        BroadcastReceiver fireReceiver = new FireReceiver();

        /*
         * The receiver shouldn't crash if the Intent has an EXTRA_BUNDLE that isn't actually a Bundle
         */
        fireReceiver.onReceive(getContext(), new Intent(com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING).putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, "test")); //$NON-NLS-1$
    }

    /**
     * Tests sending an Intent with null message
     */
    public void testNullMessage()
    {
        BroadcastReceiver fireReceiver = new FireReceiver();

        Bundle bundle = new Bundle();
        bundle.putString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE, null);

        /*
         * The receiver shouldn't crash if the EXTRA_BUNDLE is incorrect
         */
        fireReceiver.onReceive(getContext(), new Intent(com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING).putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, bundle));
    }

    /**
     * Tests sending a normal setting Intent that should succeed.
     */
    public void testNormal()
    {
        BroadcastReceiver fireReceiver = new FireReceiver();

        Bundle bundle = new Bundle();
        bundle.putString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE, "Test message"); //$NON-NLS-1$

        fireReceiver.onReceive(getContext(), new Intent(com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING).putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, bundle));
    }
}