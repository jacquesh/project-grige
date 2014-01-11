#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewingMatrix;

in vec3 position;

void main(){
	gl_Position = projectionMatrix * viewingMatrix * vec4(position,1);
}