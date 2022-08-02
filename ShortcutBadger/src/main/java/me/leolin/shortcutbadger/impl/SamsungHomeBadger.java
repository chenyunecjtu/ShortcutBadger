package me.leolin.shortcutbadger.impl;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import java.util.Arrays;
import java.util.List;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.util.CloseHelper;

/**
 * @author Leo Lin
 */
public class SamsungHomeBadger implements Badger {
    private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
    private static final String[] CONTENT_PROJECTION = new String[]{"_id", "class"};

    private DefaultBadger defaultBadger;

    public SamsungHomeBadger() {
        if (Build.VERSION.SDK_INT >= 21) {
            defaultBadger = new DefaultBadger();
        }
    }

    @Override
    public void executeBadge(Context context, Class launcherClass, int badgeCount) throws ShortcutBadgeException {
        if (defaultBadger != null && defaultBadger.isSupported(context)) {
            defaultBadger.executeBadge(context, launcherClass, badgeCount);
        } else {
            Uri mUri = Uri.parse(CONTENT_URI);
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(mUri, CONTENT_PROJECTION, "package=?", new String[]{context.getPackageName()}, null);
                if (cursor != null) {
                    String entryActivityName = launcherClass.getName();
                    boolean entryActivityExist = false;
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(0);
                        ContentValues contentValues = getContentValues(context, launcherClass, badgeCount, false);
                        contentResolver.update(mUri, contentValues, "_id=?", new String[]{String.valueOf(id)});
                        if (entryActivityName.equals(cursor.getString(cursor.getColumnIndex("class")))) {
                            entryActivityExist = true;
                        }
                    }

                    if (!entryActivityExist) {
                        ContentValues contentValues = getContentValues(context, launcherClass, badgeCount, true);
                        contentResolver.insert(mUri, contentValues);
                    }
                }
            } finally {
                CloseHelper.close(cursor);
            }
        }
    }

    private ContentValues getContentValues(Context context, Class launcherClass, int badgeCount, boolean isInsert) {
        ContentValues contentValues = new ContentValues();
        if (isInsert) {
            contentValues.put("package", context.getPackageName());
            contentValues.put("class", launcherClass.getName());
        }

        contentValues.put("badgecount", badgeCount);

        return contentValues;
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Arrays.asList(
                "com.sec.android.app.launcher",
                "com.sec.android.app.twlauncher"
        );
    }
}
