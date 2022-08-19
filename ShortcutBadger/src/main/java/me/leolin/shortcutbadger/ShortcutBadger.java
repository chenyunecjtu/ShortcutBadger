package me.leolin.shortcutbadger;

import android.app.Notification;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import me.leolin.shortcutbadger.impl.DefaultBadger;
import me.leolin.shortcutbadger.impl.HuaweiHomeBadger;
import me.leolin.shortcutbadger.impl.OPPOHomeBader;
import me.leolin.shortcutbadger.impl.SamsungHomeBadger;
import me.leolin.shortcutbadger.impl.VivoHomeBadger;
import me.leolin.shortcutbadger.impl.XiaomiHomeBadger;
import me.leolin.shortcutbadger.impl.ZukHomeBadger;
import me.leolin.shortcutbadger.util.ILog;
import me.leolin.shortcutbadger.util.OAIDRom;


/**
 * @author Leo Lin
 */
public final class ShortcutBadger {
    private static final String LOG_TAG = "ShortcutBadger";
    public   static Class launcherClass;
    private static Badger sShortcutBadger;
    public static ILog log;


    /**
     * Tries to update the notification count
     *
     * @param context    Caller context
     * @param badgeCount Desired badge count
     * @return true in case of success, false otherwise
     */
    public static boolean applyCount(Class launcherClass, Context context, int badgeCount) {
        try {
            applyCountOrThrow(launcherClass, context, badgeCount);
            return true;
        } catch (ShortcutBadgeException e) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, "Unable to execute badge", e);
            }
            return false;
        }
    }

    /**
     * Tries to update the notification count, throw a {@link ShortcutBadgeException} if it fails
     *
     * @param context    Caller context
     * @param badgeCount Desired badge count
     */
    public static void applyCountOrThrow(Class launcherClass, Context context, int badgeCount) throws ShortcutBadgeException {
        if (sShortcutBadger == null) {
            initBadger(context);
        }

        try {
            sShortcutBadger.executeBadge(context, launcherClass, badgeCount);
        } catch (Exception e) {
            e.printStackTrace();
//            throw new ShortcutBadgeException("Unable to execute badge", e);
        }
    }

    /**
     * Tries to remove the notification count
     *
     * @param context Caller context
     * @return true in case of success, false otherwise
     */
    public static boolean removeCount(Context context,Class launcherClass) {
        return applyCount(launcherClass, context, 0);
    }

    /**
     * Tries to remove the notification count, throw a {@link ShortcutBadgeException} if it fails
     *
     * @param context Caller context
     */
    public static void removeCountOrThrow(Context context, Class launcherClass) throws ShortcutBadgeException {
        applyCountOrThrow(launcherClass, context, 0);
    }


    /**
     * @param context      Caller context
     * @param notification
     * @param badgeCount
     */
    public static void applyNotification(Context context, Notification notification, int badgeCount) {
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            try {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, badgeCount);
            } catch (Exception e) {
                if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                    Log.d(LOG_TAG, "Unable to execute badge", e);
                }
            }
        }
    }

    // Initialize Badger if a launcher is availalble (eg. set as default on the device)
    // Returns true if a launcher is available, in this case, the Badger will be set and sShortcutBadger will be non null.
    private static boolean initBadger(Context context) {

        if (sShortcutBadger == null) {
            if (OAIDRom.isHuawei()) {
                sShortcutBadger = new HuaweiHomeBadger();
            } else if (OAIDRom.isVivo()) {
                sShortcutBadger = new VivoHomeBadger();
            } else if (OAIDRom.isOppo()) {
                sShortcutBadger = new OPPOHomeBader();
            } else if (OAIDRom.isXiaomi()) {
                sShortcutBadger = new XiaomiHomeBadger();
            } else if (OAIDRom.isSamsung()) {
                sShortcutBadger = new SamsungHomeBadger();
            } else if (OAIDRom.isLenovo()) {
                sShortcutBadger = new ZukHomeBadger();
            }else {
                sShortcutBadger = new DefaultBadger();
            }
        }

        return true;
    }

    // Avoid anybody to instantiate this class
    private ShortcutBadger() {

    }
}
