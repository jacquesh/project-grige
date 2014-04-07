#version 330 core

uniform sampler2D textureUnit;
uniform sampler2D normalUnit;

in vec2 texCoordV;

void main(){
    gl_FragData[0] = texture(textureUnit, texCoordV);
    gl_FragData[1] = texture(normalUnit, texCoordV);
}
