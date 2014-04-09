#version 330 core

uniform vec3 lightLoc;

in vec3 position;
in vec2 texCoord;

out vec2 texCoordV;

void main(){
	gl_Position = vec4(position.xyz, 1);
	
	texCoordV = texCoord;
}
