package com.example.dekudemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(28, 32, 28, 32);
        root.setBackgroundColor(Color.rgb(5, 17, 12));

        TextView title = new TextView(this);
        title.setText("DEKU DEMO PANEL");
        title.setTextColor(Color.rgb(90, 255, 140));
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView desc = new TextView(this);
        desc.setText("Painel flutuante com efeitos visuais locais.");
        desc.setTextColor(Color.WHITE);
        desc.setTextSize(15);
        desc.setGravity(Gravity.CENTER);
        root.addView(desc);

        Button start = new Button(this);
        start.setText("ATIVAR PAINEL FLUTUANTE");
        root.addView(start);

        Button stop = new Button(this);
        stop.setText("DESATIVAR PAINEL");
        root.addView(stop);

        start.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Intent permission = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
                );
                startActivity(permission);
            } else {
                Intent service = new Intent(this, OverlayService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(service);
                } else {
                    startService(service);
                }
            }
        });

        stop.setOnClickListener(v ->
            stopService(new Intent(this, OverlayService.class))
        );

        setContentView(root);
    }
  }
