#version 330 core
layout(location = 0) out vec4 colour;

uniform sampler2D geometryTextureUnit;
uniform sampler2D lightingTextureUnit;

in vec2 texCoordV;

void main(){
	vec4 geometryColour = texture(geometryTextureUnit, texCoordV);
	vec4 lightingColour = texture(lightingTextureUnit, texCoordV);
    
    //colour = geometryColour + lightingColour;
    
    colour = geometryColour * lightingColour.a; //vec4(geometryColour.rgb, lightingColour.a);
}
