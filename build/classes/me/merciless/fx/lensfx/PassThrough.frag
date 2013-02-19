uniform sampler2D m_Texture;
uniform sampler2D m_Overlay;
uniform sampler2D m_Dirt;
uniform float m_Amplification;
varying vec2 texCoord;

vec3 blend(vec3 a, vec3 b){
  return 1.0 - (1.0 - a) * (1.0 - b);
}

void main() {
    vec3 color = texture2D(m_Texture, texCoord).rgb;
    vec3 dirt = texture2D(m_Dirt, texCoord).rgb;
    vec3 flares = texture2D(m_Overlay, texCoord).rgb;
    gl_FragColor.rgb = blend(color, m_Amplification * flares * dirt);
}