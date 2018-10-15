package com.polaron.android.dash2;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static java.lang.Math.*;

public class MainRenderer implements GLSurfaceView.Renderer {

    public float[] touchPos = new float[]{0.0f, 0.0f};
    long startTime;
    long currentTime;
    float t;
    float effectTime = 0.0f;
    int ploom = 0;
    float aspect;

    int[][] path = new int[2][12];
    float[] elevations = new float[12];
    float xOffset = 0.0f;
    float yOffset = 0.0f;

    int[] instances;
    int[] iIndices;

    float[] camPos = new float[2];
    float[] camVel = new float[2];
    Random rand = new Random();

    ShaderProgram program;
    Model piston;

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
        startTime = System.nanoTime();

        program = new ShaderProgram("assets/Shaders/Floor.vs.glsl", "assets/Shaders/Floor.fs.glsl");
        piston = createPistonModel();

        for(int i = 0; i < 12; i++)
        {
            path[0][i] = 32;
            path[1][i] = 21 - i;
        }
        for(int i = 0; i < 12; i++)
        {
            elevations[i] = 1.0f;
        }
        elevations[11] = 0.0f;

        instances = findInstances(path);
        iIndices = findIndices(path);
    }

    public void onDrawFrame(GL10 unused) {

        currentTime = System.nanoTime() - startTime;
        float dt = 0.05f;
        t += dt;
        //t = ((float)currentTime)/1000000000.0f;

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        program.render();

        if(ploom == 1 && t > (float)Math.PI/3.0f)
        {
            t = 0.0f;
        }
        if(ploom == 1)
        {
            effectTime += dt;
        }
        if(effectTime > (float)Math.PI/3.0f)
        {
            effectTime = 0.0f;
            ploom = 0;
        }

        //instances = findInstances(path);
        //iIndices = findIndices(path);
        calcElevations(iIndices[11]);

        instances = findInstances(path);
        iIndices = findIndices(path);
        updateCamPos(instances, iIndices);
        instances = findInstances(path);
        iIndices = findIndices(path);


        float[] eye = new float[]{camPos[0], camPos[1], 33.0f};
        float[] center = new float[]{camPos[0], camPos[1], 0.0f};
        float[] up = new float[]{0.0f, 1.0f, 0.0f};

        float scale = 0.95f;
        float length = 5.0f;

        float[] projection = vmath.perspective(60.0f, aspect, 10.0f, 100.0f);
        float[] view = vmath.lookat(eye, center, up);

        int modelLoc = GLES30.glGetUniformLocation(program.getProgramID(), "model");
        int viewLoc = GLES30.glGetUniformLocation(program.getProgramID(), "view");
        int projectionLoc = GLES30.glGetUniformLocation(program.getProgramID(), "projection");
        int iIndexLoc = GLES30.glGetUniformLocation(program.getProgramID(), "iIndex");
        int light_posLoc = GLES30.glGetUniformLocation(program.getProgramID(), "light_pos");
        int instancesLoc = GLES30.glGetUniformLocation(program.getProgramID(), "instances");
        int iIndicesLoc = GLES30.glGetUniformLocation(program.getProgramID(), "iIndices");
        int elevationsLoc = GLES30.glGetUniformLocation(program.getProgramID(), "elevations");
        int xOffsetLoc = GLES30.glGetUniformLocation(program.getProgramID(), "xOffset");
        int yOffsetLoc = GLES30.glGetUniformLocation(program.getProgramID(), "yOffset");
        int tLoc = GLES30.glGetUniformLocation(program.getProgramID(), "t");
        int camPosLoc = GLES30.glGetUniformLocation(program.getProgramID(), "camPos");
        int ploomLoc = GLES30.glGetUniformLocation(program.getProgramID(), "ploomEffect");

        GLES30.glUniformMatrix4fv(modelLoc, 1, false, vmath.scaleNonUniform(scale, scale, length), 0);
        GLES30.glUniformMatrix4fv(viewLoc, 1, false, view, 0);
        GLES30.glUniformMatrix4fv(projectionLoc, 1, false, projection, 0);
        GLES30.glUniform3fv(light_posLoc, 1, eye, 0);
        GLES30.glUniform1iv(instancesLoc, 12, instances, 0);
        GLES30.glUniform1iv(iIndicesLoc, 12, iIndices, 0);
        GLES30.glUniform1fv(elevationsLoc, 12, elevations, 0);
        GLES30.glUniform1f(xOffsetLoc, xOffset);
        GLES30.glUniform1f(yOffsetLoc, yOffset);
        GLES30.glUniform1f(tLoc, effectTime);
        GLES30.glUniform2fv(camPosLoc, 1, camPos, 0);
        GLES30.glUniform1i(ploomLoc, ploom);

        GLES30.glBindVertexArray(piston.vertexArray[0]);

        GLES30.glUniform1i(iIndexLoc, 0);
        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, piston.count/3, (int)pow(2, 10));
        GLES30.glUniform1i(iIndexLoc, 1);
        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, piston.count/3, (int)pow(2, 10));

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    private float[] findCameraPos(int[] ins, int[] inds)
    {
        float x = 0.0f;
        float y = 0.0f;
        for(int i = 0; i < 12; i++)
        {
            float[] pos = instanceToWorldPos(ins[i], inds[i]);
            x += pos[0];
            y += pos[1];
        }
        x /= 12.0f;
        y /= 12.0f;
        return new float[]{x, y};
    }

    private float[] instanceToWorldPos(int i, int ind)
    {
        float x = 3.0f*((float)(i >> 5) - 16.0f + 0.5f*(float)ind);
        float y = 3.0f*((1.0f/(float)sqrt(3.0f))*(float)(i & 0x1F) - 9.0f + (float)ind*((float)sqrt(3.0f)/6.0f));
        return new float[]{x, y};
    }

    private void updateCamPos(int[] ins, int[] inds)
    {
        float k = 2.0f;
        float c = 1.0f;
        float dt = 0.01f;
        float[] lerpPos = findCameraPos(ins, inds);
        float[] x = new float[2];
        x[0] = lerpPos[0] - camPos[0];
        x[1] = lerpPos[1] - camPos[1];
        float[] force = new float[2];
        force[0] = k*x[0] - c*camVel[0];
        force[1] = k*x[1] - c*camVel[1];
        camVel[0] += force[0]*dt;
        camVel[1] += force[1]*dt;
        camPos[0] += camVel[0]*dt;
        camPos[1] += camVel[1]*dt;
        if(camPos[1] >= (float)sqrt(3.0))
        {
            yOffset -= (float)sqrt(3.0f);
            camPos[1] -= (float)sqrt(3.0f);
            for(int i = 0; i < 12; i++)
            {
                path[1][i] += 1;
            }
        }
        if(camPos[1] <= -(float)sqrt(3.0))
        {
            yOffset += (float)sqrt(3.0f);
            camPos[1] += (float)sqrt(3.0f);
            for(int i = 0; i < 12; i++)
            {
                path[1][i] -= 1;
            }
        }
        if(camPos[0] >= 3.0f)
        {
            xOffset -= 3.0f;
            camPos[0] -= 3.0f;
            for(int i = 0; i < 12; i++)
            {
                path[0][i] -= 2;
            }
        }
        if(camPos[0] <= -3.0f)
        {
            xOffset += 3.0f;
            camPos[0] += 3.0f;
            for(int i = 0; i < 12; i++)
            {
                path[0][i] += 2;
            }
        }
    }

    private int[] pickDirection(int index)
    {
        int x = rand.nextInt(3)-1;
        int y = -1;
        if(x == 1)
        {
            y += 1-index;
        }
        if(x == -1)
        {
            y += 1-index;
        }
        return new int[]{x,y};
    }

    private void calcElevations(int index)
    {
        float dt = 0.05f;
        elevations[11] += dt;
        elevations[0] -= dt;
        if(elevations[11] >= 1.0f)
        {
            updatePath();
            int[] direction = pickDirection(index);
            path[0][11] += direction[0];
            path[1][11] += direction[1];
        }
    }

    private void updatePath()
    {
        for(int i = 0; i < 11; i++)
        {
            path[0][i] = path[0][i+1];
            path[1][i] = path[1][i+1];
            //elevations[i] = 1.0f;
        }
        elevations[0] = 1.0f;
        elevations[11] = 0.0f;
        //camPos[1] -= (float)sqrt(3.0f);
    }

    private int[] findInstances(int[][] p)
    {
        int[] instances = new int[12];
        for(int i = 0; i < 12; i++)
        {
            int x = p[0][i];
            int y = p[1][i];
            if(x % 2 == 0)
            {
                instances[i] = (32*x/2) + (31 - y);
            }
            else
            {
                instances[i] = (32*(x-1)/2) + (31 - y);
            }
        }
        return instances;
    }

    private int[] findIndices(int[][] p)
    {
        int[] iIndices = new int[12];
        for(int i = 0; i < 12; i++)
        {
            int x = p[0][i];
            if(x % 2 == 0)
            {
                iIndices[i] = 0;
            }
            else
            {
                iIndices[i] = 1;
            }
        }
        return  iIndices;
    }


    private Model createPistonModel()
    {
        int sides = 6;
        float angle = 2.0f*(float)Math.PI/(float)sides;

        float[] vertices = new float[12*sides*3];
        float[] normals = new float[12*sides*3];

        for(int i = 0; i < sides; i++)
        {
            vertices[36*i] = 0.0f;
            vertices[36*i+1] = 0.0f;
            vertices[36*i+2] = 1.0f;
            normals[36*i] = 0.0f;
            normals[36*i+1] = 0.0f;
            normals[36*i+2] = 1.0f;

            float a = i*angle;
            vertices[36*i+3] = (float)Math.cos(a);
            vertices[36*i+4] = (float)Math.sin(a);
            vertices[36*i+5] = 1.0f;
            normals[36*i+3] = 0.0f;
            normals[36*i+4] = 0.0f;
            normals[36*i+5] = 1.0f;

            a = (i+1)*angle;
            vertices[36*i+6] = (float)Math.cos(a);
            vertices[36*i+7] = (float)Math.sin(a);
            vertices[36*i+8] = 1.0f;
            normals[36*i+6] = 0.0f;
            normals[36*i+7] = 0.0f;
            normals[36*i+8] = 1.0f;

            //--------------------

            a = (i+1)*angle;
            vertices[36*i+9] = (float)Math.cos(a);
            vertices[36*i+10] = (float)Math.sin(a);
            vertices[36*i+11] = 1.0f;
            normals[36*i+9] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+10] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+11] = 0.0f;

            a = i*angle;
            vertices[36*i+12] = (float)Math.cos(a);
            vertices[36*i+13] = (float)Math.sin(a);
            vertices[36*i+14] = 1.0f;
            normals[36*i+12] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+13] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+14] = 0.0f;

            a = (i+1)*angle;
            vertices[36*i+15] = (float)Math.cos(a);
            vertices[36*i+16] = (float)Math.sin(a);
            vertices[36*i+17] = -1.0f;
            normals[36*i+15] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+16] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+17] = 0.0f;

            //--------------------

            a = i*angle;
            vertices[36*i+18] = (float)Math.cos(a);
            vertices[36*i+19] = (float)Math.sin(a);
            vertices[36*i+20] = 1.0f;
            normals[36*i+18] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+19] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+20] = 0.0f;

            a = i*angle;
            vertices[36*i+21] = (float)Math.cos(a);
            vertices[36*i+22] = (float)Math.sin(a);
            vertices[36*i+23] = -1.0f;
            normals[36*i+21] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+22] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+23] = 0.0f;

            a = (i+1)*angle;
            vertices[36*i+24] = (float)Math.cos(a);
            vertices[36*i+25] = (float)Math.sin(a);
            vertices[36*i+26] = -1.0f;
            normals[36*i+24] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+25] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+26] = 0.0f;

            //--------------------

            vertices[36*i+27] = 0.0f;
            vertices[36*i+28] = 0.0f;
            vertices[36*i+29] = -1.0f;
            normals[36*i+27] = 0.0f;
            normals[36*i+28] = 0.0f;
            normals[36*i+29] = -1.0f;

            a = (i+1)*angle;
            vertices[36*i+30] = (float)Math.cos(a);
            vertices[36*i+31] = (float)Math.sin(a);
            vertices[36*i+32] = -1.0f;
            normals[36*i+30] = 0.0f;
            normals[36*i+31] = 0.0f;
            normals[36*i+32] = -1.0f;

            a = i*angle;
            vertices[36*i+33] = (float)Math.cos(a);
            vertices[36*i+34] = (float)Math.sin(a);
            vertices[36*i+35] = -1.0f;
            normals[36*i+33] = 0.0f;
            normals[36*i+34] = 0.0f;
            normals[36*i+35] = -1.0f;
        }

         return new Model(vertices, normals);

    }

}
