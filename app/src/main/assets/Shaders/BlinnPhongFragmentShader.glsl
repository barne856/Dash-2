#version 300 es

precision mediump float;

in vec3 normalInterp;
in vec3 vertPos;

in vec3 lightPos;
flat in float elevation;

//uniform int mode;

const vec3 lightColor = vec3(1.0, 1.0, 1.0);
const float lightPower = 500.0;
uniform vec3 ambientColor;
const vec3 diffuseColor = vec3(0.5, 0.5, 0.5);
const vec3 specColor = vec3(1.0, 1.0, 1.0);
const float shininess = 16.0;
const float screenGamma = 2.2; // Assume the monitor is calibrated to the sRGB color space

out vec4 color;

vec3 calcColorFromLight(vec3 lightPosition)
{
  vec3 normal = normalize(normalInterp);
  vec3 lightDir = lightPosition - vertPos;
  float distance = length(lightDir);
  distance = distance * distance;
  lightDir = normalize(lightDir);

  float lambertian = max(dot(lightDir,normal), 0.0);
  float specular = 0.0;

  if(lambertian > 0.0) {

    vec3 viewDir = normalize(-vertPos);

    // this is blinn phong
    vec3 halfDir = normalize(lightDir + viewDir);
    float specAngle = max(dot(halfDir, normal), 0.0);
    specular = pow(specAngle, shininess);

    // this is phong (for comparison)
    //if(mode == 2) {
    //  vec3 reflectDir = reflect(-lightDir, normal);
    //  specAngle = max(dot(reflectDir, viewDir), 0.0);
    //  // note that the exponent is different here
    //  specular = pow(specAngle, shininess/4.0);
    //}
  }
  return diffuseColor * lambertian * lightColor * lightPower / distance +
                              specColor * specular * lightColor * lightPower / distance;
}

void main() {
    float light_distance = 25.0;
    float aspect = 16.0/9.0;
    vec3 light_tr = calcColorFromLight(vec3(lightPos.x + light_distance, lightPos.y + light_distance*aspect, lightPos.z));
    vec3 light_br = calcColorFromLight(vec3(lightPos.x + light_distance, lightPos.y - light_distance*aspect, lightPos.z));
    vec3 light_bl = calcColorFromLight(vec3(lightPos.x - light_distance, lightPos.y - light_distance*aspect, lightPos.z));
    vec3 light_tl = calcColorFromLight(vec3(lightPos.x - light_distance, lightPos.y + light_distance*aspect, lightPos.z));
    vec3 light_cc = calcColorFromLight(lightPos);
    vec3 colorLinear = ambientColor + light_tr + light_br + light_bl + light_tl;
    // apply gamma correction (assume ambientColor, diffuseColor and specColor
      // have been linearized, i.e. have no gamma correction in them)
      vec3 colorGammaCorrected = pow(colorLinear, vec3(1.0/screenGamma));
      // use the gamma corrected color in the fragment
      colorGammaCorrected.r = 1.0-elevation;
      color = vec4(colorGammaCorrected, 1.0);
}