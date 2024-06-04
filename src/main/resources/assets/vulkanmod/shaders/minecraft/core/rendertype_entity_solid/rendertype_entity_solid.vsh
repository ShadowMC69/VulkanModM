#version 460

#include "light.glsl"

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;
layout(location = 3) in ivec2 UV1;
layout(location = 4) in ivec2 UV2;
layout(location = 5) in vec3 Normal;

layout(binding = 0) uniform readonly UniformBufferObject {
   mat4 MVP[8];
};
//Exploit aliasing and allow new Uniforms to overwrite the prior content: reducing required PushConstant Range
layout(push_constant) readonly uniform  PushConstant
{
    vec3 Light0_Direction;
    vec3 Light1_Direction;
};

layout(binding = 2) uniform sampler2D Sampler2[];

layout(location = 0) out invariant flat uint baseInstance;
layout(location = 1) out vec4 vertexColor;
layout(location = 2) out vec4 lightMapColor;
layout(location = 3) out vec4 overlayColor;
layout(location = 4) out vec2 texCoord0;

void main() {
    gl_Position = MVP[gl_BaseInstance& 7] * vec4(Position, 1.0);
    baseInstance = gl_BaseInstance>>16;


    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = texelFetch(Sampler2[0], UV2 / 16, 0);
    overlayColor = texelFetch(Sampler2[1], UV1, 0);
//     overlayColor = vec4(1.0f);
    texCoord0 = UV0;
    //normal = (MVP * vec4(Normal, 0.0)).xyz;
}
/*
#version 150

#moj_import <light.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;

void main() {
    gl_Position = MVP * vec4(Position, 1.0);


    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;
    normal = MVP * vec4(Normal, 0.0);
} */
