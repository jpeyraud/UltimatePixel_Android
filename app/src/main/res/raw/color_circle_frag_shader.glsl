precision mediump float;

uniform vec2 iResolution;           // viewport iResolution (in pixels)
uniform float iGlobalTime;           // shader playback time (in seconds)

vec4 mainImage( vec2 fragCoord )
{
    vec2 p = (2.0*fragCoord.xy-iResolution.xy)/iResolution.y;
    float tau = 3.1415926535*2.0;
    float a = atan(p.x,p.y);
    float r = length(p);
    vec2 uv = vec2(a/tau,r);

    //get the color
    float xCol = (uv.x - (iGlobalTime / 3.0)) * 3.0;
    xCol = mod(xCol, 3.0);
    vec3 horColour = vec3(0.25, 0.25, 0.25);

    if (xCol < 1.0) {

        horColour.r += 1.0 - xCol;
        horColour.g += xCol;
    }
    else if (xCol < 2.0) {

        xCol -= 1.0;
        horColour.g += 1.0 - xCol;
        horColour.b += xCol;
    }
    else {

        xCol -= 2.0;
        horColour.b += 1.0 - xCol;
        horColour.r += xCol;
    }

    // draw color beam
    uv = (2.0 * uv) - 1.0;
    float beamWidth = (0.9+1.5*cos(uv.x*10.0*tau*0.15*clamp(floor(5.0 + 10.0*cos(iGlobalTime)), 0.0, 10.0))) * abs(1.0 / (30.0 * uv.y));
    vec3 horBeam = vec3(beamWidth);
    return vec4((( horBeam) * horColour), 1.0);
}

void main()
{
    gl_FragColor = mainImage(gl_FragCoord.xy);
}