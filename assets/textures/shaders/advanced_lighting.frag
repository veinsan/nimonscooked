#ifdef GL_ES
#define LOWP lowp
precision highp float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
varying vec3 v_position;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform float u_time;

const int MAX_LIGHTS = 6;
uniform int u_lightCount;
uniform vec2 u_lightPositions[MAX_LIGHTS];
uniform vec3 u_lightColors[MAX_LIGHTS];
uniform float u_lightRadii[MAX_LIGHTS];
uniform float u_lightIntensities[MAX_LIGHTS];

const vec3 AMBIENT_COLOR = vec3(0.25, 0.28, 0.35);
const float AMBIENT_STRENGTH = 0.4;

const vec3 WARM_LIGHT = vec3(1.0, 0.85, 0.6);
const vec3 FIRE_LIGHT = vec3(1.0, 0.6, 0.3);

const float SPECULAR_STRENGTH = 0.3;
const float SPECULAR_POWER = 16.0;

const float RIM_STRENGTH = 0.4;
const vec3 RIM_COLOR = vec3(0.8, 0.9, 1.0);

const float SSS_STRENGTH = 0.2;

float attenuationSmooth(float distance, float radius) {
    float x = distance / radius;
    if(x >= 1.0) return 0.0;
    float attenuation = 1.0 - x * x;
    attenuation = attenuation * attenuation;
    return attenuation;
}

vec3 calculateLighting(vec2 pixelPos, vec3 normal) {
    vec3 totalLight = AMBIENT_COLOR * AMBIENT_STRENGTH;
    float flicker = sin(u_time * 8.0) * 0.05 + 1.0;
    
    for(int i = 0; i < MAX_LIGHTS; i++) {
        if(i >= u_lightCount) break;
        
        vec2 lightPos = u_lightPositions[i];
        vec3 lightColor = u_lightColors[i];
        float lightRadius = u_lightRadii[i];
        float lightIntensity = u_lightIntensities[i];
        
        float dist = length(pixelPos - lightPos);
        float attenuation = attenuationSmooth(dist, lightRadius);
        
        if(attenuation > 0.0) {
            vec3 lightDir = normalize(vec3(lightPos - pixelPos, 50.0));
            float diffuse = max(dot(normal, lightDir), 0.0);
            vec3 viewDir = vec3(0.0, 0.0, 1.0);
            vec3 halfDir = normalize(lightDir + viewDir);
            float specular = pow(max(dot(normal, halfDir), 0.0), SPECULAR_POWER);
            specular *= SPECULAR_STRENGTH;
            float flickerAmount = lightColor.r > lightColor.b ? flicker : 1.0;
            vec3 light = lightColor * lightIntensity * attenuation * flickerAmount;
            totalLight += light * (diffuse + specular);
        }
    }
    
    return totalLight;
}

vec3 rimLighting(vec2 uv) {
    vec2 center = vec2(0.5);
    vec2 toCenter = center - uv;
    float dist = length(toCenter);
    float rim = smoothstep(0.3, 0.5, dist);
    return RIM_COLOR * rim * RIM_STRENGTH;
}

vec3 subsurfaceScatter(vec3 color, vec3 lightColor) {
    float luminance = dot(color, vec3(0.299, 0.587, 0.114));
    vec3 scatter = lightColor * luminance * SSS_STRENGTH;
    return scatter;
}

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    vec2 pixelPos = gl_FragCoord.xy;
    vec3 normal = vec3(0.0, 0.0, 1.0);
    vec3 lighting = calculateLighting(pixelPos, normal);
    vec3 color = texColor.rgb * lighting;
    color += rimLighting(v_texCoords);
    color += subsurfaceScatter(texColor.rgb, lighting);
    color = pow(color, vec3(0.95));
    gl_FragColor = vec4(color, texColor.a) * v_color;
}