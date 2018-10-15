package com.polaron.android.dash2;

import android.opengl.GLES30;

public class Camera {
    class UniformLocations
    {
        int CAMERA, LIGHT, VIEW, PROJECTION;
    }
    UniformLocations uLocs = new UniformLocations();

    float[] eye = new float[]{0.0f, 0.0f, 1.0f};
    float[] center = new float[]{0.0f, 0.0f, 0.0f};
    float[] up = new float[]{0.0f, 1.0f, 0.0f};

    //float[] lightPosition = new float[]{-5.0f, 10.0f, -10.0f};
    float[] lightPosition = new float[]{0.0f, 0.0f, 2.0f};

    float[] viewMatrix;
    float[] projectionMatrix;

    Camera(float aspect)
    {
        viewMatrix = vmath.lookat(eye, center, up);
        //projectionMatrix = vmath.orthogonal(1.0f, 1.0f/aspect, 0.0f, 1000.0f);
        projectionMatrix = vmath.perspective(60.0f, aspect, 0.1f, 1000.0f);
    }

    public void setPosition(float[] position)
    {
        eye = position;
        //float[] f = vmath.normalize(vmath.subtractVectors(eye, center));
        //up = vmath.cross(new float[]{-f[1], f[2], f[0]}, f);
        center[0] = position[0];
        center[1] = position[1];
        viewMatrix = vmath.lookat(eye, center, up);
    }

    void render(int programID)
    {
        uLocs.CAMERA = GLES30.glGetUniformLocation(programID, "camera_pos");
        uLocs.LIGHT = GLES30.glGetUniformLocation(programID, "light_pos");
        uLocs.VIEW = GLES30.glGetUniformLocation(programID, "view");
        uLocs.PROJECTION = GLES30.glGetUniformLocation(programID, "projection");

        GLES30.glUniform3fv(uLocs.CAMERA, 1, eye, 0);
        GLES30.glUniform3fv(uLocs.LIGHT, 1, lightPosition, 0);
        GLES30.glUniformMatrix4fv(uLocs.VIEW, 1, false, viewMatrix, 0);
        GLES30.glUniformMatrix4fv(uLocs.PROJECTION, 1, false, projectionMatrix, 0);
    }

    public void setLightPosition(float[] lightPosition)
    {
        this.lightPosition = lightPosition;
    }
}
