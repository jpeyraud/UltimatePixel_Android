precision mediump float;

// Gamma correction
#define GAMMA (2.2)

const float tau = 6.28318530717958647692;

uniform vec2 iResolution;           // viewport iResolution (in pixels)
uniform float iGlobalTime;          // shader playback time (in seconds)
uniform sampler2D iChannel0;        // random color texture


//original 239c
#define K(a) .5 / length(pow(abs(a),vec2(sin(iGlobalTime*.9)*.5+.6)*3.))
vec4 mainImage( in vec2 g )
{
    vec4 f = vec4(iResolution, iResolution);
    g = (g+g - f.xy)/f.y*2.0;
    g.y -= 0.5;

    vec3 e = vec3(.6,1.2,-1.2);
    f.r = K(g-e.xz);
    f.g = K(g+e.xy);
    f.b = K(g);

    f = smoothstep(f, f * 0.4, vec4(1));

    return f;
}
void main()
{
    gl_FragColor = mainImage(gl_FragCoord.xy);
}