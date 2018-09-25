package com.polaron.android.dash2;


import android.opengl.GLES30;

public class Sphere extends GameObject {

    class UniformLocations
    {
        int MODEL, COLOR;
    }
    UniformLocations uLocs = new UniformLocations();

    float[] translation = new float[16];

    Sphere()
    {

        int recursionLevel = 4;

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

    int[] iter = new int[]{0};
    int count = 180*(int)Math.pow(4, recursionLevel);
    float[] normals = new float[count];

    for(int i = 0; i < 20; i++)
    {
        float[] v1 = new float[]{vertices[3 * indices[3 * i]], vertices[3 * indices[3 * i] + 1],
                vertices[3 * indices[3 * i] + 2]};
        float[] v2 = {vertices[3 * indices[3 * i + 1]], vertices[3 * indices[3 * i + 1] + 1],
            vertices[3 * indices[3 * i + 1] + 2]};
        float[] v3 = {vertices[3 * indices[3 * i + 2]], vertices[3 * indices[3 * i + 2] + 1],
            vertices[3 * indices[3 * i + 2] + 2]};
        subdivide(v1, v2, v3, recursionLevel, normals, iter);
    }

    Model sphere = new Model(normals, normals);
    //ShaderProgram program = new ShaderProgram("assets/Objects/Sphere/VertexShader.glsl", "assets/Objects/Sphere/FragmentShader.glsl");
    ShaderProgram program = new ShaderProgram("assets/Shaders/ToonVertexShader.glsl", "assets/Shaders/ToonFragmentShader.glsl");

    model = sphere;
    shader = program;

    // Model
        uLocs.MODEL = GLES30.glGetUniformLocation(program.getProgramID(), "model");
        GLES30.glUniformMatrix4fv(uLocs.MODEL, 1, false, vmath.identityMatrix(), 0);
    // Color
        float[] color = new float[]{0.439f, 0.666f, 0.960f, 1.0f};
        uLocs.COLOR = GLES30.glGetUniformLocation(program.getProgramID(), "uColor");
        GLES30.glUniform4fv(uLocs.COLOR, 1, color, 0);

}

    @Override
    public void render(float t)
    {
        super.render(t);
        update(t);
    }

    private void update(float t)
    {

    }

    private void subdivide(float[] v1, float[] v2, float[] v3, int depth, float[] vertices, int[] iter)
    {
        float[] v12 = new float[3], v23 = new float[3], v31 = new float[3];

        if (depth == 0) {
            vertices[iter[0]++] = v1[0];
            vertices[iter[0]++] = v1[1];
            vertices[iter[0]++] = v1[2];
            vertices[iter[0]++] = v2[0];
            vertices[iter[0]++] = v2[1];
            vertices[iter[0]++] = v2[2];
            vertices[iter[0]++] = v3[0];
            vertices[iter[0]++] = v3[1];
            vertices[iter[0]++] = v3[2];
            return;
        }

        // calculate midpoints of each side
        for (int i = 0; i < 3; i++) {
            v12[i] = (v1[i] + v2[i]) / 2.0f;
            v23[i] = (v2[i] + v3[i]) / 2.0f;
            v31[i] = (v3[i] + v1[i]) / 2.0f;
        }
        // extrude midpoints to lie on unit sphere
        v12 = vmath.normalize(v12);
        v23 = vmath.normalize(v23);
        v31 = vmath.normalize(v31);

        // recursively subdivide new triangles
        subdivide(v1, v12, v31, depth - 1, vertices, iter);
        subdivide(v2, v23, v12, depth - 1, vertices, iter);
        subdivide(v3, v31, v23, depth - 1, vertices, iter);
        subdivide(v12, v23, v31, depth - 1, vertices, iter);
    }

    public void setPosition(float[] position)
    {
        translation = vmath.translation(position[0], position[1], position[2]);
        GLES30.glUniformMatrix4fv(uLocs.MODEL, 1, false, translation, 0);
    }

}
