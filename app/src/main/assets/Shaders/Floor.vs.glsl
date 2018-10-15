#version 300 es

// Model
layout(location = 0) in vec4 position;
layout(location = 1) in vec3 normal;

// Transformations
uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

// Interlaced grid
uniform int iIndex;
uniform float xOffset;
uniform float yOffset;
uniform float t;
uniform vec2 camPos;

// Path variables
uniform int[12] instances;
uniform int[12] iIndices;
uniform float[12] elevations;

// Light data
uniform vec3 light_pos;

// Output to fragment shader
out vec3 vertPos;
out vec3 normalInterp;
out vec3 lightPos;
out vec3 ambientColor;

// effects
uniform int ploomEffect;

float noise(int x, int y, int random)
{
    int n = x + y * 57 + random * 131;
    n = (n << 13) ^ n;
    return (1.0f - float( (n * (n * n * 15731 + 789221) + 1376312589)&0x7fffffff)* 0.000000000931322574615478515625f);
}

float waveFunc(float x, float y)
{
    return cos(y+t) * cos(sin(x*0.1));
}

float ploom(float x, float y)
{
    return (sin(0.75*sqrt(x*x+y*y)) + 1.0f) * sin(t*3.0f) - 1.0;
}

void main()
{
    // (x, y) from instance ID via bitwise operations
    float x = float(gl_InstanceID >> 5) - 16.0 + 0.5*float(iIndex);
    float y = (1.0/sqrt(3.0))*float(gl_InstanceID & 0x1F) - 9.0 + float(iIndex)*(sqrt(3.0)/6.0);

    // elevations
    //float elevation = 0.3*waveFunc(x-xOffset/3.0,y-yOffset/3.0);
    float elevation = -0.3;
    if(ploomEffect == 1)
    {
        elevation = 0.3*ploom(x-(camPos.x)/3.0,y-(camPos.y)/3.0);
    }

    for(int i = 0; i < 12; i++)
    {
        if(instances[i] == gl_InstanceID && iIndex == iIndices[i])
        {
            elevation = elevations[i]-0.3;
        }
    }

    // set ambient color
    //ambientColor = vec3(0.05, 0.15, 0.25);
    ambientColor = vec3(0.25, 0.15, 0.05);
    ambientColor = ambientColor*(elevation + 0.2);

    // Grid offset
    vec4 offset = 3.0*vec4(x, y, elevation, 0.0);
    vec4 offsetPos = model * position + offset;

    // Output screen position
    gl_Position =  projection * view * offsetPos;

    // Output to fragment shader
    vec4 vertPos4 = view * offsetPos;
    vertPos = vec3(vertPos4) / vertPos4.w;
    mat4 normalMat = transpose(inverse(view * model)); // use a uniform for this
    normalInterp = vec3(normalMat * vec4(normal, 0.0));
    lightPos = vec3(view * vec4(light_pos, 1.0));
}