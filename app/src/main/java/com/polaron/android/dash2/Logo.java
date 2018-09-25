package com.polaron.android.dash2;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.lang.Math.*;
import static com.polaron.android.dash2.vmath.*;

public class Logo extends GameObject {

    int colorLocation;
    int modelLocation;
    int viewLocation;
    int projectionLocation;
    int camera_posLocatoin;
    float[] color;

    Logo() {
        float x = 0.525731112119133606f;
        float z = 0.850650808352039932f;
        float n = 0.0f;

        float[] vertices = new float[]
                {
                        -x, n, z,
                        x, n, z,
                        -x, n, -z,
                        x, n, -z,

                        n, z, x,
                        n, z, -x,
                        n, -z, x,
                        n, -z, -x,

                        z, x, n,
                        -z, x, n,
                        z, -x, n,
                        -z, -x, n
                };

        int[] indices = new int[]
                {
                        0,4,1,0,9,4,9,5,4,4,5,8,4,8,1,
                        8,10,1,8,3,10,5,3,8,5,2,3,2,7,3,
                        7,10,3,7,6,10,7,11,6,11,0,6,0,1,6,
                        6,1,10,9,0,11,9,11,2,9,2,5,7,2,11
                };

        ElementModel logo = new ElementModel(vertices, vertices, indices);
        ShaderProgram program = new ShaderProgram("assets/Objects/Triangle/VertexShader.glsl", "assets/Objects/Triangle/FragmentShader.glsl");

        model = logo;
        shader = program;

        colorLocation = GLES30.glGetUniformLocation(shader.getProgramID(), "uColor");
        modelLocation = GLES30.glGetUniformLocation(shader.getProgramID(), "model");
        viewLocation = GLES30.glGetUniformLocation(shader.getProgramID(), "view");
        projectionLocation = GLES30.glGetUniformLocation(shader.getProgramID(), "projection");
        camera_posLocatoin = GLES30.glGetUniformLocation(shader.getProgramID(), "camera_pos");
    }

    @Override
    public void render(float t)
    {
        super.render(t);
        update(t);
    }

    private void update(float t)
    {
        color = new float[]{1.0f, 0.2f, 0.380f, 1.0f};
        GLES30.glUniform4fv(colorLocation, 1, color, 0);

        float[] translation = translation(0.0f, 0.0f, 0.0f);
        float[] scale = scaleUniform(0.8f);
        float[] modelMatrix = matrixMult(translation, scale);
        GLES30.glUniformMatrix4fv(modelLocation, 1, false, modelMatrix, 0 );

        float[] projection = perspective(60.0f, 9.0f/16.0f, 0.1f, 100.0f);
        GLES30.glUniformMatrix4fv(projectionLocation, 1, false, projection, 0 );

        float[] eye = new float[]{5.0f*(float)sin(t), (float)sin(t), 5.0f*(float)cos(t)};
        float[] center = new float[]{0.0f, 0.0f, 0.0f};
        float[] up = new float[]{0.0f, 1.0f, 0.0f};
        float[] view = lookat(eye, center, up);
        GLES30.glUniformMatrix4fv(viewLocation, 1, false, view, 0 );
        GLES30.glUniform3fv(camera_posLocatoin, 1, eye, 0);

        float x = 0.525731112119133606f;
        float z = -2.0f*(float)cos(t)*(float)sin(t)*0.850650808352039932f;
        float n = 0.0f;

        float[] vertices = new float[]
                {
                        -x, n, z,
                        x, n, z,
                        -x, n, -z,
                        x, n, -z,

                        n, z, x,
                        n, z, -x,
                        n, -z, x,
                        n, -z, -x,

                        z, x, n,
                        -z, x, n,
                        z, -x, n,
                        -z, -x, n
                };

        updateVertexBuffer(vertices);

    }

    private void updateVertexBuffer(float[] vertices)
    {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ((ElementModel)model).elementBuffers[0]);

        model.vertexFloatBuffer =
                ( (ByteBuffer) GLES30.glMapBufferRange (
                        GLES30.GL_ARRAY_BUFFER, 0, 4*vertices.length,
                        GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT | GLES30.GL_MAP_INVALIDATE_RANGE_BIT)
                ).order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        model.vertexFloatBuffer.put ( vertices ).position ( 0 );

        GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ((ElementModel)model).elementBuffers[1]);

        model.vertexFloatBuffer =
                ( (ByteBuffer) GLES30.glMapBufferRange (
                        GLES30.GL_ARRAY_BUFFER, 0, 4*vertices.length,
                        GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT | GLES30.GL_MAP_INVALIDATE_RANGE_BIT)
                ).order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        model.vertexFloatBuffer.put ( vertices ).position ( 0 );

        GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);
    }
}
