package com.polaron.android.dash2;


import android.opengl.GLES30;

public class PistonGrid {

    ShaderProgram program;
    Camera camera;
    int gridSize = 51;
    Piston piston;
    float[] touchPos = new float[]{0.0f, 0.0f};
    float[] camPos;

    float[] path = new float[10];

    int touchPosLoc;
    int ambientColorLoc;
    int gridSizeLoc;
    int camPosLoc;
    int pathPos;

    PistonGrid(Camera camera)
    {
        this.camera = camera;
        program = new ShaderProgram("assets/Shaders/BlinnPhongVertexShader.glsl", "assets/Shaders/BlinnPhongFragmentShader.glsl");
        piston = new Piston(camera, program);
        piston.setScaleMatrix(vmath.scaleNonUniform(1.0f, 1.0f, 13.0f));

        touchPosLoc = GLES30.glGetUniformLocation(program.getProgramID(), "touchPos");
        ambientColorLoc = GLES30.glGetUniformLocation(program.getProgramID(), "ambientColor");
        gridSizeLoc = GLES30.glGetUniformLocation(program.getProgramID(), "gridSize");
        camPosLoc = GLES30.glGetUniformLocation(program.getProgramID(), "cam_pos");
        pathPos = GLES30.glGetUniformLocation(program.getProgramID(), "path");

        GLES30.glUniform1i(gridSizeLoc, gridSize);

        path[0] = 0.0f;
        path[1] = 0.0f;
        path[2] = 0.0f;
        path[3] = 5.0f;
        path[4] = 0.0f;
        path[5] = 10.0f;
        path[6] = 0.0f;
        path[7] = 15.0f;
        path[8] = 0.0f;
        path[9] = 20.0f;

    }

    public void render(float t)
    {
        int r = 0;
        int g = 45;
        int b = 45;
        float[] color = new float[]{((float)r/255.0f), ((float)g/255.0f), ((float)b/255.0f), 1.0f};

            piston.updateModelMatrix();
            piston.shader.render();
            GLES30.glUniformMatrix4fv(piston.uLocs.MODEL, 1, false, piston.modelMatrix, 0);
            GLES30.glUniform2fv(touchPosLoc, 1, touchPos, 0);
            GLES30.glUniform3fv(ambientColorLoc, 1, color, 0);
            GLES30.glUniform3fv(camPosLoc, 1, camPos, 0);
            GLES30.glUniform2fv(pathPos, 5, path, 0);
            camera.render(program.getProgramID());
            GLES30.glBindVertexArray(piston.model.vertexArray[0]);
            GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, piston.model.count/3 , gridSize*gridSize);
    }

}
