#version 330 core
layout(location = 0) out vec4 colour;

uniform mat4 projectionMatrix;
uniform mat4 viewingMatrix;
uniform vec2 screenSize;

uniform vec3 lightColour;
uniform float intensity;

uniform vec2 lightLoc;
uniform float radius;

void main(){
	//We first need to get the light co-ords into the same scale/system as gl_FragCoord IE from (-1, 1) to (0, screenSize)
	vec2 lightFragLoc = lightLoc + vec2(1,1);
	lightFragLoc *= screenSize/2;
	
	float distanceToLight = length(gl_FragCoord.xy - lightFragLoc);
	float attenuationMultiplier = 1 - distanceToLight/radius;

	float fragIntensity = intensity * attenuationMultiplier;

	colour = vec4(lightColour, fragIntensity);
}
