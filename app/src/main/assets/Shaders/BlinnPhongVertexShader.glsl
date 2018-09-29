#version 300 es

layout(location = 0) in vec4 position;
layout(location = 1) in vec3 normal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform vec3 light_pos;

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

vec4 calcOffset()
{

float gap = (sqrt(3.0)/2.0);

float deltaX = (float(gridSize-1)*((sqrt(3.0)/2.0)));
float deltaY = (float(gridSize-1)*((3.0/4.0)));

int j = int(mod(float(gl_InstanceID), float(gridSize)));
int i = int(floor(float(gl_InstanceID)/float(gridSize)));
float x = deltaX*((2.0*float(j)/float(gridSize-1)) - 1.0);
float temp;
if(int((float(i) - 2.0*floor(float(i)/2.0))) == int(0))
{
    x = x+gap;
}

                float y = -deltaY*((2.0*float(i)/float(gridSize-1)) - 1.0);
                float dx = -20.0*touchPos.x;
                float dy = -20.0*touchPos.y;
                float z = hillFunc((x+dx)/13.0, (y+dy)/13.0);
                //float z = rand(vec2(float(gl_InstanceID), 5.0545));
                return vec4(position.x + x, position.y + y, position.z + z*2.0, position.w);
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