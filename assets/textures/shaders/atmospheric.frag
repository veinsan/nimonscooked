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

const int VOLUMETRIC_SAMPLES = 16;
const float VOLUMETRIC_DENSITY = 0.4;
const float VOLUMETRIC_DECAY = 0.96;
const vec2 LIGHT_SOURCE = vec2(0.5, 0.75);

const vec3 FOG_COLOR = vec3(0.6, 0.7, 0.85);
const float FOG_DENSITY = 0.02;
const float FOG_START = 200.0;
const float FOG_END = 800.0;

const vec3 SNOW_TINT = vec3(0.95, 0.95, 1.0);
const float SNOW_SCATTER = 0.3;

const float DEPTH_FADE_START = 0.3;
const float DEPTH_FADE_END = 0.7;

vec3 volumetricLight(vec2 uv) {
    vec2 deltaTexCoord = uv - LIGHT_SOURCE;
    deltaTexCoord *= 1.0 / float(VOLUMETRIC_SAMPLES) * VOLUMETRIC_DENSITY;
    
    vec3 color = vec3(0.0);
    float illuminationDecay = 1.0;
    vec2 sampleUV = uv;
    
    for(int i = 0; i < VOLUMETRIC_SAMPLES; i++) {
        sampleUV -= deltaTexCoord;
        vec3 sample = texture2D(u_texture, sampleUV).rgb;
        sample *= illuminationDecay;
        color += sample;
        illuminationDecay *= VOLUMETRIC_DECAY;
    }
    
    color /= float(VOLUMETRIC_SAMPLES);
    color *= 0.4;
    
    return color;
}

vec3 applyFog(vec3 color, float distance) {
    float fogAmount = 1.0 - exp(-distance * FOG_DENSITY);
    fogAmount = smoothstep(FOG_START, FOG_END, distance) * fogAmount;
    return mix(color, FOG_COLOR, fogAmount * 0.5);
}

vec3 depthGrading(vec3 color, vec2 uv) {
    float depth = 1.0 - uv.y;
    float blueTint = smoothstep(DEPTH_FADE_START, DEPTH_FADE_END, depth);
    color = mix(color, color * vec3(0.9, 0.95, 1.1), blueTint * 0.3);
    color *= 1.0 - blueTint * 0.15;
    return color;
}

vec3 snowScatter(vec3 color, vec2 uv) {
    float scatter = texture2D(u_texture, uv).a;
    float noise = fract(sin(dot(uv * u_time * 0.1, vec2(12.9898, 78.233))) * 43758.5453);
    scatter += noise * 0.1;
    vec3 scatterColor = SNOW_TINT * scatter * SNOW_SCATTER;
    return color + scatterColor;
}

float atmosphericParticles(vec2 uv) {
    float particles = 0.0;
    
    for(int i = 0; i < 3; i++) {
        float speed = 0.02 * float(i + 1);
        vec2 offset = vec2(u_time * speed * 0.5, u_time * speed);
        vec2 particleUV = uv * (3.0 + float(i) * 2.0) + offset;
        float noise = fract(sin(dot(particleUV, vec2(12.9898, 78.233))) * 43758.5453);
        particles += noise * (0.3 - float(i) * 0.08);
    }
    
    particles = smoothstep(0.97, 1.0, particles);
    return particles;
}

vec3 temperatureShift(vec3 color, float temperature) {
    if(temperature < 0.0) {
        color.b *= 1.0 + abs(temperature) * 0.2;
        color.r *= 1.0 - abs(temperature) * 0.15;
    } else {
        color.r *= 1.0 + temperature * 0.2;
        color.b *= 1.0 - temperature * 0.15;
    }
    return color;
}

void main() {
    vec2 uv = v_texCoords;
    vec4 texColor = texture2D(u_texture, uv);
    vec3 color = texColor.rgb;
    
    vec3 volumetric = volumetricLight(uv);
    color += volumetric * 0.8;
    
    color = depthGrading(color, uv);
    
    float distance = length((uv - vec2(0.5)) * u_resolution);
    color = applyFog(color, distance);
    
    color = snowScatter(color, uv);
    
    float particles = atmosphericParticles(uv);
    color += vec3(particles) * 0.3;
    
    float temperature = (uv.y - 0.5) * 0.6;
    color = temperatureShift(color, temperature);
    
    float ao = 0.9 + 0.1 * smoothstep(0.0, 0.4, uv.y);
    color *= ao;
    
    gl_FragColor = vec4(color, texColor.a) * v_color;
}