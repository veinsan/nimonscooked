#ifdef GL_ES
#define LOWP lowp
precision highp float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_textureSize;
uniform float u_pixelScale;

void main() {
    vec2 uv = v_texCoords * u_textureSize;
    vec2 pixelUV = floor(uv / u_pixelScale) * u_pixelScale;
    vec2 finalUV = pixelUV / u_textureSize;
    
    vec4 texColor = texture2D(u_texture, finalUV);
    
    gl_FragColor = texColor * v_color;
}