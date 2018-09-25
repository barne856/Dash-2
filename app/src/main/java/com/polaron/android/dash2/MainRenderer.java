package com.polaron.android.dash2;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static java.lang.Math.*;

public class MainRenderer implements GLSurfaceView.Renderer {

    Sphere player;
    Camera mainCamera = new Camera();
    long startTime;
    long currentTime;
    float t;

    public MainRenderer()
    {

    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_LESS);
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glCullFace(GLES30.GL_FRONT);

        // Sphere
        player = new Sphere();
        startTime = System.nanoTime();
    }

    public void onDrawFrame(GL10 unused) {
        currentTime = System.nanoTime() - startTime;
        t = ((float)currentTime)/1000000000.0f;

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        float[] playerPos = new float[]{5.0f*(float)sin(t)*(float)cos(t), 5.0f*(float)sin(t), 0.0f};
        mainCamera.setPosition(new float[]{0.0f, 0.0f, -20.0f});
        player.setPosition(playerPos);
        mainCamera.render(player.getProgramID());
        player.render(t);

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

}
