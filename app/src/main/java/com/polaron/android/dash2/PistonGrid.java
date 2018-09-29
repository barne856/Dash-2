package com.polaron.android.dash2;


import android.opengl.GLES30;

public class PistonGrid {

    ShaderProgram program;
    Camera camera;
    int gridSize = 51;
    Piston piston;
    float[] touchPos = new float[]{0.0f, 0.0f};
    int touchPosLoc;
    int ambientColorLoc;
    int gridSizeLoc;

    PistonGrid(Camera camera)
    {
        this.camera = camera;
        program = new ShaderProgram("assets/Shaders/BlinnPhongVertexShader.glsl", "assets/Shaders/BlinnPhongFragmentShader.glsl");
        piston = new Piston(camera, program);
        piston.setScaleMatrix(vmath.scaleNonUniform(1.0f, 1.0f, 13.0f));

        touchPosLoc = GLES30.glGetUniformLocation(program.getProgramID(), "touchPos");
        ambientColorLoc = GLES30.glGetUniformLocation(program.getProgramID(), "ambientColor");
        gridSizeLoc = GLES30.glGetUniformLocation(program.getProgramID(), "gridSize");

        GLES30.glUniform1i(gridSizeLoc, gridSize);

    }

    public void render(float t)
    {
        int r = 109;
        int g = 46;
        int b = 27;
        float[] color = new float[]{((float)r/255.0f), ((float)g/255.0f), ((float)b/255.0f), 1.0f};

            piston.updateModelMatrix();
            piston.shader.render();
            GLES30.glUniformMatrix4fv(piston.uLocs.MODEL, 1, false, piston.modelMatrix, 0);
            GLES30.glUniform2fv(touchPosLoc, 1, touchPos, 0);
            GLES30.glUniform3fv(ambientColorLoc, 1, color, 0);
            camera.render(program.getProgramID());
            GLES30.glBindVertexArray(piston.model.vertexArray[0]);
            GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, piston.model.count/3 , gridSize*gridSize);
    }

}
