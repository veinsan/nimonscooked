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

const float VIGNETTE_STRENGTH = 0.6;
const float VIGNETTE_SOFTNESS = 0.4;
const float COLOR_BOOST = 1.15;
const float CONTRAST = 1.1;
const float SATURATION = 1.2;

vec3 adjustContrast(vec3 color, float contrast) {
    return (color - 0.5) * contrast + 0.5;
}

vec3 adjustSaturation(vec3 color, float saturation) {
    float luminance = dot(color, vec3(0.299, 0.587, 0.114));
    return mix(vec3(luminance), color, saturation);
}

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    vec2 uv = v_texCoords;
    vec2 center = uv - vec2(0.5);
    float dist = length(center);
    
    float vignette = smoothstep(VIGNETTE_STRENGTH + VIGNETTE_SOFTNESS, 
                                 VIGNETTE_STRENGTH - VIGNETTE_SOFTNESS, 
                                 dist);
    
    vec3 color = texColor.rgb;
    
    color.r *= 1.12;
    color.g *= 1.08;
    color.b *= 0.95;
    
    color = adjustContrast(color, CONTRAST);
    color = adjustSaturation(color, SATURATION);
    
    color *= COLOR_BOOST;
    color *= vignette;
    
    color = clamp(color, 0.0, 1.0);
    
    gl_FragColor = vec4(color, texColor.a) * v_color;
}