#version 330 core

uniform vec3 lightLoc;

in vec3 position;

void main(){
	gl_Position = vec4(position.xyz, 1);
}
