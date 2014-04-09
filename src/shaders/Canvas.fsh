#version 330 core

uniform sampler2D geometryTextureUnit;
uniform sampler2D lightingTextureUnit;
uniform sampler2D selfIlluTextureUnit;
uniform sampler2D interfaceTextureUnit;

uniform vec4 ambientLight;

in vec2 texCoordV;

void main(){
	vec4 geometryColour = texture(geometryTextureUnit, texCoordV);
	vec4 lightingColour = texture(lightingTextureUnit, texCoordV);
	vec4 selfIlluColour = texture(selfIlluTextureUnit, texCoordV);
	vec4 interfaceColour = texture(interfaceTextureUnit, texCoordV);
	
	vec3 light = ambientLight.rgb * ambientLight.a + lightingColour.rgb;
	light = max(light, selfIlluColour.rgb);
	
	vec3 finalColour = geometryColour.rgb * light;
    
	gl_FragColor.rgb = interfaceColour.rgb*interfaceColour.a + finalColour.rgb*(1-interfaceColour.a);
}
