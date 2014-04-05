#version 330 core
layout(location = 0) out vec4 colour;

uniform vec3 lightColour;
uniform float intensity;

uniform vec3 lightLoc;
uniform float radius;

void main(){
	float distanceToLight = length(gl_FragCoord.xy - lightLoc.xy);
	float attenuation = intensity/(1 + 2*distanceToLight/radius);

	colour = attenuation * vec4(lightColour, 1);
}
