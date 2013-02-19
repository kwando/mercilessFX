uniform mat4 g_WorldViewProjectionMatrix;
uniform float g_Aspect;
attribute vec4 inPosition;
attribute vec2 inTexCoord;
varying vec2 texCoord;

void main() {  
    gl_Position = inPosition * 2.0 - 1.0; //vec4(pos, 0.0, 1.0);

    texCoord = inTexCoord;

    // This is needed to get a spherical halo!
    float scale = 1.0 / g_Aspect;
    float offset = (1.0 - scale)/ 2.0;
    
    texCoord.y = texCoord.y * scale + offset;
}