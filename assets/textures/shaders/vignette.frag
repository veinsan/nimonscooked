#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec2 u_resolution;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);

    vec2 position = (gl_FragCoord.xy / u_resolution.xy) - vec2(0.5);
    float len = length(position);
    float vignette = smoothstep(0.8, 0.2, len);

    color.r *= 1.1;
    color.g *= 1.05;
    color.b *= 0.9;

    gl_FragColor = vec4(color.rgb * vignette, color.a) * v_color;
}