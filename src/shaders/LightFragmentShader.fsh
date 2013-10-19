#version 330 core
layout(location = 0) out vec4 color;

uniform sampler2D textureUnit;

in vec2 texCoordV;

void main(){
    color = texture(textureUnit, texCoordV);
}
