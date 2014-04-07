#version 330 core

uniform sampler2D geometryTextureUnit;
uniform sampler2D lightingTextureUnit;
uniform sampler2D interfaceTextureUnit;

in vec2 texCoordV;

void main(){
	vec4 geometryColour = texture(geometryTextureUnit, texCoordV);
	vec4 lightingColour = texture(lightingTextureUnit, texCoordV);
	vec4 interfaceColour = texture(interfaceTextureUnit, texCoordV);
    
    vec3 worldColour = (geometryColour.rgb + lightingColour.rgb) * lightingColour.a + interfaceColour.rgb;
    
    gl_FragColor.rgb = interfaceColour.rgb*interfaceColour.a + worldColour*(1-interfaceColour.a);
}
