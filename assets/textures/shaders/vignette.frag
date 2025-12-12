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

const float VIGNETTE_STRENGTH = 0.5;
const float VIGNETTE_SOFTNESS = 0.45;
const float CONTRAST = 1.05;
const float SATURATION = 0.9;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    vec2 uv = v_texCoords;
    vec2 center = uv - vec2(0.5);
    float dist = length(center);
    float vignette = smoothstep(VIGNETTE_STRENGTH + VIGNETTE_SOFTNESS, 
                                VIGNETTE_STRENGTH - VIGNETTE_SOFTNESS, 
                                dist);
    
    vec3 color = texColor.rgb;
    
    color.r *= 0.9; 
    color.g *= 0.95;
    color.b *= 1.15;
    
    color = (color - 0.5) * CONTRAST + 0.5;
    
    float luminance = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(luminance), color, SATURATION);
    
    color *= vignette;
    
    gl_FragColor = vec4(color, texColor.a) * v_color;
}