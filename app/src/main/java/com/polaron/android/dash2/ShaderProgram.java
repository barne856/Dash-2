package com.polaron.android.dash2;

import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderProgram {
    int program;

    ShaderProgram(String vertexSourcePath, String fragmentSourcePath)
    {
        program = GLES30.glCreateProgram();

        int vertexShader = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        GLES30.glShaderSource(vertexShader, readShader(vertexSourcePath));
        GLES30.glCompileShader(vertexShader);
        Log.d("VERTEX SHADER", checkShader(vertexShader));
        GLES30.glAttachShader(program, vertexShader);

        int fragmentShader = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        GLES30.glShaderSource(fragmentShader, readShader(fragmentSourcePath));
        GLES30.glCompileShader(fragmentShader);
        Log.d("FRAGMENT SHADER", checkShader(fragmentShader));
        GLES30.glAttachShader(program, fragmentShader);

        GLES30.glLinkProgram(program);
        Log.d("PROGRAM", checkProgram(program));
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);
        GLES30.glUseProgram(program);
    }

    public void render()
    {
        GLES30.glUseProgram(program);
    }

    private String checkShader(int shader)
    {
        String message = "Shader Compiled Successfully.";
        int[] result = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, result, 0);
        if(result[0] == 0)
        {
            int[] length = new int[1];
            message = GLES30.glGetShaderInfoLog(shader);
            GLES30.glDeleteShader(shader);
            return message;
        }
        return message;
    }

    private String checkProgram(int program)
    {
        String message = "Program validated.";
        int[] status = new int[1];
        GLES30.glValidateProgram(program);
        GLES30.glGetProgramiv(program, GLES30.GL_VALIDATE_STATUS, status, 0);
        if(status[0] == 0)
        {
            message = "Program failed to validate.\n";
            message += GLES30.glGetProgramInfoLog(program);
            return message;
        }
        return message;
    }

    public int getProgramID()
    {
        return program;
    }

    public String readShader(String filepath)
    {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(filepath);


        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}
