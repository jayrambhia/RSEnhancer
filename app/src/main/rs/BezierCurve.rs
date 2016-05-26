#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(elanic.in.rsenhancer)

static int gWidth;
static int gHeight;

static float p0 = 0;
static float p1 = 0.000001;
static float p2 = 0.999999;
static float p3 = 1;

static float cubicBezier(float x);
static float4 cubicBezierF(float4 f);

void setSize(int w, int h) {
    gWidth = w;
    gHeight = h;
}

void root(const uchar4* in, uchar4 *out, const void* usrData, uint32_t x, uint32_t y) {

	float4 pIn = rsUnpackColor8888(*in);
	float4 pOut = rsUnpackColor8888(*out);

	pOut.r = cubicBezier(pIn.r);
	pOut.g = cubicBezier(pIn.g);
	pOut.b = cubicBezier(pIn.b);
	pOut.a = 1.0;

    // This takes a lot more time
	//pOut = cubicBezierF(pIn);

	*out = rsPackColorTo8888(pOut);
}

static float cubicBezier(float x) {
    //return pow(1-x,3)*p0 + 3*x*pow(1 - x,2)*p1 + 3*x*x*(1-x)*p2 + x*x*x*p3;
    // optimize
    // return x * pow(1-x, 2) * 0.00003 + 2.9997 * pow(x, 2) * (1-x) + pow(x,3);

    // more optimize
    return -1.995 * pow(x, 3) + 2.991 * pow(x, 2);
}

static float4 cubicBezierF(float4 f) {
    return f * pow(1-f, 2) * 0.00003 + 2.9997 * pow(f, 2) * (1-f) + pow(f,3);
}
