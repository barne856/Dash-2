package com.polaron.android.dash2;

public class GameObject {
    Model model;
    ShaderProgram shader;

    GameObject()
    {

    }

    GameObject(Model model, ShaderProgram shader)
    {
        this.model = model;
        this.shader = shader;
    }

    public void render(float t)
    {
        shader.render();
        model.render();
    }

    public int getProgramID()
    {
        return shader.getProgramID();
    }
}
