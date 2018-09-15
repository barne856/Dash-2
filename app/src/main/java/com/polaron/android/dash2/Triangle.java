package com.polaron.android.dash2;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Triangle extends GameObject {
    int colorLocation;
    float[] color;
    Triangle()
    {
        float[] vertices = new float[]
                {-0.5f, -0.5f, 0.0f,
                        0.5f, -0.5f, 0.0f,
                        0.5f, 0.5f, 0.0f};
        float[] normals = new float[]
                {
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f
                };
        Model triangle = new Model(vertices, normals);
        ShaderProgram program = new ShaderProgram("assets/Objects/Triangle/VertexShader.glsl", "assets/Objects/Triangle/FragmentShader.glsl");

        model = triangle;
        shader = program;
        color = new float[]{1.0f, 0.419f, 0.380f, 1.0f};
        colorLocation = GLES30.glGetUniformLocation(shader.getProgramID(), "uColor");
    }

    @Override
    public void render(float t)
    {
        super.render(t);
        update(t);
    }

    private void update(float t)
    {
        float[] vertices = new float[]
                {-0.5f, -0.5f*(float)sin(t), 0.0f,
                        0.5f*(float)sin(t), -0.5f*(float)cos(t), 0.0f,
                        0.5f, 0.5f*(float)cos(t), 0.0f};

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, model.buffers[0]);

        model.vertexFloatBuffer =
                ( (ByteBuffer) GLES30.glMapBufferRange (
                        GLES30.GL_ARRAY_BUFFER, 0, 4*vertices.length,
                        GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT | GLES30.GL_MAP_INVALIDATE_RANGE_BIT)
                ).order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        model.vertexFloatBuffer.put ( vertices ).position ( 0 );

        GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);

        color = new float[]{1.0f, 0.2f, 0.380f, 1.0f};
        GLES30.glUniform4fv(colorLocation, 1, color, 0);
    }
}
