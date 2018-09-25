package com.polaron.android.dash2;

import static java.lang.Math.*;

public final class vmath {
    private vmath(){}

    public static float[] perspective(float fovy, float aspect, float near, float far)
    {
        /*float[] result = zeroMatrix();
        result[0] = aspect / (float)tan(radians(0.5f * fovy));
        result[5] = 1.0f / (float)tan(radians(0.5f * fovy));
        result[10] = (near + far) / (near - far);
        result[14] = 2.0f * far * near / (near - far);
        result[7] = -1.0f;
        return result;*/

        float n = near;
        float f = far;
        float t = near*(float)tan(radians(fovy)/2.0f);
        float r = t * aspect;

        return new float[]
                {
                        n/r, 0.0f, 0.0f, 0.0f,
                        0.0f, n/t, 0.0f, 0.0f,
                        0.0f, 0.0f, -(f+n)/(f-n), -1.0f,
                        0.0f, 0.0f, -2.0f*f*n/(f-n), 0.0f
                };


        /*float q = 1.0f / (float)tan(radians(0.5f * fovy));
        float A = q/aspect;
        float B = (near + far) / (near - far);
        float C = (2.0f * near * far) / (near - far);

        float[] result = new float[]
        {
                A, 0.0f, 0.0f, 0.0f,
                0.0f, q, 0.0f, 0.0f,
                0.0f, 0.0f, B, C,
                0.0f, 0.0f, -1.0f, 0.0f
        };

        return result;*/
    }

    public static float[] orthogonal(float r, float t, float n, float f)
    {
        return new float[]
                {
                        1.0f/r, 0.0f, 0.0f, 0.0f,
                        0.0f, 1.0f/t, 0.0f, 0.0f,
                        0.0f, 0.0f, -2.0f/(f-n), 0.0f,
                        0.0f, 0.0f, -(f+n)/(f-n), 1.0f
                };
    }

    public static float[] lookat(float[] eye, float[] center, float[] up)
    {
        float[] f = normalize(subtractVectors(eye, center));
        float[] s = cross(new float[]{0.0f, 1.0f, 0.0f}, f);
        float[] upN = cross(f, s);
        float[] M = new float[]
                {
                        s[0], upN[0], f[0], 0.0f,
                        s[1], upN[1], f[1], 0.0f,
                        s[2], upN[2], f[2], 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f
                };
        return matrixMult(M, translation(-eye[0], -eye[1], -eye[2]));
    }

    public static float[] translation(float x, float y, float z)
    {
        return new float[]
                {
                  1.0f, 0.0f, 0.0f, 0.0f,
                  0.0f, 1.0f, 0.0f, 0.0f,
                  0.0f, 0.0f, 1.0f, 0.0f,
                  x, y, z, 1.0f
                };
    }
    public static float[] scaleUniform(float s)
    {
        return new float[]
                {
                        s, 0.0f, 0.0f, 0.0f,
                        0.0f, s, 0.0f, 0.0f,
                        0.0f, 0.0f, s, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f
                };
    }

    public static float[] matrixMult(float[] M1, float[] M2)
    {
        int n = (int)sqrt(M1.length);
        float[] M = new float[n*n];
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                M[j*n + i] = 0.0f;
                for(int k = 0; k < n; k++) {
                    M[j * n + i] += M1[k * n + i] * M2[k + j * n];
                }
            }
        }
        return M;
    }

    public static float[] identityMatrix()
    {
        return new float[]
                {
                  1.0f, 0.0f, 0.0f, 0.0f,
                  0.0f, 1.0f, 0.0f, 0.0f,
                  0.0f, 0.0f, 1.0f, 0.0f,
                  0.0f, 0.0f, 0.0f, 1.0f
                };
    }

    public static float[] zeroMatrix()
    {
        return new float[]
                {
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 0.0f
                };
    }

    public static float[] normalize(float[] vec)
    {
        float l = 0.0f;
        float[] result = new float[vec.length];
        for(int i = 0; i < vec.length; i++)
        {
            l += vec[i]*vec[i];
        }
        l = (float)Math.sqrt(l);
        for(int i = 0; i < vec.length; i++)
        {
            result[i] = vec[i]/l;
        }
        return result;
    }

    public static float[] subtractVectors(float[] v1, float[] v2)
    {
        return new float[]{v1[0] - v2[0], v1[1] - v2[1], v1[2]- v2[2]};
    }

    public static float[] cross(float[] v1, float[] v2)
    {
        return new float[]
        {
            v1[1] * v2[2] - v2[1] * v1[2],
                v1[2] * v2[0] - v2[2] * v1[0],
                v1[0] * v2[1] - v2[0] * v1[1]
        };
    }

    public static float radians(float degrees)
    {
        return degrees * (float)Math.PI/180.0f;
    }


}
