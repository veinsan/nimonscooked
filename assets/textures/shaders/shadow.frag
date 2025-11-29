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
uniform vec2 u_lightPos;
uniform float u_shadowIntensity;
uniform float u_shadowSoftness;

const float SHADOW_BIAS = 0.05;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    vec2 toLight = u_lightPos - v_position.xy;
    float distToLight = length(toLight);
    
    float shadow = 1.0 - smoothstep(0.0, u_shadowSoftness, 
                                     abs(distToLight - SHADOW_BIAS));
    shadow = 1.0 - shadow * u_shadowIntensity;
    
    vec3 finalColor = texColor.rgb * shadow;
    
    gl_FragColor = vec4(finalColor, texColor.a) * v_color;
}