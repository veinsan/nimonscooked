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
uniform float u_bloomIntensity;
uniform float u_bloomThreshold;

const int BLUR_SAMPLES = 9;
const float BLUR_OFFSETS[9] = float[](
    -4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0
);
const float BLUR_WEIGHTS[9] = float[](
    0.05, 0.09, 0.12, 0.15, 0.16, 0.15, 0.12, 0.09, 0.05
);

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoords);
    
    vec3 bloom = vec3(0.0);
    
    for(int i = 0; i < BLUR_SAMPLES; i++) {
        vec2 offset = vec2(BLUR_OFFSETS[i]) * u_texelSize;
        vec4 sample = texture2D(u_texture, v_texCoords + offset);
        
        float brightness = dot(sample.rgb, vec3(0.299, 0.587, 0.114));
        if(brightness > u_bloomThreshold) {
            bloom += sample.rgb * BLUR_WEIGHTS[i];
        }
    }
    
    vec3 finalColor = texColor.rgb + bloom * u_bloomIntensity;
    
    gl_FragColor = vec4(finalColor, texColor.a) * v_color;
}