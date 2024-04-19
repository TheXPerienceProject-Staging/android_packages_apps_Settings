/*
 * Copyright (C) 2011-2024 The XPerience Project
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
package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class CLOVersionPreferenceController extends BasePreferenceController {

    private static final String PROPERTY_CLO_VERSION = "ro.qcom.system";
    private static final String PROPERTY_CLO_VENDOR = "ro.qcom.vendor";

    public CLOVersionPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        if (!TextUtils.isEmpty(SystemProperties.get(PROPERTY_CLO_VERSION))&& !TextUtils.isEmpty(SystemProperties.get(PROPERTY_CLO_VENDOR))) return AVAILABLE;
        return CONDITIONALLY_UNAVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        String systemVer = SystemProperties.get(PROPERTY_CLO_VERSION);
        String vendorVer = SystemProperties.get(PROPERTY_CLO_VENDOR);

        if (!systemVer.isEmpty() && !vendorVer.isEmpty())
            return /*mContext.getString(R.string.clo_sys_vend)+"\n"+*/systemVer +"\n" + vendorVer;
        else
            return mContext.getString(R.string.unknown);
    }
}
