#version 300 es

layout(location = 0) in vec4 position;
layout(location = 1) in vec3 normal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform vec3 light_pos;
uniform vec3 cam_pos;
uniform vec2[5] path;

out vec3 normalInterp;
out vec3 vertPos;
out vec3 lightPos;

uniform int gridSize;
uniform vec2 touchPos;

const float PI = 3.1415926535897932384626433832795;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float p(float t)
{
    return (1.0-t)*(1.0-t)*(1.0+t)*(1.0+t);
}

float hillFunc(float x, float y)
{
float t = min(1.0, sqrt(x*x+y*y));
return p(t);
}

float coneFunc(float x, float y)
{
    float z = -(sqrt(x*x+y*y)-5.0);
    if(z > 0.0)
    {
        return z;
    }
    else
    {
    return 0.0;
    }
}

vec4 calcOffset()
{

float gridDelta = (sqrt(3.0)/2.0);
float gap = 0.1f;
float rowWidth = (1.5 + 2.0*gap);
float deltaX = (float(gridSize-1)*(gap+(sqrt(3.0)/2.0)));
float deltaY = (float(gridSize-1)*(gap+(3.0/4.0)));

float yTranslation = 2.0f*rowWidth*floor(cam_pos.y/(2.0f*rowWidth));

int j = int(mod(float(gl_InstanceID), float(gridSize)));
int i = int(floor(float(gl_InstanceID)/float(gridSize)));
float x = deltaX*((2.0*float(j)/float(gridSize-1)) - 1.0);
float temp;
if(int((float(i) - 2.0*floor(float(i)/2.0))) == int(0))
{
    x = x+gridDelta+gap;
}

                float y = -deltaY*((2.0*float(i)/float(gridSize-1)) - 1.0);
                float dx = -20.0*touchPos.x;
                float dy = -20.0*touchPos.y - cam_pos.y + yTranslation;
                //float z = hillFunc((x+dx)/5.0, (y+dy)/5.0);
                float z = coneFunc(x+dx, y+dy);
                //float z = rand(vec2(float(gl_InstanceID), 5.0545));
                //float z = 0.0f;
                return vec4(position.x + x, position.y + y + yTranslation, position.z + z/5.0, position.w);
}

void main(){
    vec4 offsetPos = calcOffset();
    gl_Position = projection * view * model * offsetPos;
    vec4 vertPos4 = view * model * offsetPos;
    vertPos = vec3(vertPos4) / vertPos4.w;
    mat4 normalMat = transpose(inverse(view * model));
    normalInterp = vec3(normalMat * vec4(normal, 0.0));
    lightPos = vec3(view * vec4(light_pos, 1.0));
}