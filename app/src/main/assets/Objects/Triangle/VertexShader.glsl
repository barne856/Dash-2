#version 300 es

layout(location = 0) in vec4 position;
layout(location = 1) in vec3 normal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

vec3 light_pos = vec3(10.0, 10.0, 10.0);

// Inputs from vertex shader
out vec3 N;
out vec3 L;
out vec3 V;

void main() {
    // Calculate view-space coordinate
        vec4 P = model * position;

        // Calculate normal in view-space
        mat4 normalMatrix = transpose(inverse(model));
        N = mat3(normalMatrix) * normal;

        // Calculate light vector
        L = light_pos - P.xyz;

        // Calculate view vector
        V = P.xyz;

        // Calculate the clip-space position of each vertex
        gl_Position = projection * view * P;
}