#version 330 core

uniform sampler2D textureUnit;

in vec2 texCoordV;

void main(){
    gl_FragColor = texture(textureUnit, texCoordV);
}
