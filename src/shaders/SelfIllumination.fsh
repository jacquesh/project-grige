#version 330 core

uniform sampler2D geometrySampler;
uniform sampler2D selfIlluSampler;

in vec2 texCoordV;

void main(){
	vec4 geometryColour = texture(geometrySampler, texCoordV);
	vec4 selfIlluColour = texture(selfIlluSampler, texCoordV);
    
    gl_FragColor = geometryColour * selfIlluColour;
}
