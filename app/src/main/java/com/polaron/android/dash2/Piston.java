package com.polaron.android.dash2;

import android.opengl.GLES30;

public class Piston extends GameObject {
    float[] modelMatrix = vmath.identityMatrix();
    float[] scaleMatrix = vmath.identityMatrix();
    float[] translationMatrix = vmath.identityMatrix();

    Camera camera;

    Piston()
    {

    }

    Piston(Camera camera, ShaderProgram program)
    {
        this.camera = camera;

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

            float a = (-angle/2.0f) + i*angle;
            vertices[36*i+3] = (float)Math.cos(a);
            vertices[36*i+4] = (float)Math.sin(a);
            vertices[36*i+5] = 1.0f;
            normals[36*i+3] = 0.0f;
            normals[36*i+4] = 0.0f;
            normals[36*i+5] = 1.0f;

            a = (-angle/2.0f) + (i+1)*angle;
            vertices[36*i+6] = (float)Math.cos(a);
            vertices[36*i+7] = (float)Math.sin(a);
            vertices[36*i+8] = 1.0f;
            normals[36*i+6] = 0.0f;
            normals[36*i+7] = 0.0f;
            normals[36*i+8] = 1.0f;

            //--------------------

            a = (-angle/2.0f) + (i+1)*angle;
            vertices[36*i+9] = (float)Math.cos(a);
            vertices[36*i+10] = (float)Math.sin(a);
            vertices[36*i+11] = 1.0f;
            normals[36*i+9] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+10] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+11] = 0.0f;

            a = (-angle/2.0f) + i*angle;
            vertices[36*i+12] = (float)Math.cos(a);
            vertices[36*i+13] = (float)Math.sin(a);
            vertices[36*i+14] = 1.0f;
            normals[36*i+12] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+13] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+14] = 0.0f;

            a = (-angle/2.0f) + (i+1)*angle;
            vertices[36*i+15] = (float)Math.cos(a);
            vertices[36*i+16] = (float)Math.sin(a);
            vertices[36*i+17] = -1.0f;
            normals[36*i+15] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+16] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+17] = 0.0f;

            //--------------------

            a = (-angle/2.0f) + i*angle;
            vertices[36*i+18] = (float)Math.cos(a);
            vertices[36*i+19] = (float)Math.sin(a);
            vertices[36*i+20] = 1.0f;
            normals[36*i+18] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+19] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+20] = 0.0f;

            a = (-angle/2.0f) + i*angle;
            vertices[36*i+21] = (float)Math.cos(a);
            vertices[36*i+22] = (float)Math.sin(a);
            vertices[36*i+23] = -1.0f;
            normals[36*i+21] = 1.0f * (float)Math.cos(i*angle);
            normals[36*i+22] = 1.0f * (float)Math.sin(i*angle);
            normals[36*i+23] = 0.0f;

            a = (-angle/2.0f) + (i+1)*angle;
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

            a = (-angle/2.0f) + (i+1)*angle;
            vertices[36*i+30] = (float)Math.cos(a);
            vertices[36*i+31] = (float)Math.sin(a);
            vertices[36*i+32] = -1.0f;
            normals[36*i+30] = 0.0f;
            normals[36*i+31] = 0.0f;
            normals[36*i+32] = -1.0f;

            a = (-angle/2.0f) + i*angle;
            vertices[36*i+33] = (float)Math.cos(a);
            vertices[36*i+34] = (float)Math.sin(a);
            vertices[36*i+35] = -1.0f;
            normals[36*i+33] = 0.0f;
            normals[36*i+34] = 0.0f;
            normals[36*i+35] = -1.0f;
        }

        Model piston = new Model(vertices, normals);

        model = piston;
        shader = program;

        // Model
        uLocs.MODEL = GLES30.glGetUniformLocation(program.getProgramID(), "model");
        GLES30.glUniformMatrix4fv(uLocs.MODEL, 1, false, modelMatrix, 0);
        // Color
        short r = 66;
        short g = 134;
        short b = 244;
        float[] color = new float[]{(float)(r/255), (float)(g/255), (float)(b/255), 1.0f};
        uLocs.COLOR = GLES30.glGetUniformLocation(program.getProgramID(), "uColor");
        GLES30.glUniform4fv(uLocs.COLOR, 1, color, 0);
    }

    @Override
    public void render(float t)
    {
        update(t);
        updateModelMatrix();
        shader.render();
        GLES30.glUniformMatrix4fv(uLocs.MODEL, 1, false, modelMatrix, 0);
        camera.render(shader.getProgramID());
        model.render();
    }

    public void update(float t)
    {

    }

    public void setScaleMatrix(float[] scaleMatrix)
    {
        this.scaleMatrix = scaleMatrix;
    }

    public void setPosition(float[] position)
    {
        translationMatrix = vmath.translation(position[0], position[1], position[2]);
    }

    public void updateModelMatrix()
    {
        modelMatrix = vmath.matrixMult(scaleMatrix, translationMatrix);
    }
}
