/*
 * Copyright (C) 2017-2019 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.fuelgauge;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settingslib.search.Indexable;
import com.android.settingslib.search.SearchIndexable;

import mx.xperience.framework.preference.SystemSettingSwitchPreference;
import mx.xperience.framework.preference.SystemSettingListPreference;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@SearchIndexable
public class StatusBarBatterySettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener, Indexable {

    public static final String TAG = "StatusBarBatterySettings";

    private static final String BATTERY_STYLE = "status_bar_battery_style";
    private static final String SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String SHOW_BATTERY_PERCENT_CHARGING = "status_bar_show_battery_percent_charging";
    private static final String SHOW_BATTERY_PERCENT_INSIDE = "status_bar_show_battery_percent_inside";

    public static final int BATTERY_STYLE_PORTRAIT = 0;
    public static final int BATTERY_STYLE_CIRCLE = 1;
    public static final int BATTERY_STYLE_DOTTED_CIRCLE = 2;
    public static final int BATTERY_STYLE_FULL_CIRCLE = 3;
    public static final int BATTERY_STYLE_TEXT = 4;
    public static final int BATTERY_STYLE_RLANDSCAPE = 5;
    public static final int BATTERY_STYLE_LANDSCAPE = 6;
    public static final int BATTERY_STYLE_iOS15 = 7;
    public static final int BATTERY_STYLE_iOS16 = 8;
    public static final int BATTERY_STYLE_LANDSCAPE_BUDDY = 9;
    public static final int BATTERY_STYLE_LANDSCAPE_LINE = 10;
    public static final int BATTERY_STYLE_LANDSCAPE_MUSKU = 11;
    public static final int BATTERY_STYLE_LANDSCAPE_PILL = 12;
    public static final int BATTERY_STYLE_LANDSCAPE_SIGNAL = 13;
    public static final int BATTERY_STYLE_RLANDSCAPE_STYLE_A = 14;
    public static final int BATTERY_STYLE_LANDSCAPE_STYLE_A = 15;
    public static final int BATTERY_STYLE_RLANDSCAPE_STYLE_B = 16;
    public static final int BATTERY_STYLE_LANDSCAPE_STYLE_B = 17;
    public static final int BATTERY_STYLE_LANDSCAPE_ORIGAMI = 18;
    public static final int BATTERY_STYLE_LANDSCAPE_MIUI_PILL = 19;
    public static final int BATTERY_STYLE_LANDSCAPE_SIMPLY = 20;
    public static final int BATTERY_STYLE_LANDSCAPE_NENINE = 21;
    public static final int BATTERY_STYLE_LANDSCAPE_COLOROS = 22;
    public static final int BATTERY_STYLE_LANDSCAPE_LOVE = 23;
    public static final int BATTERY_STYLE_LANDSCAPE_STRIP = 24;
    public static final int BATTERY_STYLE_LANDSCAPE_IOS_OUTLINE = 25;
    public static final int BATTERY_STYLE_LANDSCAPE_RULER = 26;
    public static final int BATTERY_STYLE_LANDSCAPE_WINDOWS = 27;

    private SystemSettingListPreference mBatteryStyle;
    private SystemSettingSwitchPreference mBatteryPercent;
    private SystemSettingSwitchPreference mBatteryPercentCharging;
    private SystemSettingSwitchPreference mBatteryPercentInside;

    private int mValidStyle = BATTERY_STYLE_PORTRAIT;

    // Define allowed battery style values
    private static final int[] allowedStyles = {
            BATTERY_STYLE_PORTRAIT,
            BATTERY_STYLE_RLANDSCAPE,
            BATTERY_STYLE_LANDSCAPE,
            BATTERY_STYLE_iOS15,
            BATTERY_STYLE_iOS16,
            BATTERY_STYLE_LANDSCAPE_BUDDY,
            BATTERY_STYLE_LANDSCAPE_LINE,
            BATTERY_STYLE_LANDSCAPE_MUSKU,
            BATTERY_STYLE_LANDSCAPE_PILL,
            BATTERY_STYLE_LANDSCAPE_SIGNAL,
            BATTERY_STYLE_RLANDSCAPE_STYLE_A,
            BATTERY_STYLE_LANDSCAPE_STYLE_A,
            BATTERY_STYLE_RLANDSCAPE_STYLE_B,
            BATTERY_STYLE_LANDSCAPE_STYLE_B,
            BATTERY_STYLE_LANDSCAPE_ORIGAMI,
            BATTERY_STYLE_LANDSCAPE_MIUI_PILL,
            BATTERY_STYLE_LANDSCAPE_SIMPLY,
            BATTERY_STYLE_LANDSCAPE_NENINE,
            BATTERY_STYLE_LANDSCAPE_COLOROS,
            BATTERY_STYLE_LANDSCAPE_LOVE,
            BATTERY_STYLE_LANDSCAPE_STRIP,
            BATTERY_STYLE_LANDSCAPE_IOS_OUTLINE,
            BATTERY_STYLE_LANDSCAPE_RULER,
            BATTERY_STYLE_LANDSCAPE_WINDOWS,
            BATTERY_STYLE_FULL_CIRCLE,
            BATTERY_STYLE_CIRCLE,
            BATTERY_STYLE_DOTTED_CIRCLE,
            BATTERY_STYLE_TEXT
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.statusbar_battery_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

        mBatteryPercent = findPreference(SHOW_BATTERY_PERCENT);
        final boolean percentEnabled = Settings.System.getIntForUser(resolver,
                SHOW_BATTERY_PERCENT, 0, UserHandle.USER_CURRENT) == 1;
        mBatteryPercent.setChecked(percentEnabled);
        mBatteryPercent.setOnPreferenceChangeListener(this);

        mBatteryPercentInside = findPreference(SHOW_BATTERY_PERCENT_INSIDE);
        mBatteryPercentInside.setEnabled(percentEnabled);
        final boolean percentInside = Settings.System.getIntForUser(resolver,
                SHOW_BATTERY_PERCENT_INSIDE, 0, UserHandle.USER_CURRENT) == 1;
        mBatteryPercentInside.setChecked(percentInside);
        mBatteryPercentInside.setOnPreferenceChangeListener(this);

        /* algo iba a ponr aqui pero lo olvide */
        mBatteryStyle = findPreference(BATTERY_STYLE);

        // Get the current battery style value from settings
        int value = Settings.System.getIntForUser(resolver,
            BATTERY_STYLE, BATTERY_STYLE_PORTRAIT, UserHandle.USER_CURRENT);

        // Validate the current value against allowed styles
        boolean validValue = false;
        for (int allowedStyle : allowedStyles) {
            if (value == allowedStyle) {
                validValue = true;
                break;
            }
        }

        // If the value is invalid, set it to the default (BATTERY_STYLE_PORTRAIT)
        if (!validValue) {
            value = BATTERY_STYLE_PORTRAIT;
            Settings.System.putIntForUser(resolver, BATTERY_STYLE, value, UserHandle.USER_CURRENT);
        }

        // Set the battery style preference value and summary
        mBatteryStyle.setValue(Integer.toString(value));
        mBatteryStyle.setSummary(mBatteryStyle.getEntry());
        mBatteryStyle.setOnPreferenceChangeListener(this);

        mBatteryPercentCharging = findPreference(SHOW_BATTERY_PERCENT_CHARGING);
        updatePercentChargingEnablement(value, percentEnabled, percentInside);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBatteryStyle) {
            // Ensure the selected value is within the allowed range
            int value = Integer.valueOf((String) objValue);
            boolean validValue = false;
            for (int allowedStyle : allowedStyles) {
                if (value == allowedStyle) {
                    validValue = true;
                    mValidStyle = value; // Set mValidStyle here after validation
                    break;
                }
            }
            if (validValue) {
                int index = mBatteryStyle.findIndexOfValue((String) objValue);
                mBatteryStyle.setSummary(mBatteryStyle.getEntries()[index]);
                //Log.d(TAG, "Setting valid style: " + mValidStyle); // Add this line for debugging
                Settings.System.putIntForUser(resolver,
                        BATTERY_STYLE, mValidStyle, UserHandle.USER_CURRENT);
                updatePercentEnablement(value != BATTERY_STYLE_TEXT);
                updatePercentChargingEnablement(value, null, null);
            } else {
                // Handle invalid selection here (e.g., toast message)
                // Reset the preference value to the previously valid option
                mBatteryStyle.setValue(String.valueOf(mValidStyle));
            }
            return validValue;
        } else if (preference == mBatteryPercent) {
            boolean enabled = (boolean) objValue;
            Settings.System.putInt(resolver,
                    SHOW_BATTERY_PERCENT, enabled ? 1 : 0);
            mBatteryPercentInside.setEnabled(enabled);
            updatePercentChargingEnablement(null, enabled, null);
            return true;
        } else if (preference == mBatteryPercentInside) {
            boolean enabled = (boolean) objValue;
            Settings.System.putInt(resolver,
                    SHOW_BATTERY_PERCENT_INSIDE, enabled ? 1 : 0);
            // we already know style isn't text and percent is enabled
            mBatteryPercentCharging.setEnabled(enabled);
            return true;
        }
        return false;
    }

    private void updatePercentEnablement(boolean enabled) {
        mBatteryPercent.setEnabled(enabled);
        mBatteryPercentInside.setEnabled(enabled && mBatteryPercent.isChecked());
    }

    private void updatePercentChargingEnablement(Integer value, Boolean isPercentEnabled, Boolean isPercentInsideEnabled) {
        // Handle cases where value might be invalid
        if (value == null || !isValidStyle(value)) {
            value = BATTERY_STYLE_PORTRAIT; // Or use a default valid style
            Log.w(TAG, "Invalid battery style passed to updatePercentChargingEnablement: " + value);
        }

        // Update "show battery percent while charging" preference logic
        boolean enableChargingPercent = (isPercentEnabled != null ? isPercentEnabled : mBatteryPercent.isChecked())
                && (isPercentInsideEnabled != null ? isPercentInsideEnabled : mBatteryPercentInside.isChecked())
                && value != BATTERY_STYLE_TEXT;

        mBatteryPercentCharging.setEnabled(enableChargingPercent);
    }

    private boolean isValidStyle(int value) {
        for (int allowedStyle : allowedStyles) {
            if (value == allowedStyle) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.RAINBOW_UNICORN;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.statusbar_battery_settings;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    return keys;
                }
            };
}
