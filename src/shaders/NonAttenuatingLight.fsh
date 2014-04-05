#version 330 core
layout(location = 0) out vec4 colour;

uniform vec3 lightColour;
uniform float intensity;

void main(){
	colour = intensity * vec4(lightColour, 1);
}
