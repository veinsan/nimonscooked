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
uniform vec2 u_lightPos;
uniform vec3 u_lightColor;
uniform float u_lightRadius;
uniform float u_ambientLight;

const float LIGHT_FALLOFF = 2.0;
const vec3 AMBIENT_COLOR = vec3(0.3, 0.35, 0.4);

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    vec2 pixelPos = gl_FragCoord.xy;
    float distance = length(pixelPos - u_lightPos);
    float attenuation = 1.0 / (1.0 + distance * distance / (u_lightRadius * u_lightRadius));
    attenuation = pow(attenuation, LIGHT_FALLOFF);
    
    vec3 lighting = u_ambientLight * AMBIENT_COLOR + attenuation * u_lightColor;
    lighting = clamp(lighting, 0.0, 1.5);
    
    vec3 finalColor = texColor.rgb * lighting;
    
    gl_FragColor = vec4(finalColor, texColor.a) * v_color;
}