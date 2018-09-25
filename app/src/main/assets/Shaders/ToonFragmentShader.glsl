#version 300 es

precision mediump float;

uniform vec4 uColor;
uniform vec3 camera_pos;

in vec3 N;
in vec3 V;
in vec3 L;

out vec4 color;

void main(void)
{
    // cell shading colors
    vec4 dark = uColor*0.25;
    vec4 ambient = uColor;
    vec4 bright = uColor*2.0;

    // Calculate per-pixel normal and light vector
    vec3 normal = normalize(N);
    vec3 light = normalize(L);

    // Simple N dot L diffuse lighting
    float tc = pow(max(0.0, dot(normal, light)), 5.0);

    // Sample from cell shading colors
    if(tc < 0.00001)
    {
        color = dark * 4.0*(tc * 0.8 + 0.2);
    }
    else if(tc < 0.93)
    {
        color = ambient * (tc * 0.8 + 0.2);
    }
    else
    {
        color = bright * (tc * 0.8 + 0.2);
    }
}