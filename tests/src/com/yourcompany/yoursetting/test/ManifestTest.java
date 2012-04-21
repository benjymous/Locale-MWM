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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.test.AndroidTestCase;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.twofortyfouram.locale.PackageUtilities;

/**
 * Tests to verify proper entries in the plug-in's Android Manifest.
 */
public final class ManifestTest extends AndroidTestCase
{
    /**
     * Verifies there is a Locale compatible host present
     */
    public void testPreconditions()
    {
        assertNotNull(getHostPackage(getContext().getPackageManager()));
    }

    /**
     * Verifies that the application is enabled
     */
    public void testApplicationEnabled()
    {
        assertTrue(getContext().getApplicationInfo().enabled);
    }

    /**
     * Verifies that a plug-in setting Activity is present, enabled, exported, doesn't require permissions, and has a name and
     * icon.
     */
    public void testPluginActivityPresent()
    {
        final PackageManager packageManager = getContext().getPackageManager();

        final List<ResolveInfo> activities = getPluginActivities(getContext());
        assertFalse(activities.isEmpty());

        for (final ResolveInfo x : activities)
        {
            assertTrue(x.activityInfo.enabled);
            assertTrue(x.activityInfo.exported);

            /*
             * Verify that the plug-in doesn't request permissions not available to the host
             */
            assertFalse(x.activityInfo.permission != null
                    && packageManager.checkPermission(x.activityInfo.permission, getHostPackage(packageManager)) == PackageManager.PERMISSION_DENIED);

            /*
             * Verify that the plug-in has a label attribute in the AndroidManifest
             */
            assertFalse(0 == x.activityInfo.labelRes);

            /*
             * Verify that the plug-in has a icon attribute in the AndroidManifest
             */
            assertFalse(0 == x.activityInfo.icon);
        }
    }

    /**
     * Verifies that the plug-in exports a single {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING} BroadcastReceiver.
     */
    public void testPluginReceiver()
    {
        final PackageManager packageManager = getContext().getPackageManager();
        final String hostPackage = getHostPackage(getContext().getPackageManager());

        final List<ResolveInfo> receivers = getPluginReceivers(getContext());

        assertEquals(1, receivers.size());

        for (final ResolveInfo x : receivers)
        {
            assertTrue(x.activityInfo.enabled);
            assertTrue(x.activityInfo.exported);

            /*
             * Verify that the plug-in doesn't request permissions not available to the host
             */
            assertFalse(x.activityInfo.permission != null && packageManager.checkPermission(x.activityInfo.permission, hostPackage) == PackageManager.PERMISSION_DENIED);
        }
    }

    /**
     * Tests that the plug-in targets at least the same SDK as Locale.
     */
    public void testTargetSdk()
    {
        /*
         * Plug-ins should always keep up to date with the latest target SDK version. This verifies that the plug-in is at least
         * as current as the host application. Locale won't reject plug-ins targeting older SDKs, but Android will run those
         * plug-ins in backwards compatibility mode which may negatively affect the UI.
         */

        try
        {
            final Context localeContext = getContext().createPackageContext(getHostPackage(getContext().getPackageManager()), 0);
            assertTrue(getContext().getApplicationInfo().targetSdkVersion >= localeContext.getApplicationInfo().targetSdkVersion);
        }
        catch (final NameNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies the package is not debuggable
     */
    public void testDebuggable()
    {
        /*
         * Locale won't reject plug-ins that are debuggable, but this flag should be set to false once the app goes to production
         */

        try
        {
            assertTrue((getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), 0).flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0);
        }
        catch (final NameNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Verifies the package is configured to be installed to internal memory
     */
    public void testManifestInstallLocation()
    {
        /*
         * There are two cases: if the installLocation attribute is present in the Android Manifest, then it should be explicitly
         * set to internalOnly. If installLocation is missing, then it is assumed the package is installed to internal memory.
         */

        /*
         * There isn't a public API to check the installLocation of an APK, so this is a hacky implementation to read the value
         * directly from the package's AndroidManifest.
         */

        /*
         * Note that in addition to this test, Locale will also check that a plug-in is actually on internal memory at runtime.
         * This primarily affects custom ROMs that permit moving apps to external memory even if the app specifies internalOnly.
         */

        XmlResourceParser xml = null;
        try
        {
            final int internalOnly = 1;
            xml = getContext().getAssets().openXmlResourceParser("AndroidManifest.xml"); //$NON-NLS-1$
            for (int eventType = xml.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xml.nextToken())
            {
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                    {
                        if (xml.getName().matches("manifest")) //$NON-NLS-1$
                        {
                            for (int x = 0; x < xml.getAttributeCount(); x++)
                            {
                                if (xml.getAttributeName(x).matches("installLocation")) //$NON-NLS-1$
                                {
                                    assertEquals("Install location is incorrect.  Install location must be internalOnly", Integer.parseInt(xml.getAttributeValue(x)), internalOnly); //$NON-NLS-1$
                                    return;
                                }
                            }
                        }

                        break;
                    }
                }
            }
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (final XmlPullParserException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (null != xml)
            {
                xml.close();
            }
        }
    }

    /**
     * Helper to get a Locale-compatible host package.
     * 
     * @param manager PackageManager cannot be null.
     * @return Package name of the Locale-compatible host. May be null if no host is present.
     */
    private static final String getHostPackage(final PackageManager manager)
    {
        return PackageUtilities.getCompatiblePackage(manager, "com.twofortyfouram.locale"); //$NON-NLS-1$
    }

    /**
     * Gets a list of all Activities in {@code context}'s package that export
     * {@link com.twofortyfouram.locale.Intent#ACTION_EDIT_SETTING}.
     * 
     * @param context context. Cannot be null
     */
    private static List<ResolveInfo> getPluginActivities(final Context context)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("context cannot be null"); //$NON-NLS-1$
        }

        final String packageName = context.getPackageName();

        final List<ResolveInfo> result = new LinkedList<ResolveInfo>();

        for (final ResolveInfo x : context.getPackageManager()
                                          .queryIntentActivities(new Intent(com.twofortyfouram.locale.Intent.ACTION_EDIT_SETTING), PackageManager.GET_ACTIVITIES))
        {
            if (packageName.equals(x.activityInfo.packageName))
            {
                result.add(x);
            }
        }

        return result;
    }

    /**
     * Gets a list of all BroadcastReceivers in {@code context}'s package that export
     * {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING}.
     * 
     * @param context context. Cannot be null
     */
    private static List<ResolveInfo> getPluginReceivers(final Context context)
    {
        if (context == null)
        {
            throw new IllegalArgumentException("context cannot be null"); //$NON-NLS-1$
        }

        final String packageName = context.getPackageName();

        final List<ResolveInfo> result = new LinkedList<ResolveInfo>();

        for (final ResolveInfo x : context.getPackageManager()
                                          .queryBroadcastReceivers(new Intent(com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING), PackageManager.GET_INTENT_FILTERS))
        {
            if (packageName.equals(x.activityInfo.packageName))
            {
                result.add(x);
            }

        }

        return result;
    }
}