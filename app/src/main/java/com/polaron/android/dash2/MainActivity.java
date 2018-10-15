package com.polaron.android.dash2;

import android.app.Activity;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.MotionEvent;

public class MainActivity extends Activity {
    private GLSurfaceView glSurface;
    private MainRenderer mRenderer;
    float width = 0.0f;
    float height = 0.0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display disp = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        width = (float)size.x;
        height = (float)size.y;

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        glSurface = new GLSurfaceView(this);
        glSurface.setEGLContextClientVersion(2);
        mRenderer = new MainRenderer(width/height);
        glSurface.setRenderer(mRenderer);
        setContentView(glSurface);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // MotionEvent object holds X-Y values
        if((event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_DOWN)) {
            float x = event.getX();
            float y = event.getY();
            mRenderer.touchPos = screenToWorld(new float[]{x,y});
            mRenderer.ploom = 1;
        }

        return super.onTouchEvent(event);
    }

    private float[] screenToWorld(float[] screen)
    {
        float x = ((2.0f*screen[0]/width) - 1.0f);
        float y = -((2.0f*screen[1]/width) - height/width);
        return new float[]{x,y};
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
