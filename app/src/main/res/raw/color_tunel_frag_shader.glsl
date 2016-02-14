precision mediump float;

#define PI 3.141592653

uniform vec2 iResolution;           // viewport iResolution (in pixels)
uniform float iGlobalTime;           // shader playback time (in seconds)
//varying vec2 v_UV;


vec3 pattern(vec2 c, vec2 p)
{
    float ang = atan(p.y-c.y,p.x-c.x);
    float d = distance(c,p);
    float rd = d;
    if (rd < 0.04) return vec3(0.1,0.1,0.1);
    d = 0.05/(d*d) + iGlobalTime;


    if (mod(d,0.4) <= 0.2) {
        ang += PI/20.;
    }

    if (mod(d,1.0) <= 0.5) return vec3(0.1,0.1,0.1);

    if (mod(d,2.0) <= 1.0) ang += iGlobalTime *0.4;
    else ang -= iGlobalTime *0.4;
    if ( mod(ang ,PI/10.) < PI/20.) return vec3(0.1,0.1,0.1);

    return vec3(sin(d ),cos(d),sin(d)-cos(d));
}

vec4 mainImage( vec2 fragCoord ) {
    vec2 c = vec2(0.5,0.5);
    vec2 p = fragCoord.xy / iResolution.xy;
    p.x *= iResolution.x/ iResolution.y;
    c.x *= iResolution.x/ iResolution.y;

    float d = distance(c,p);

    return vec4( pattern(c,p), 1.0 );

}
vec4 mainImage2(vec2 fragCoord)
{
    vec2 uv = fragCoord.xy / iResolution.xy;
    return vec4(uv,0.5+0.5*sin(iGlobalTime),1.0);
}
void main()
{
    gl_FragColor = mainImage(gl_FragCoord.xy);
}