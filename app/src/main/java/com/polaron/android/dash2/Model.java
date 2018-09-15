package com.polaron.android.dash2;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Model {

    int[] buffers = new int[2];
    int[] vertexArray = new int[1];
    int count;
    FloatBuffer vertexFloatBuffer;
    FloatBuffer normalFloatBuffer;
    float t = 0.0f;
    float dt = 0.01f;

    Model(float[] vertices, float[] normals)
    {
        count = vertices.length;

        GLES30.glGenVertexArrays(1, vertexArray, 0);
        GLES30.glBindVertexArray(vertexArray[0]);
        GLES30.glGenBuffers(2, buffers, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4*vertices.length, null, GLES30.GL_DYNAMIC_DRAW);
        vertexFloatBuffer =
                ( ( ByteBuffer ) GLES30.glMapBufferRange (
                        GLES30.GL_ARRAY_BUFFER, 0, 4*vertices.length,
                        GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT | GLES30.GL_MAP_INVALIDATE_RANGE_BIT)
                ).order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        vertexFloatBuffer.put ( vertices ).position ( 0 );
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0,0 );
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, buffers[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4*normals.length, null, GLES30.GL_DYNAMIC_DRAW);
        normalFloatBuffer =
                ( ( ByteBuffer ) GLES30.glMapBufferRange (
                        GLES30.GL_ARRAY_BUFFER, 0, 4*normals.length,
                        GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT | GLES30.GL_MAP_INVALIDATE_RANGE_BIT)
                ).order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        normalFloatBuffer.put ( normals ).position ( 0 );
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, 0,0 );
        GLES30.glEnableVertexAttribArray(1);

        GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);

    }

    public void render()
    {
        GLES30.glBindVertexArray(vertexArray[0]);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, count/3);
    }

}
