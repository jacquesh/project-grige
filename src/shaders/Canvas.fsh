#version 330 core
layout(location = 0) out vec4 colour;

uniform sampler2D geometryTextureUnit;
uniform sampler2D lightingTextureUnit;

in vec2 texCoordV;

void main(){
    colour = texture(geometryTextureUnit, texCoordV) + texture(lightingTextureUnit, texCoordV);
}
