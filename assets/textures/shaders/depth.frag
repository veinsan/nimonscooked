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
uniform float u_cameraY;
uniform vec3 u_fogColor;

const float FOG_START = 5.0;
const float FOG_END = 15.0;
const float DEPTH_DARKENING = 0.15;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    float depth = abs(v_position.y - u_cameraY);
    float fogFactor = clamp((depth - FOG_START) / (FOG_END - FOG_START), 0.0, 1.0);
    
    vec3 color = texColor.rgb;
    color *= (1.0 - depth * DEPTH_DARKENING * 0.05);
    color = mix(color, u_fogColor, fogFactor * 0.3);
    
    gl_FragColor = vec4(color, texColor.a) * v_color;
}