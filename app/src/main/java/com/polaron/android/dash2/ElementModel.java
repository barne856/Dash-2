package com.polaron.android.dash2;

import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ElementModel extends Model {

    public int[] elementBuffers = new int[3];
    int count;
    IntBuffer indexIntBuffer;

    ElementModel(float[] vertices, float[] normals, int[] indices)
    {
        count = indices.length;

        GLES30.glGenVertexArrays(1, vertexArray, 0);
        GLES30.glBindVertexArray(vertexArray[0]);
        GLES30.glGenBuffers(3, elementBuffers, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, elementBuffers[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, 4*vertices.length, null, GLES30.GL_DYNAMIC_DRAW);
        vertexFloatBuffer =
                ( (ByteBuffer) GLES30.glMapBufferRange (
                        GLES30.GL_ARRAY_BUFFER, 0, 4*vertices.length,
                        GLES30.GL_MAP_WRITE_BIT | GLES30.GL_MAP_INVALIDATE_BUFFER_BIT | GLES30.GL_MAP_INVALIDATE_RANGE_BIT)
                ).order ( ByteOrder.nativeOrder() ).asFloatBuffer();
        vertexFloatBuffer.put ( vertices ).position ( 0 );
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0,0 );
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glUnmapBuffer(GLES30.GL_ARRAY_BUFFER);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, elementBuffers[1]);
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

        indexIntBuffer = ByteBuffer.allocateDirect(4*indices.length).order(ByteOrder.nativeOrder()).asIntBuffer();
        indexIntBuffer.put(indices).position(0);

        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, elementBuffers[2]);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, 4*count, indexIntBuffer, GLES30.GL_STATIC_DRAW);

    }

    @Override
    public void render()
    {
        GLES30.glBindVertexArray(vertexArray[0]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, count, GLES30.GL_UNSIGNED_INT, 0);
    }
}
