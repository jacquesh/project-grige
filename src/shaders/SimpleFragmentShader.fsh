#version 330 core
layout(location = 0) out vec4 colour;

uniform sampler2D textureUnit;

in vec2 texCoordV;
in vec4 vertColour;

void main(){
    colour = texture(textureUnit, texCoordV);
}
