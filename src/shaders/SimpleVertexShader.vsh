#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewingMatrix;
uniform mat4 objectTransform;

in vec3 position;
in vec2 texCoord;
in vec4 tintColour;

out vec2 texCoordV;
out vec4 vertColour;

void main(){
	gl_Position = projectionMatrix * viewingMatrix * objectTransform * vec4(position,1);
	texCoordV = texCoord;
	vertColour = tintColour;
}