package com.polaron.android.dash2;

import android.opengl.GLES30;

public class Path extends GameObject {

    float[] modelMatrix = vmath.identityMatrix();

    Camera camera;

    Path()
    {

    }

    Path(Camera camera, ShaderProgram program)
    {
        this.camera = camera;



        //Model sphere = new Model(normals, normals);

        //model = sphere;
        shader = program;

        // Model
        uLocs.MODEL = GLES30.glGetUniformLocation(program.getProgramID(), "model");
        GLES30.glUniformMatrix4fv(uLocs.MODEL, 1, false, modelMatrix, 0);
        // Color
        float[] color = new float[]{0.905f, 0.419f, 0.313f, 1.0f};
        uLocs.COLOR = GLES30.glGetUniformLocation(program.getProgramID(), "uColor");
        GLES30.glUniform4fv(uLocs.COLOR, 1, color, 0);
    }

    @Override
    public void render(float t)
    {
        camera.render(shader.getProgramID());
        super.render(t);
        update(t);
    }

    private void update(float t)
    {

    }

}
