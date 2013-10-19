#version 330 core
//layout(location = 0) in vec3 vertexPosition_modelspace;

uniform mat4 projectionMatrix;
uniform mat4 viewingMatrix;
uniform mat4 objectTransform;

in vec3 position;
in vec2 texCoord;

out vec2 texCoordV;

void main(){
	gl_Position = projectionMatrix * viewingMatrix * objectTransform * vec4(position,1);
	//gl_Position = projectionMatrix * vec4(position,1);
	texCoordV = texCoord;
}