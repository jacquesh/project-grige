#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewingMatrix;
uniform mat4 objectTransform;

in vec3 position;

out vec4 vertColourV;

void main(){
	gl_Position = projectionMatrix * viewingMatrix * objectTransform * vec4(position,1);
}
