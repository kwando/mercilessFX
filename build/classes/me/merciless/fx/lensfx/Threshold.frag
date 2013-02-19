uniform sampler2D m_Texture;
uniform float m_Threshold;

varying vec2 texCoord;

const vec3 lumConv = vec3(0.27, 0.67, 0.06);
void main() {
    // Flip texture coordinates
    vec2 flippedCoords = 1.0 - texCoord;
    
    // Lookup and threshold texture value
    vec3 color = texture2D(m_Texture, flippedCoords).rgb - m_Threshold;

    gl_FragColor.rgb = color;
}

