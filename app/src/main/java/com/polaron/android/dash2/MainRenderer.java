package com.polaron.android.dash2;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static java.lang.Math.*;

public class MainRenderer implements GLSurfaceView.Renderer {

    public float[] touchPos = new float[]{0.0f, 0.0f};
    public float[] camPos = new float[]{0.0f, 0.0f, 77.0f};
    public float[] lightPos = new float[]{0.0f, 0.0f, 0.0f};
    PistonGrid pg;
    Camera mainCamera;
    long startTime;
    long currentTime;
    float t;
    float aspect;

    public MainRenderer(float aspect)
    {
        this.aspect = aspect;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES30.glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_LESS);
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glCullFace(GLES30.GL_BACK);
        mainCamera = new Camera(aspect);
        pg = new PistonGrid(mainCamera);
        startTime = System.nanoTime();

        mainCamera.setPosition(new float[]{0.0f, 0.0f, 77.0f});

    }

    public void onDrawFrame(GL10 unused) {

        currentTime = System.nanoTime() - startTime;
        t = ((float)currentTime)/1000000000.0f;

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        float row = t*5.0f;
        float gap = 0.1f;
        float rowWidth = (1.5f + 2.0f*gap);

        camPos = new float[]{0.0f, row*rowWidth, 77.0f};
        lightPos = camPos;
        mainCamera.setPosition(camPos);

        mainCamera.setLightPosition(lightPos);
        pg.touchPos = touchPos;
        pg.camPos = camPos;
        pg.render(t);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }



}
