package com.example.catresolutionclone;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.widget.*;

public class MainActivity extends Activity {
    private TextView stretchValue, nativeRes, projectedRes, status;
    private SeekBar stretch;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        nativeRes = findViewById(R.id.nativeRes);
        projectedRes = findViewById(R.id.projectedRes);
        stretch = findViewById(R.id.stretch);
        stretchValue = findViewById(R.id.stretchValue);
        status = findViewById(R.id.status);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        nativeRes.setText(dm.widthPixels + " × " + dm.heightPixels + " px");

        updateProjection(50);
        stretch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean f) { updateProjection(p); }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });

        findViewById(R.id.overlay).setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())));
            } else {
                status.setText("✓ Ventana flotante autorizada.");
            }
        });

        findViewById(R.id.activate).setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                status.setText("Activa primero el permiso de ventana flotante.");
                return;
            }
            status.setText("✓ Estiramiento preparado en " + stretchValue.getText() +
                    ". No se modifica la resolución física.");
            Toast.makeText(this, "Estiramiento activado", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.restore).setOnClickListener(v -> {
            stretch.setProgress(0);
            status.setText("✓ Vista restaurada a 1.00x.");
        });
    }

    private void updateProjection(int p) {
        float x = 1.00f + (p / 99f) * 0.99f;
        stretchValue.setText(String.format(java.util.Locale.US, "Estiramiento: %.2fx", x));
        DisplayMetrics dm = getResources().getDisplayMetrics();
        projectedRes.setText(Math.round(dm.widthPixels * x) + " × " + dm.heightPixels + " px");
    }
}
