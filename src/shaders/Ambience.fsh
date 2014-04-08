#version 330 core

uniform vec4 ambientLight;

void main(){
    gl_FragColor = vec4(ambientLight.rgb * ambientLight.a, 0);
}
