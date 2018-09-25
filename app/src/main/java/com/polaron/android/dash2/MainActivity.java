package com.polaron.android.dash2;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private GLSurfaceView glSurface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        glSurface = new GLSurfaceView(this);
        glSurface.setEGLContextClientVersion(2);
        glSurface.setRenderer(new MainRenderer());
        setContentView(glSurface);

    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurface.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurface.onPause();
    }

}
