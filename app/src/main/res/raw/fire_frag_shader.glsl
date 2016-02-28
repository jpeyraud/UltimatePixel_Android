precision highp float;

uniform vec2 iResolution;           // viewport iResolution (in pixels)
uniform float iGlobalTime;          // shader playback time (in seconds)
uniform sampler2D iChannel0;        // random color texture
uniform float u_Progress;


#define ITERATIONS 9
#define SPEED 12.0
#define DISPLACEMENT 0.2
#define TIGHTNESS 5.0
#define YOFFSET 0.8
#define YSCALE 0.8
#define FLAMETONE vec3(50.0, 5.0, 1.0)


float hash( float n )
{
    return fract(sin(n)*43758.5453);
}
float noise( vec3 x )
{
    // The noise function returns a value in the range -1.0f -> 1.0f
    vec3 p = floor(x);
    vec3 f = fract(x);

    f       = f*f*(3.0-2.0*f);
    float n = p.x + p.y*57.0 + 113.0*p.z;

    return mix(mix(mix( hash(n+0.0), hash(n+1.0),f.x),
                   mix( hash(n+57.0), hash(n+58.0),f.x),f.y),
               mix(mix( hash(n+113.0), hash(n+114.0),f.x),
                   mix( hash(n+170.0), hash(n+171.0),f.x),f.y),f.z);
}

float shape(in vec2 pos) // a blob shape to distord
{
    return clamp( sin(pos.x*3.1416) - pos.y+(u_Progress-YOFFSET), 0.0, 1.0 );
}

vec4 mainImage( in vec2 fragCoord )
{
    vec2 uv = fragCoord.xy / vec2(iResolution.x*1.25,iResolution.y);
    float nx = -0.27;
    float ny = 0.0;
    for (int i=1; i<ITERATIONS+1; i++)
    {
        float ii = pow(float(i), 2.0);
        float ifrac = float(i)/float(ITERATIONS);
        float t = ifrac * iGlobalTime * SPEED;
        float d = (1.0-ifrac) * DISPLACEMENT;
        float noise = noise( vec3(uv.x*ii-iGlobalTime*ifrac, uv.y*YSCALE*ii-t, 0.0)) * d;
        nx += noise ;
        ny += noise ;
    }
    float flame = shape( vec2(uv.x+nx, uv.y+ny) );
    vec3 col = pow(flame, TIGHTNESS) * FLAMETONE;

    // tonemapping
    col = col / (1.0+col);
    col = pow(col, vec3(1.0/2.2));
    col = clamp(col, 0.0, 1.0);

    return vec4(col, 1.0);
}
void main()
{
    gl_FragColor = mainImage(gl_FragCoord.xy);
}