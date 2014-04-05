#define PI 3.1415926535897932384626433832795
#define leeway 0.15707963267948966192

#version 330 core
layout(location = 0) out vec4 colour;

uniform vec3 lightLoc;

uniform vec3 lightColour;
uniform float intensity;

uniform float spotAngle;

void main(){
	float angle = atan(gl_FragCoord.y-lightLoc.y, gl_FragCoord.x-lightLoc.x);
	float compareAngle = 2*abs(angle);
	
	if(compareAngle > spotAngle+leeway)
	{
		colour = vec4(0,0,0,1);
	}
	else if(compareAngle > spotAngle-leeway)
	{
		float lerp = 0.5f - (compareAngle - spotAngle)/(2*leeway);
		
		colour = intensity*lerp*vec4(lightColour, 1);
	}
	else
	{
		colour = intensity * vec4(lightColour, 1);
	}
}
