#version 330 core

uniform sampler2D normalSampler;

uniform vec4 lightColor;

in vec2 texCoordV;

void main(){
	//Get normal data
	vec3 normalMap = texture2D(normalSampler, texCoordV).rgb;
	
	//Normalize our vectors
	vec3 N = normalize(normalMap * 2.0 - 1.0); //Convert from [0, 1] to [-1, 1] before normalizing
	vec3 L = vec3(0,0,1);
	
	//Pre-multiply light color with intensity
	//Then perform "N dot L" to determine our diffuse term
	vec3 diffuse = (lightColor.rgb * lightColor.a) * max(dot(N, L), 0.0);
	
	gl_FragColor = vec4(diffuse);
}