/*
 * Copyright 2011 two forty four a.m. LLC <http://www.twofortyfouram.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.yourcompany.yoursetting.ui;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import com.twofortyfouram.locale.MarketActivity;

/**
 * Tests the {@link MarketActivity}.
 */
public final class MarketActivityTest extends ActivityInstrumentationTestCase2<MarketActivity>
{

    /**
     * Constructor for the test class; required by Android.
     */
    public MarketActivityTest()
    {
        /*
         * Using the deprecated constructor in order to support unit tests on SDK 4
         */
        super("com.yourcompany.yoursetting", MarketActivity.class); //$NON-NLS-1$
    }

    /**
     * A simple test to verify the Activity opens. Primarily, this is a regression test to ensure the MarketActivity entry is
     * correctly in the plug-in's Android Manifest.
     */
    @UiThreadTest
    public void testDisplayActivity()
    {
        // start the Activity. If the Activity isn't properly registered in the Manifest, this will fail.
        getActivity();
    }
}