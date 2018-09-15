package com.polaron.android.dash2;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class StartScreenRenderer implements Renderer {

    Triangle triangle;
    long startTime;
    long currentTime;
    float t;

    public StartScreenRenderer()
    {

    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES30.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);

        // Triangle
        triangle = new Triangle();

        startTime = System.currentTimeMillis();
    }

    public void onDrawFrame(GL10 unused) {
        currentTime = System.currentTimeMillis() - startTime;
        t = ((float)currentTime)/1000.0f;

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        triangle.render(t);

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

}
