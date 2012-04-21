/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.metawatch.manager.locale.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.twofortyfouram.locale.BreadCrumber;
import org.metawatch.manager.locale.Constants;
import org.metawatch.manager.locale.R;
import org.metawatch.manager.locale.bundle.BundleScrubber;
import org.metawatch.manager.locale.bundle.PluginBundleManager;

/**
 * This is the "Edit" activity for a Locale Plug-in.
 */
public final class EditActivity extends Activity
{

    /**
     * Help URL, used for the {@link org.metawatch.manager.locale.R.id#twofortyfouram_locale_menu_help} menu item.
     */
    // TODO: Place a real help URL here
    private static final String HELP_URL = "http://www.yourcompany.com/yourhelp.html"; //$NON-NLS-1$

    /**
     * Flag boolean that can only be set to true via the "Don't Save"
     * {@link org.metawatch.manager.locale.R.id#twofortyfouram_locale_menu_dontsave} menu item in
     * {@link #onMenuItemSelected(int, MenuItem)}.
     * <p>
     * If true, then this {@code Activity} should return {@link Activity#RESULT_CANCELED} in {@link #finish()}.
     * <p>
     * If false, then this {@code Activity} should generally return {@link Activity#RESULT_OK} with extras
     * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} and {@link com.twofortyfouram.locale.Intent#EXTRA_STRING_BLURB}.
     * <p>
     * There is no need to save/restore this field's state when the {@code Activity} is paused.
     */
    private boolean mIsCancelled = false;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*
         * A hack to prevent a private serializable classloader attack
         */
        BundleScrubber.scrub(getIntent());
        BundleScrubber.scrub(getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE));

        setContentView(R.layout.main);

        if (Build.VERSION.SDK_INT >= 11)
        {
            CharSequence callingApplicationLabel = null;
            try
            {
                callingApplicationLabel = getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(getCallingPackage(), 0));
            }
            catch (final NameNotFoundException e)
            {
                if (Constants.IS_LOGGABLE)
                {
                    Log.e(Constants.LOG_TAG, "Calling package couldn't be found", e); //$NON-NLS-1$
                }
            }
            if (null != callingApplicationLabel)
            {
                setTitle(callingApplicationLabel);
            }
        }
        else
        {
            setTitle(BreadCrumber.generateBreadcrumb(getApplicationContext(), getIntent(), getString(R.string.plugin_name)));
        }
        
        /*
         * Initialise widget icon spinner
         */
        
        Spinner s1 = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.icons, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter);
        s1.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        //showToast("Spinner1: position=" + position + " id=" + id);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        //showToast("Spinner1: unselected");
                    }
                });

        /*
         * if savedInstanceState is null, then then this is a new Activity instance and a check for EXTRA_BUNDLE is needed
         */
        if (null == savedInstanceState)
        {
            final Bundle forwardedBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);

            if (PluginBundleManager.isBundleValid(forwardedBundle))
            {
                ((EditText) findViewById(R.id.text1)).setText(forwardedBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE));
                ((EditText) findViewById(R.id.text2)).setText(forwardedBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_TITLE));
            }
        }
        /*
         * if savedInstanceState isn't null, there is no need to restore any Activity state directly via onSaveInstanceState(), as
         * the EditText object handles that automatically
         */
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish()
    {
        if (mIsCancelled)
        {
            setResult(RESULT_CANCELED);
        }
        else
        {
            final String message = ((EditText) findViewById(R.id.text1)).getText().toString();
            final String title = ((EditText) findViewById(R.id.text2)).getText().toString();

            /*
             * If the message is of 0 length, then there isn't a setting to save.
             */
            if (0 == message.length())
            {
                setResult(RESULT_CANCELED);
            }
            else
            {
                /*
                 * This is the result Intent to Locale
                 */
                final Intent resultIntent = new Intent();

                /*
                 * This extra is the data to ourselves: either for the Activity or the BroadcastReceiver. Note that anything
                 * placed in this Bundle must be available to Locale's class loader. So storing String, int, and other standard
                 * objects will work just fine. However Parcelable objects must also be Serializable. And Serializable objects
                 * must be standard Java objects (e.g. a private subclass to this plug-in cannot be stored in the Bundle, as
                 * Locale's classloader will not recognize it).
                 */
                final Bundle resultBundle = new Bundle();
                resultBundle.putInt(PluginBundleManager.BUNDLE_EXTRA_INT_VERSION_CODE, Constants.getVersionCode(this));
                resultBundle.putString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE, message);
                resultBundle.putString(PluginBundleManager.BUNDLE_EXTRA_STRING_TITLE, title);

                resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);

                /*
                 * This is the blurb concisely describing what your setting's state is. This is simply used for display in the UI.
                 */
                
                StringBuilder builder = new StringBuilder();
                
                if (title.length() > 0 ) {
                	builder.append(title);
                	builder.append(" : ");
                }
                builder.append(message);
                
                if (builder.length() > getResources().getInteger(org.metawatch.manager.locale.R.integer.twofortyfouram_locale_maximum_blurb_length))
                {
                    resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, builder.toString().substring(0, getResources().getInteger(org.metawatch.manager.locale.R.integer.twofortyfouram_locale_maximum_blurb_length)));
                }
                else
                {
                    resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, builder.toString());
                }

                setResult(RESULT_OK, resultIntent);
            }
        }

        super.finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        /*
         * inflate the default menu layout from XML
         */
        getMenuInflater().inflate(org.metawatch.manager.locale.R.menu.twofortyfouram_locale_help_save_dontsave, menu);

        /*
         * Set up the breadcrumbs for the ActionBar
         */
        if (Build.VERSION.SDK_INT >= 11)
        {
            getActionBar().setSubtitle(BreadCrumber.generateBreadcrumb(getApplicationContext(), getIntent(), getString(R.string.plugin_name)));
        }
        /*
         * Dynamically load the home icon from the host package for Ice Cream Sandwich or later. Note that this leaves Honeycomb
         * devices without the host's icon in the ActionBar, but eventually all Honeycomb devices should receive an OTA to Ice
         * Cream Sandwich so this problem will go away.
         */
        if (Build.VERSION.SDK_INT >= 14)
        {
            getActionBar().setDisplayHomeAsUpEnabled(true);

            /*
             * Note: There is a small TOCTOU error here, in that the host could be uninstalled right after launching the plug-in.
             * That would cause getApplicationIcon() to return the default application icon. It won't fail, but it will return an
             * incorrect icon.
             *
             * In practice, the chances that the host will be uninstalled while the plug-in UI is running are very slim.
             */
            try
            {
                getActionBar().setIcon(getPackageManager().getApplicationIcon(getCallingPackage()));
            }
            catch (final NameNotFoundException e)
            {
                if (Constants.IS_LOGGABLE)
                {
                    Log.w(Constants.LOG_TAG, "An error occurred loading the host's icon", e); //$NON-NLS-1$
                }
            }
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item)
    {
        final int id = item.getItemId();

        if (Build.VERSION.SDK_INT >= 11)
        {
            if (id == android.R.id.home)
            {
                finish();
                return true;
            }
        }

        if (id == org.metawatch.manager.locale.R.id.twofortyfouram_locale_menu_help)
        {
            try
            {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(HELP_URL)));
            }
            catch (final Exception e)
            {
                Toast.makeText(getApplicationContext(), org.metawatch.manager.locale.R.string.twofortyfouram_locale_application_not_available, Toast.LENGTH_LONG).show();
            }

            return true;
        }
        else if (id == org.metawatch.manager.locale.R.id.twofortyfouram_locale_menu_dontsave)
        {
            mIsCancelled = true;
            finish();
            return true;
        }
        else if (id == org.metawatch.manager.locale.R.id.twofortyfouram_locale_menu_save)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}