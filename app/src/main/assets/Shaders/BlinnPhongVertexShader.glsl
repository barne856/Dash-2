#version 300 es

layout(location = 0) in vec4 position;
layout(location = 1) in vec3 normal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform vec3 light_pos;
uniform vec3 cam_pos;

out vec3 normalInterp;
out vec3 vertPos;
out vec3 lightPos;
flat out float elevation;

uniform int gridSize;
uniform vec2 touchPos;

const float PI = 3.1415926535897932384626433832795;

//float rand(vec2 co){
//    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
//}

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
float gap = 0.2;
float rowWidth = 2.0*(0.75 + gap);
float columnWidth = 2.0*(gap+(sqrt(3.0)/2.0));
float deltaX = (float(gridSize-1)*(columnWidth/2.0));
float deltaY = (float(gridSize-1)*(rowWidth/2.0));

float yTranslation = 2.0f*rowWidth*floor(cam_pos.y/(2.0f*rowWidth));
float xTranslation = 2.0f*columnWidth*floor(cam_pos.x/(2.0f*columnWidth));

int j = int(mod(float(gl_InstanceID), float(gridSize)));
int i = int(floor(float(gl_InstanceID)/float(gridSize)));
float x = deltaX*((2.0*float(j)/float(gridSize-1)) - 1.0);
float temp;
if(int((float(i) - 2.0*floor(float(i)/2.0))) == int(0))
{
    x = x+columnWidth/2.0;
}

                float y = -deltaY*((2.0*float(i)/float(gridSize-1)) - 1.0);

                float z = 0.0;

                    float dx = -10.0*touchPos.x - cam_pos.x + xTranslation;
                    float dy = -10.0*touchPos.y - cam_pos.y + yTranslation;
                    z = z + hillFunc(0.1f*(x+dx), 0.1f*(y+dy));

                elevation = z;
                return vec4(position.x + x + xTranslation, position.y + y + yTranslation, position.z + z, position.w);
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