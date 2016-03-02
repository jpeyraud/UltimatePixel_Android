#ifdef GL_ES
precision highp float;
#endif

uniform vec2 iResolution;           // viewport iResolution (in pixels)
uniform float iGlobalTime;          // shader playback time (in seconds)

#ifdef GL_ES
precision highp float;
#endif

vec3 hsv2rgb (in vec3 hsv) {
    hsv.yz = clamp (hsv.yz, 0.0, 1.0);
    return hsv.z * (1.0 + 0.5 * hsv.y * (cos (2.0 * 3.14159 * (hsv.x + vec3 (0.0, 2.0 / 3.0, 1.0 / 3.0))) - 1.0));
}

float rand (in vec2 seed) {
    return fract (sin (dot (seed, vec2 (12.9898, 78.233))) * 137.5453);
}

vec4 mainImage (in vec2 fragCoord) {
    vec2 frag = (2.0 * fragCoord.xy - iResolution.xy) / iResolution.y;

    //crazy effect
    //frag *= 2.0 - 0.2 * sin (frag.yx* iGlobalTime/2.0) * cos (3.14159 * 0.5 * iGlobalTime);
    //first effect
    //frag *= 2.0 - 0.4 * cos (frag.yx) * sin (3.14159 * 0.5 * iGlobalTime);
    //current
    //frag *= 2.0 - 0.4 * cos (frag.yx) * sin (3.14159 * 0.5 * iGlobalTime);

    // New big one
    vec2 p = (2.0*fragCoord.xy-iResolution.xy)/iResolution.y;
    float tau = 3.1415926535*2.0;
    float a = atan(p.x,p.y);
    float r = length(p);
    vec2 uv = vec2(a/tau,r);
    float beamWidth = (cos(uv.x*12.0*tau*1.0*clamp(sin(iGlobalTime), 0.0, 10.0))) * abs(1.0 / (30.0 * uv.y));
    vec3 horBeam = vec3(beamWidth);

    frag *= 2.28 - 1.5 * horBeam.xy;

    // Other
    frag *= 5.0;
    float random = rand (floor (frag));

    // square
    vec2 black = smoothstep (1.0, 0.8, cos (frag * 3.14159 * 2.0));

    // color
    vec3 color = hsv2rgb (vec3 (random, 1.0, 1.0));
    color *= black.x * black.y * smoothstep (1.0, 0.0, length (fract (frag) - 0.5));
    color *= 0.5 + 0.5 * cos (random + random * iGlobalTime + iGlobalTime/3.0 + 3.14159 * 0.5);
    return vec4 (color, 1.0);
}

void main()
{
    gl_FragColor = mainImage(gl_FragCoord.xy);
}