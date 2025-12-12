#version 120

#ifdef GL_ES
#define LOWP lowp
precision highp float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform float u_time;

const vec3 WARM_GLOW = vec3(1.4, 1.2, 1.0);
const float BRIGHTNESS = 0.8;
const float CONTRAST = 1.08;

const float BLOOM_THRESHOLD = 0.75;
const float BLOOM_INTENSITY = 0.6;

const float VIGNETTE_STRENGTH = 0.25;
const float VIGNETTE_SOFTNESS = 0.6;

const float SNOW_TINT = 0.05;
const vec3 COOL_ACCENT = vec3(0.95, 0.98, 1.05);

const float CHROMA_AMOUNT = 0.0008;

float noise(vec2 co) {
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

vec3 bloom(vec2 uv) {
    vec3 bloomColor = vec3(0.0);
    vec4 texColor = texture2D(u_texture, uv);
    float brightness = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
    
    if(brightness > BLOOM_THRESHOLD) {
        for(int x = -2; x <= 2; x++) {
            for(int y = -2; y <= 2; y++) {
                vec2 offset = vec2(float(x), float(y)) / u_resolution * 1.5;
                vec4 sample = texture2D(u_texture, uv + offset);
                float dist = length(vec2(x, y));
                bloomColor += sample.rgb / (1.0 + dist);
            }
        }
        bloomColor /= 25.0;
    }
    
    return bloomColor * BLOOM_INTENSITY;
}

vec3 chromaticAberration(vec2 uv) {
    vec2 direction = uv - vec2(0.5);
    float dist = length(direction);
    
    float r = texture2D(u_texture, uv + direction * CHROMA_AMOUNT * dist).r;
    float g = texture2D(u_texture, uv).g;
    float b = texture2D(u_texture, uv - direction * CHROMA_AMOUNT * dist).b;
    
    return vec3(r, g, b);
}

void main() {
    vec2 uv = v_texCoords;
    vec3 color = chromaticAberration(uv);
    
    vec3 bloomColor = bloom(uv);
    color += bloomColor;
    
    color *= WARM_GLOW;
    color *= BRIGHTNESS;
    color = (color - 0.5) * CONTRAST + 0.5;
    
    float edgeDist = length(uv - vec2(0.5)) * 2.0;
    float coolMix = smoothstep(0.7, 1.0, edgeDist) * SNOW_TINT;
    color = mix(color, color * COOL_ACCENT, coolMix);
    
    vec2 center = uv - vec2(0.5);
    float dist = length(center);
    float vignette = smoothstep(
        VIGNETTE_STRENGTH + VIGNETTE_SOFTNESS, 
        VIGNETTE_STRENGTH - VIGNETTE_SOFTNESS, 
        dist
    );
    vignette = mix(0.85, 1.0, vignette);
    color *= vignette;
    
    float grain = noise(uv * u_time * 0.5) * 0.015;
    color += grain;
    
    float luminance = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(luminance), color, 1.1);
    
    color = clamp(color, 0.0, 1.0);
    
    vec4 finalColor = vec4(color, texture2D(u_texture, uv).a);
    gl_FragColor = finalColor * v_color;
}