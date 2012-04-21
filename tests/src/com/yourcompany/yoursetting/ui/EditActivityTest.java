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

package com.yourcompany.yoursetting.ui;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.text.TextUtils;
import android.widget.TextView;

import java.lang.reflect.Field;

import com.twofortyfouram.locale.BreadCrumber;
import com.yourcompany.yoursetting.Constants;
import com.yourcompany.yoursetting.R;
import com.yourcompany.yoursetting.bundle.PluginBundleManager;
import com.yourcompany.yoursetting.test.BundleTestHelper;

/**
 * Tests the {@link EditActivity}.
 */
public final class EditActivityTest extends ActivityInstrumentationTestCase2<EditActivity>
{
    /**
     * Activity instance being tested. This is initialized in {@link #setUp()}.
     */
    protected EditActivity mActivity;

    /**
     * Context of the target application. This is initialized in {@link #setUp()}.
     */
    protected Context mTargetContext;

    /**
     * Instrumentation for the test. This is initialized in {@link #setUp()}.
     */
    private Instrumentation mInstrumentation;

    /**
     * The message view in the UI. This is initialized in {@link #setUp()}.
     */
    private TextView mMessageView;

    /**
     * Constructor for the test class; required by Android.
     */
    public EditActivityTest()
    {
        super(EditActivity.class);
    }

    /**
     * Setup that executes before every test case
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        mInstrumentation = getInstrumentation();
        mTargetContext = mInstrumentation.getTargetContext();

        /*
         * Perform test case specific initialization. This is required to be set up here because setActivityIntent has no effect
         * inside a method annotated with @UiThreadTest
         */
        if ("testNewSettingCancel".equals(getName())) //$NON-NLS-1$
        {
            setActivityIntent(new Intent(com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING).putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BREADCRUMB, "Locale > Edit Situation")); //$NON-NLS-1$
        }
        else if ("testNewSettingSave".equals(getName())) //$NON-NLS-1$
        {
            setActivityIntent(new Intent(com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING).putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BREADCRUMB, "Locale > Edit Situation")); //$NON-NLS-1$
        }
        else if ("testOldSetting".equals(getName())) //$NON-NLS-1$
        {
            final Bundle bundle = new Bundle();
            bundle.putString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE, "I am a toast message!"); //$NON-NLS-1$
            bundle.putInt(PluginBundleManager.BUNDLE_EXTRA_INT_VERSION_CODE, Constants.getVersionCode(mTargetContext));

            setActivityIntent(new Intent(com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING).putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BREADCRUMB, "Locale > Edit Situation").putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, bundle)); //$NON-NLS-1$
        }
        else if ("testMissingBreadcrumb".equals(getName())) //$NON-NLS-1$
        {
            setActivityIntent(new Intent(com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING));
        }
        else if ("testBadBundle".equals(getName())) //$NON-NLS-1$
        {
            final Bundle bundle = new Bundle();
            bundle.putString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE, null);
            bundle.putInt(PluginBundleManager.BUNDLE_EXTRA_INT_VERSION_CODE, Constants.getVersionCode(mTargetContext));

            setActivityIntent(new Intent(com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING).putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BREADCRUMB, "Locale > Edit Situation").putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, bundle)); //$NON-NLS-1$
        }

        /*
         * This MUST be called after setActivityIntent is called
         */
        mActivity = getActivity();

        mMessageView = (TextView) mActivity.findViewById(android.R.id.text1);
    }

    /**
     * Verifies the Activity class name hasn't been accidentally changed.
     */
    public static void testActivityName()
    {
        /*
         * NOTE: This test is expected to fail initially when you are adapting this example to your own plug-in. Once you've
         * settled on a name for your Activity, go ahead and update this test case.
         *
         * The goal of this test case is to prevent accidental renaming of the Activity. Once a plug-in is published to the
         * Android Market, the Activity shouldn't be renamed because that will break the plug-in for users who had the old version
         * of the plug-in. If you ever find yourself really needing to rename the Activity after the plug-in has been published,
         * take a look at using an activity-alias entry in the Android Manifest.
         */

        assertEquals("com.yourcompany.yoursetting.ui.EditActivity", EditActivity.class.getName()); //$NON-NLS-1$
    }

    /**
     * Tests creation of a new setting, that the UI is initialized to the right state, and that the Activity result is correct if
     * the user doesn't input anything.
     */
    @UiThreadTest
    public void testNewSettingCancel()
    {
        /*
         * Verify the title with breadcrumbs
         */
        if (Build.VERSION.SDK_INT >= 11)
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getActionBar()
                                                                                                                                                          .getSubtitle());
        }
        else
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getTitle());
        }

        /*
         * Verify the initial UI values
         */
        assertEquals(0, mMessageView.getText().length());
        assertEquals(mTargetContext.getString(R.string.message_hint), mMessageView.getHint().toString());

        /*
         * Verify finishing with no text entry is treated as a cancellation
         */
        mActivity.finish();

        assertEquals(Activity.RESULT_CANCELED, getActivityResultCode(mActivity));
    }

    /**
     * Tests creation of a new setting, that the UI is initialized to the right state, and that changes are properly saved
     */
    @UiThreadTest
    public void testNewSettingSave()
    {
        /*
         * Verify the title with breadcrumbs
         */
        if (Build.VERSION.SDK_INT >= 11)
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getActionBar()
                                                                                                                                                          .getSubtitle());
        }
        else
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getTitle());
        }

        /*
         * Verify the initial UI values
         */
        assertEquals(0, mMessageView.getText().length());
        assertEquals(mTargetContext.getString(R.string.message_hint), mMessageView.getHint().toString());

        /*
         * Set a message
         */
        mMessageView.setText("I am a toast message!"); //$NON-NLS-1$

        /*
         * Verify finishing with text entry is saved
         */
        mActivity.finish();

        assertEquals(Activity.RESULT_OK, getActivityResultCode(mActivity));

        final Intent result = getActivityResultData(mActivity);
        assertNotNull(result);

        final Bundle extras = result.getExtras();
        assertNotNull(extras);
        assertEquals(String.format("Extras should only contain %s and %s but actually contain %s", com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, extras.keySet() //$NON-NLS-1$
                                                                                                                                                                                                             .toString()), 2, extras.keySet()
                                                                                                                                                                                                                                    .size());

        assertFalse(TextUtils.isEmpty(extras.getString(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB)));

        final Bundle pluginBundle = extras.getBundle(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        assertNotNull(pluginBundle);

        /*
         * Verify the Bundle can be serialized
         */
        BundleTestHelper.assertSerializable(pluginBundle);

        /*
         * The following are tests specific to this plug-in's bundle
         */
        assertTrue(PluginBundleManager.isBundleValid(pluginBundle));
    }

    /**
     * Tests editing an old condition, that the UI is initialized to the right state, and that the Activity result is correct
     */
    @UiThreadTest
    public void testOldSetting()
    {
        /*
         * Verify the title with breadcrumbs
         */
        if (Build.VERSION.SDK_INT >= 11)
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getActionBar()
                                                                                                                                                          .getSubtitle());
        }
        else
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getTitle());
        }

        /*
         * Verify the initial UI values
         */
        assertEquals("I am a toast message!", mMessageView.getText().toString()); //$NON-NLS-1$

        /*
         * Verify finishing with text entry is saved
         */
        mActivity.finish();

        assertEquals(Activity.RESULT_OK, getActivityResultCode(mActivity));

        final Intent result = getActivityResultData(mActivity);
        assertNotNull(result);

        final Bundle extras = result.getExtras();
        assertNotNull(extras);
        assertEquals(String.format("Extras should only contain %s and %s but actually contain %s", com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, extras.keySet() //$NON-NLS-1$
                                                                                                                                                                                                             .toString()), 2, extras.keySet()
                                                                                                                                                                                                                                    .size());

        assertFalse(TextUtils.isEmpty(extras.getString(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB)));

        final Bundle pluginBundle = extras.getBundle(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        assertNotNull(pluginBundle);

        /*
         * The following are tests specific to this plug-in's bundle
         */
        assertTrue(PluginBundleManager.isBundleValid(pluginBundle));
        assertEquals("I am a toast message!", pluginBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE)); //$NON-NLS-1$
    }

    /**
     * Verifies the Activity properly handles a missing breadcrumb
     */
    @UiThreadTest
    public void testMissingBreadcrumb()
    {
        /*
         * Not much to do here, the work was done in setUp. If the Activity fails to load, then this test case will fail.
         */

        /*
         * Verify the title with breadcrumbs
         */
        if (Build.VERSION.SDK_INT >= 11)
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getActionBar()
                                                                                                                                                          .getSubtitle());
        }
        else
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getTitle());
        }
    }

    /**
     * Verifies the Activity properly handles a bundle with a bad value embedded in it.
     */
    @UiThreadTest
    public void testBadBundle()
    {
        /*
         * Not much to do here. The primary point of this test is to see if the Activity fails to load, which will have already
         * caused the test to fail
         */

        /*
         * Verify the title with breadcrumbs
         */
        if (Build.VERSION.SDK_INT >= 11)
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getActionBar()
                                                                                                                                                          .getSubtitle());
        }
        else
        {
            assertEquals(BreadCrumber.generateBreadcrumb(mTargetContext, mActivity.getIntent(), mTargetContext.getString(R.string.plugin_name)), mActivity.getTitle());
        }

        /*
         * Verify the initial UI values
         */
        assertEquals(0, mMessageView.getText().length());
        assertEquals(mTargetContext.getString(R.string.message_hint), mMessageView.getHint().toString());

        /*
         * Verify finishing with no text entry is treated as a cancellation
         */
        mActivity.finish();

        assertEquals(Activity.RESULT_CANCELED, getActivityResultCode(mActivity));
    }

    /**
     * Helper to get the Activity result code via reflection.
     *
     * @param activity Activity whose result code is to be obtained. Cannot be null.
     * @return Result code of the Activity
     * @throws IllegalArgumentException if {@code activity} is null
     */
    private static int getActivityResultCode(final Activity activity)
    {
        if (null == activity)
        {
            throw new IllegalArgumentException("activity cannot be null"); //$NON-NLS-1$
        }

        /*
         * This is a hack to verify the result code. There is no official way to check this using the Android testing frameworks,
         * so accessing the internals of the Activity object is the only way. This could break on newer versions of Android.
         */

        try
        {
            final Field resultCodeField = Activity.class.getDeclaredField("mResultCode"); //$NON-NLS-1$
            resultCodeField.setAccessible(true);
            return ((Integer) resultCodeField.get(activity)).intValue();
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper to get the Activity result Intent via reflection.
     *
     * @param activity Activity whose result Intent is to be obtained. Cannot be null.
     * @return Result Intent of the Activity
     * @throws IllegalArgumentException if {@code activity} is null
     */
    private static Intent getActivityResultData(final Activity activity)
    {
        if (null == activity)
        {
            throw new IllegalArgumentException("activity cannot be null"); //$NON-NLS-1$
        }

        /*
         * This is a hack to verify the result code. There is no official way to check this using the Android testing frameworks,
         * so accessing the internals of the Activity object is the only way. This could break on newer versions of Android.
         */

        try
        {
            final Field resultIntentField = Activity.class.getDeclaredField("mResultData"); //$NON-NLS-1$
            resultIntentField.setAccessible(true);
            return ((Intent) resultIntentField.get(activity));
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}