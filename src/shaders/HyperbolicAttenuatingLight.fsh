#version 330 core

uniform vec3 lightColour;
uniform float intensity;

uniform vec3 lightLoc;
uniform float radius;

void main(){
	float distanceToLight = length(gl_FragCoord.xy - lightLoc.xy);
	float attenuation = intensity/(1 + 2*distanceToLight/radius);

	gl_FragColor = attenuation * vec4(lightColour, 1);
}
