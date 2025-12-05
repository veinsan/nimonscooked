#ifdef GL_ES
#define LOWP lowp
precision highp float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_texelSize;
uniform vec3 u_outlineColor;
uniform float u_outlineThickness;

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    float alpha = texColor.a;
    float outline = 0.0;
    
    float thickness = u_outlineThickness;
    
    for(float x = -thickness; x <= thickness; x += 1.0) {
        for(float y = -thickness; y <= thickness; y += 1.0) {
            vec2 offset = vec2(x, y) * u_texelSize;
            float sampleAlpha = texture2D(u_texture, v_texCoords + offset).a;
            outline = max(outline, sampleAlpha);
        }
    }
    
    outline = outline - alpha;
    
    vec3 finalColor = mix(texColor.rgb, u_outlineColor, outline);
    float finalAlpha = max(alpha, outline);
    
    gl_FragColor = vec4(finalColor, finalAlpha) * v_color;
}