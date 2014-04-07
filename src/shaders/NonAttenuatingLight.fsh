#version 330 core

uniform vec3 lightColour;
uniform float intensity;

void main(){
	gl_FragColor = intensity * vec4(lightColour, 1);
}
