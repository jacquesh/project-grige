#version 330 core

uniform sampler2D lightingTextureUnit;
uniform sampler2D interfaceTextureUnit;

in vec2 texCoordV;

void main(){
	vec4 lightingColour = texture(lightingTextureUnit, texCoordV);
	vec4 interfaceColour = texture(interfaceTextureUnit, texCoordV);
    
    gl_FragColor.rgb = interfaceColour.rgb*interfaceColour.a + lightingColour.rgb*(1-interfaceColour.a);
}
