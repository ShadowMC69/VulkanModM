#version 460

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec4 Color;
layout(location = 3) in vec3 Normal;

layout(binding = 0) uniform UniformBufferObject {
   mat4 MVP[32];


};

layout(location = 0) out vec4 vertexColor;
layout(location = 1) out vec2 texCoord0;
layout(location = 2) out float vertexDistance;

void main() {
    gl_Position = MVP[gl_BaseInstance & 31] * vec4(Position, 1.0)
;

    texCoord0 = UV0;
    vertexDistance = length((MVP[(gl_BaseInstance+1) & 31] * vec4(Position, 1.0)).xyz);
    vertexColor = Color;
    //normal = (MVP * vec4(Normal, 0.0)).xyz;
}


