package me.leolin.shortcutbadger.example;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import me.leolin.shortcutbadger.ShortcutBadger;
import me.leolin.shortcutbadger.util.OAIDRom;


public class MainActivity extends Activity {
    private int cid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ShortcutBadger.launcherClass = this.getClass();
        final EditText numInput = findViewById(R.id.numInput);
        Log.i("mainactivity", "v:" + System.getProperty("ro.miui.ui.version.name"));
        Log.d("mainactivity", "v:" + System.getProperty("ro.miui.ui.version.code"));
        Log.d("mainactivity", "v:" + OAIDRom.sysProperty("ro.miui.ui.version.code", ""));
        Log.d("mainactivity", "v:" + OAIDRom.sysProperty("ro.miui.ui.version.name", ""));
        Button button = findViewById(R.id.btnSetBadge);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(numInput.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
                }

                boolean success = ShortcutBadger.applyCount(MainActivity.this.getClass(), MainActivity.this, badgeCount);
                test(badgeCount);
                Toast.makeText(getApplicationContext(), "Set count=" + badgeCount + ", success=" + success, Toast.LENGTH_SHORT).show();
            }
        });

        Button launchNotification = findViewById(R.id.btnSetBadgeByNotification);
        launchNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int badgeCount = 0;
                try {
                    badgeCount = Integer.parseInt(numInput.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Error input", Toast.LENGTH_SHORT).show();
                }

                finish();
                startService(
                        new Intent(MainActivity.this, BadgeIntentService.class).putExtra("badgeCount", badgeCount)
                );
            }
        });

        Button removeBadgeBtn = findViewById(R.id.btnRemoveBadge);
        removeBadgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = ShortcutBadger.removeCount(MainActivity.this, MainActivity.this.getClass());

                Toast.makeText(getApplicationContext(), "success=" + success, Toast.LENGTH_SHORT).show();
            }
        });


        //find the home launcher Package
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        String currentHomePackage = "none";
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // in case of duplicate apps (Xiaomi), calling resolveActivity from one will return null
        if (resolveInfo != null) {
            currentHomePackage = resolveInfo.activityInfo.packageName;
        }

        TextView textViewHomePackage = findViewById(R.id.textViewHomePackage);
        textViewHomePackage.setText("launcher:" + currentHomePackage);
    }


    private void test(int num) {
        String id = "my_channel_01";
        CharSequence name = "channel_ShowBadge";
        String description = "角标显示";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = null;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(id) == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.setShowBadge(true);
                notificationManager.createNotificationChannel(mChannel);
            }
            Notification notification = new NotificationCompat.Builder(this, id)
                    .setContentText("df")
                    .setNumber(num)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .build();
            notificationManager.notify(cid++, notification);
        }


    }
}
