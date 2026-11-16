
package com.example.dekudemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OverlayService extends Service {

    private WindowManager wm;
    private View bubble, panel;
    private TextView effectText;
    private final Handler handler = new Handler();
    private final int green = Color.rgb(70, 255, 130);

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        createNotification();
        createBubble();
    }

    private void createNotification() {
        String channel = "deku_panel";

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel nc = new NotificationChannel(
                channel, "Deku Demo Panel",
                NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class)
                .createNotificationChannel(nc);
        }

        Notification.Builder b = Build.VERSION.SDK_INT >= 26
            ? new Notification.Builder(this, channel)
            : new Notification.Builder(this);

        startForeground(11, b
            .setContentTitle("DEKU DEMO PANEL")
            .setContentText("Painel visual ativado")
            .setSmallIcon(android.R.drawable.star_on)
            .build());
    }

    private GradientDrawable bg(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(22);
        d.setStroke(2, green);
        return d;
    }

    private WindowManager.LayoutParams params(
        int width, int height, int gravity
    ) {
        int type = Build.VERSION.SDK_INT >= 26
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams p =
            new WindowManager.LayoutParams(
                width, height, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            );

        p.gravity = gravity;
        return p;
    }

    private void createBubble() {
        TextView b = new TextView(this);
        b.setText("DEKU");
        b.setTextColor(Color.BLACK);
        b.setTextSize(15);
        b.setTypeface(null, Typeface.BOLD);
        b.setGravity(Gravity.CENTER);
        b.setBackground(bg(green));

        WindowManager.LayoutParams p = params(
            100, 55, Gravity.TOP | Gravity.START
        );
        p.x = 30;
        p.y = 250;

        b.setOnClickListener(v -> togglePanel());

        b.setOnTouchListener(new View.OnTouchListener() {
            int sx, sy;
            float tx, ty;
            boolean moved;

            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    sx = p.x;
                    sy = p.y;
                    tx = e.getRawX();
                    ty = e.getRawY();
                    moved = false;
                    return true;
                }

                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    int dx = (int)(e.getRawX() - tx);
                    int dy = (int)(e.getRawY() - ty);
                    if (Math.abs(dx) > 8 || Math.abs(dy) > 8)
                        moved = true;
                    p.x = sx + dx;
                    p.y = sy + dy;
                    wm.updateViewLayout(b, p);
                    return true;
                }

                if (e.getAction() == MotionEvent.ACTION_UP) {
                    if (!moved) v.performClick();
                    return true;
                }
                return true;
            }
        });

        bubble = b;
        wm.addView(bubble, p);
    }

    private void togglePanel() {
        if (panel == null) createPanel();
        else {
            wm.removeView(panel);
            panel = null;
        }
    }

    private void createPanel() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(18, 18, 18, 18);
        layout.setBackground(bg(Color.rgb(5, 20, 12)));

        TextView title = new TextView(this);
        title.setText("DEKU DEMO PANEL");
        title.setTextColor(green);
        title.setTextSize(20);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        String[] effects = {
            "FULL COWLING", "DANGER SENSE",
            "BLACKWHIP", "ONE FOR ALL",
            "DETROIT SMASH", "FA JIN",
            "GEARSHIFT", "DEKU DANCE"
        };

        for (String name : effects) {
            Button button = new Button(this);
            button.setText(name);
            button.setTextColor(Color.WHITE);
            button.setOnClickListener(v -> showEffect(name));
            layout.addView(button);
        }

        Button close = new Button(this);
        close.setText("FECHAR PAINEL");
        close.setOnClickListener(v -> togglePanel());
        layout.addView(close);

        panel = layout;
        wm.addView(panel, params(
            300, WindowManager.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        ));
    }

    private void showEffect(String name) {
        if (effectText != null) {
            try { wm.removeView(effectText); }
            catch (Exception ignored) {}
        }

        TextView t = new TextView(this);
        t.setText("⚡ " + name + " ⚡");
        t.setTextColor(green);
        t.setTextSize(25);
        t.setTypeface(null, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        t.setBackground(bg(Color.rgb(5, 20, 12)));

        effectText = t;
        wm.addView(t, params(
            WindowManager.LayoutParams.MATCH_PARENT,
            100, Gravity.CENTER
        ));

        handler.postDelayed(() -> {
            if (effectText == t) {
                try { wm.removeView(t); }
                catch (Exception ignored) {}
                effectText = null;
            }
        }, 1800);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        if (panel != null) wm.removeView(panel);
        if (bubble != null) wm.removeView(bubble);
        if (effectText != null) wm.removeView(effectText);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int id) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
