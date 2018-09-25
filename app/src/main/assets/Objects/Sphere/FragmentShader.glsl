#version 300 es

precision mediump float;

uniform vec4 uColor;
uniform vec3 camera_pos;
out vec4 color;

// Input from vertex shader
in vec3 N;
in vec3 L;
in vec3 V;

// Material properties
vec4 diffuse_albedo = vec4(0.7);
vec3 specular_albedo = vec3(1.0);
float specular_power = 8.0;

vec3 rim_lighting(vec3 N, vec3 V)
{
    // Calculate the rim factor
    float f = 1.0 - dot(N, V);
    // Constrain it to the range 0 to 1 using a smoothstep function
    f = smoothstep(0.0, 1.0, f);
    // Raise it to the rim exponent
    f = pow(f, 32.0);
    // Finally, multiply it by the rim color
    return f * vec3(uColor);
}

void main(void)
{
    diffuse_albedo = 0.7*uColor;
    diffuse_albedo.a = 1.0;
    // Normalize the incoming N, L and V vectors
    vec3 N = normalize(N);
    vec3 L = normalize(L);
    vec3 V = normalize(camera_pos - V);
    vec3 R = reflect(-L,N);

    // Compute the diffuse and specular components for each fragment
    vec3 diffuse = max(dot(N, L), 0.0) * vec3(uColor);
    vec3 specular = pow(max(dot(V, R), 0.0), specular_power) * specular_albedo;
    vec3 rim = rim_lighting(N,V);

    // Write final color to the framebuffer
    color = vec4(diffuse + specular, 1.0) + uColor*0.1;
    color[3] = 1.0;

}