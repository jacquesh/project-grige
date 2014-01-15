#version 330 core
layout(location = 0) out vec4 colour;

uniform mat4 projectionMatrix;
uniform mat4 viewingMatrix;

uniform vec3 lightColour;
uniform float intensity;

uniform vec2 lightLoc;
uniform float radius;

void main(){
	float distanceToLight = length(gl_FragCoord.xy - lightLoc);
	float attenuationMultiplier = 1 - distanceToLight/radius;

	float fragIntensity = intensity * attenuationMultiplier;

	colour = vec4(lightColour, fragIntensity);
}
