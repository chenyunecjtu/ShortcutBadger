package me.leolin.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * @author Jason Ling
 */

/**
 * 1. 三方应用采用build.manufacture或者build.brand值来特殊处理角标或者指纹相关功能(build.brand由HUAWEI修改为HONOR, build.manufacture由HUAWEI修改为HONOR)。
 * <p>
 * 2. 三方应用判断launcher名字来做特殊处理，导致角标功能失效。(com.huawei.android.launcher 修改为com.hihonor.android.launcher)
 */
public class HuaweiHomeBadger implements Badger {

    @Override
    public void executeBadge(Context context, Class launcherClass, int badgeCount) throws ShortcutBadgeException {
        String URI_OLD = "content://com.huawei.android.launcher.settings/badge/";
        String URI_NEW = "content://com.hihonor.android.launcher.settings/badge/";
        Uri uri = Uri.parse(URI_NEW);
        String type = context.getContentResolver().getType(uri);
        if (ShortcutBadger.log != null) {
            ShortcutBadger.log.i("HuaweiHomeBadger", "badgeCount=" + badgeCount + " new type:" + type);
        }
        Log.i("HomeBadger", "new type:" + type);
        if (TextUtils.isEmpty(type)) {
            uri = Uri.parse(URI_OLD);
            type = context.getContentResolver().getType(uri);
            Log.i("HomeBadger", "old type:" + type);
            if (ShortcutBadger.log != null) {
                ShortcutBadger.log.i("HuaweiHomeBadger", "badgeCount=" + badgeCount + " old type:" + type);
            }
            if (TextUtils.isEmpty(type)) {
                uri = null;
            }
        }
        try {
            Bundle localBundle = new Bundle();
            localBundle.putString("package", context.getPackageName());
            localBundle.putString("class", launcherClass.getName());
            localBundle.putInt("badgenumber", badgeCount);
            if (uri != null) {
                context.getContentResolver().call(uri, "change_badge", null, localBundle);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (ShortcutBadger.log != null&& e!= null) {
                ShortcutBadger.log.i("HuaweiHomeBadger", "" + e.getMessage());
            }
        }

    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.huawei.android.launcher",
                "com.hihonor.android.launcher" //荣耀手机
        );
    }
}
