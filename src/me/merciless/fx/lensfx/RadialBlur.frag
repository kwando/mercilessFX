uniform sampler2D m_Texture;
varying vec2 texCoord;
const int NUM_SAMPLES = 10;
uniform float m_FlareDispersal;
uniform float m_FlareHaloWidth;
uniform vec3 m_Chromatic;


vec3 textureDistorted(sampler2D tex, vec2 sample_center, vec2 sample_vector, vec3 distortion) {
	return vec3(
		texture2D(tex, sample_center + sample_vector * distortion.r).r,
		texture2D(tex, sample_center + sample_vector * distortion.g).g,
		texture2D(tex, sample_center + sample_vector * distortion.b).b
	);
}

void main() {
    vec2 sample_vector = (0.5 - texCoord) * m_FlareDispersal;
    vec2 halo_vector = normalize(sample_vector) * m_FlareHaloWidth;

    vec3 result = textureDistorted(m_Texture, texCoord + halo_vector, halo_vector, m_Chromatic).rgb;
    for(int i = 0; i < NUM_SAMPLES; ++i){
      vec2 offset = sample_vector * float(i);
      result += textureDistorted(m_Texture, texCoord + offset, offset, m_Chromatic).rgb;
    }
    gl_FragColor.rgb = result;
}