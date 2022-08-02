package me.leolin.shortcutbadger.impl;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;

/**
 * @author leolin
 */
public class VivoHomeBadger implements Badger {

    @SuppressLint("WrongConstant")
    @Override
    public void executeBadge(Context context, Class launcherClass, int badgeCount) throws ShortcutBadgeException {
        Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", context.getPackageName());
        intent.putExtra("className", launcherClass.getName());
        intent.putExtra("notificationNum", badgeCount);
        if (Build.VERSION.SDK_INT == 26) {
            intent.addFlags(0x01000000);
        }
        try {
            context.sendBroadcast(intent);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList("com.vivo.launcher");
    }
}
