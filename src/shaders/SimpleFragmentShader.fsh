#version 330 core

uniform int hasNormals;

uniform sampler2D textureUnit;
uniform sampler2D normalUnit;

in vec2 texCoordV;

void main(){
    gl_FragData[0] = texture(textureUnit, texCoordV);
    
    if(hasNormals == 1)
    	gl_FragData[1] = texture(normalUnit, texCoordV);
}
