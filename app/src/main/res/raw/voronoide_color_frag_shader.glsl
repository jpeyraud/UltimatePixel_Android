precision highp float;

// Gamma correction
#define GAMMA (2.2)

const float tau = 6.28318530717958647692;

uniform vec2 iResolution;           // viewport iResolution (in pixels)
uniform float iGlobalTime;          // shader playback time (in seconds)
uniform sampler2D iChannel0;        // random color texture

#define ANIMATE

vec2 hash2( vec2 p )
{
    // texture based white noise
    //return vec2(fract(sin(dot(p.xy ,vec2(12.9898,78.233))) * 43758.5453));

    // Tanguy's Art
    //return fract(vec2(dot(p,vec2(iGlobalTime*0.00005)),dot(p,vec2(iGlobalTime*0.000000001)))*iGlobalTime*0.20);

    // procedural white noise
    return fract(sin(vec2(dot(p,vec2(127.1,311.7)),dot(p,vec2(269.5,183.3))))*43758.5453);
}



vec3 voronoi( in vec2 x )
{
    vec2 n = floor(x);
    vec2 f = fract(x);

    //----------------------------------
    // first pass: regular voronoi
    //----------------------------------
    vec2 mg, mr;

    float md = 8.0;
    for( int j=-1; j<=1; j++ )
        for( int i=-1; i<=1; i++ )
        {
            vec2 g = vec2(float(i),float(j));
            vec2 o = hash2( n + g );
#ifdef ANIMATE
            o = 0.5 + 0.5*sin( iGlobalTime + 6.2831*o );
#endif
            vec2 r = g + o - f;
            float d = dot(r,r);

            if( d<md )
            {
                md = d;
                mr = r;
                mg = g;
            }
        }

    //----------------------------------
    // second pass: distance to borders
    //----------------------------------
    md = 8.0;
    for( int j=-1; j<=1; j++ )
        for( int i=-1; i<=1; i++ )
        {
            vec2 g = mg + vec2(float(i),float(j));
            vec2 o = hash2( n + g );
#ifdef ANIMATE
            o = 0.5 + 0.5*sin( iGlobalTime + 6.2831*o );
#endif
            vec2 r = g + o - f;

            if( dot(mr-r,mr-r)>0.00001 )
                md = min( md, dot( 0.5*(mr+r), normalize(r-mr) ) );
        }

    return vec3( md, mr );
}

vec4 mainImage( in vec2 fragCoord )
{
    vec2 p = fragCoord.xy/iResolution.xx;
    vec3 c = voronoi( 4.0*p );
    vec3 color = vec3(0.05,0.05,0.4);

    // isolines
    vec3 col = c.x*(0.5 + 0.5*sin(64.0*c.x))*vec3(1.0);
    // borders
    col = mix( color, col, smoothstep( 0.04, 0.07, c.x ) );
    // feature points
    float dd = length( c.yz );
    col = mix( color, col, smoothstep( 0.0, 0.12, dd) );
    col += color*(1.0-smoothstep( 0.0, 0.04, dd));

    return vec4(col,1.0);
}

void main()
{
    gl_FragColor = mainImage(gl_FragCoord.xy);
}