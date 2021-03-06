#version 330 core

uniform sampler2D normalSampler;

uniform vec2 resolution;

uniform vec3 falloff;

uniform vec3 lightLoc;
uniform vec4 lightColor;

in vec2 texCoordV;

void main(){
	//Get input data
	vec3 normalMap = texture2D(normalSampler, texCoordV).rgb;
	
	//Compute the vector from the light to the current fragment
	vec3 lightDir = vec3((lightLoc.xy - gl_FragCoord.xy)/resolution.xy, -lightLoc.z/10);
	
	//Determine distance (for attenuation) BEFORE we normalize our lightDir
	float lightOffset = length(lightDir);
	
	//Normalize our vectors
	vec3 N = normalize(normalMap * 2.0 - 1.0); //Convert from [0, 1] to [-1, 1] before normalizing
	vec3 L = normalize(lightDir);
	
	//Pre-multiply light color with intensity
	//Then perform "N dot L" to determine our diffuse term
	vec3 diffuse = (lightColor.rgb * lightColor.a) * max(dot(N, L), 0.0);
	
	//calculate attenuation
	float attenuation = 1.0/(falloff.x + (falloff.y*lightOffset) + (falloff.z*lightOffset*lightOffset));
	
	gl_FragColor = vec4(diffuse * attenuation, 1);
}
