precision highp float;

uniform vec2 iResolution;           // viewport iResolution (in pixels)
uniform float iGlobalTime;          // shader playback time (in seconds)

float myRand (in vec2 seed) {
    seed = fract (seed * vec2 (5.3983, 5.4427));
    seed += dot (seed.yx, seed.xy + vec2 (21.5351, 14.3137));
    return fract (seed.x * seed.y * 95.4337);
}
float erf(float x) {
    float s = sign(x), a = abs(x);
    x = 1.0 + (0.278393 + (0.230389 + 0.078108 * (a * a)) * a) * a;
    x *= x;
    return s - s / (x * x);
}
vec3 pixelImage(int x,int y,vec3 lg){
    int dx = x-24;
    int dy = y-18;
    if(dx<0)dx=-dx;
    if(dy<0)dy=-dy;
    if(dx<10 && dy<15){
        float xu = float(x-24)/10.0;
        lg=normalize(lg);
        if(dy>12){
            vec3 b = vec3(1.0,0.4,0.0);
            vec3 n = vec3(xu,sqrt(1.0-xu*xu),0);
            if(y==5 || y==32)n.z=0.2;
            n=normalize(n);
            vec3 diff = b*(dot(n,lg)+0.8)/2.3;
            vec3 lig = vec3(1,1,1)*pow(max(0.0,dot(reflect(vec3(0,-1,0),n),lg)),2.0)/3.0;
            return diff+lig;
        }else{
            float yi = float(y-18)/12.0;
            float dist = 1.0-exp(-yi*yi*7.5*16.0/25.0)*0.85;
            if(dist>abs(xu)){
                float fra = xu/dist;
                vec3 n = vec3(fra,0,0);
                n.y=sqrt(1.0-n.x*n.x);
                n.z=-sin(yi*3.1415926535);
                n=normalize(n);
                float spec = 0.0;
                spec += max(0.0,dot(n,lg)*0.9+0.1)/1.6;
                if(dist<=abs(xu)+0.1)spec+=0.3;
                spec += pow(max(0.0,dot(reflect(vec3(0,-1,0),n),lg)),3.0)/5.0;
                spec += abs(yi)*pow(max(0.0,dot(reflect(vec3(0,-1,0),n),vec3(sqrt(2.0)/2.0*lg.x*5.0,sqrt(2.0)/2.0,lg.z))),2.0)/8.0;

                vec3 sand = mix(vec3(0.3,0.2,0.0),vec3(0.9,0.8,0.5),myRand(vec2(float(x-24)*sign(yi),abs(yi*400.0)))*0.5+0.5);

                float time = mod(iGlobalTime/30.0,1.0);
                float wMax=dist;
                if(x!=24 || yi > 0.0 || abs(mod(time*120.0,1.0)+yi) > 0.08){
                    float A = 0.0714648/2.0;
                    float C = -4.12596*erf(0.365148*yi)+1.23993*erf(0.516398*yi)+yi+0.0357324;
                    if(yi<0.0)wMax = min(wMax,max(0.0,pow((C-time*A+0.005)*32.0,1.0/1.4)));
                    C+=pow(abs(xu),1.4)/32.0-0.005;
                    if(yi<0.0 && time<C/A)sand=vec3(1,1,1);
                    C = 4.12596*erf(0.365148*yi)-1.23993*erf(0.516398*yi)-yi+0.0357324;
                    if(yi>0.0 && time>C/A)sand=vec3(1,1,1);
                }
                if(sand.z<0.9){
                    float is = xu/wMax;
                    vec3 n2 = vec3(is,sqrt(1.0-is*is),0.0);
                    sand *= (max(0.0,dot(reflect(vec3(0,-1,0),n2),lg))/1.5+1.5)*0.8;
                }
                if(dist<=abs(xu)+0.1)sand=(vec3(1,1,1)+sand)/2.0;
                return vec3(spec)*sand;
            }else return vec3(0.2,0.2,0.2);
        }
    }
    return vec3(0.2,0.2,0.2);
}
vec3 imageFilter(vec2 pos, float rot){
    vec3 lg = vec3(0.2,0.4,0.15);
    lg.xz*=cos(rot);
    vec3 color = pixelImage(int(pos.x*16.0+24.5),int(pos.y*16.0+18.5),lg);
    vec2 di = mod(pos+1.0/32.0,1.0/16.0)*16.0-0.5;
    color *= pow(0.5-abs(di.x),0.2)+pow(0.5-abs(di.y),0.2);

	vec2 du = vec2(0,0);
    du.x = di.x * cos(rot) - di.y * sin(rot);
    du.y = di.x * sin(rot) + di.y * cos(rot);

   /* float C = cos(rot), S = sin(rot);
    vec2 du = di*mat2(C,-S,S,C);*/

    if(du.y>0.0)color*=0.8;
    float le = length(pos)/2.0;
    color *= pow(1.0/sqrt(le*le+1.0),4.0);
    return color;
}
float ease(float t){
    float s=1.0;
    t+=1.0;
    if(t<1.0){
        return (t*t*((s+1.0)*t-s))-1.0;
    }else{
        t-=2.0;
        return (t*t*((s+1.0)*t+s)+2.0)-1.0;
    }
}
vec3 getColor(vec2 pos){

    vec2 uv = pos.xy / iResolution.xy;
    uv*=3.0;
    uv-=1.5;
    uv.x*=iResolution.x/iResolution.y;

    /*vec2 R = iResolution.xy,
            uv = (2.*pos.xy - R)/R.y;*/

    float rotAngle = 0.0;
    float tM = mod(iGlobalTime/30.0,1.0);
    if(tM<0.05)rotAngle=-1.0+ease(tM*20.0);
    if(tM>0.95)rotAngle=+1.0+ease((tM-1.0)*20.0);
    rotAngle*=3.141592/2.0;
    uv+=1.0/32.0;
    /*
	vec2 q = uv;
    q.x = uv.x * cos(rotAngle) - uv.y * sin(rotAngle);
    q.y = uv.x * sin(rotAngle) + uv.y * cos(rotAngle);
	*/
    float C = cos(rotAngle), S = sin(rotAngle);
    vec2 q = uv*mat2(C,-S,S,C);

    return imageFilter(q,-rotAngle);
}
vec4 mainImage( in vec2 fragCoord ){
    vec3 c = vec3(0,0,0);
    vec2 s = vec2(0.2,0);
    c += getColor(fragCoord);
    return vec4(c,1.0);
}
void main()
{
    gl_FragColor = mainImage(gl_FragCoord.xy);
}