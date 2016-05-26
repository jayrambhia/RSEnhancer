#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(elanic.in.rsenhancer)

static int gWidth;
static int gHeight;
float alpha = 1.5;
float beta = -0.5;

void setSize(int w, int h) {
    gWidth = w;
    gHeight = h;

//    rsDebug("setSize done", 0);
//    rsDebug("width", gWidth);
//    rsDebug("height", gHeight);

}

void setAlpha(float val) {
	alpha = val;
}

void setBeta(float val) {
    beta = val;
}

void root(const uchar4* in, uchar4 *out, const void* usrData, uint32_t x, uint32_t y) {

	float4 pIn = rsUnpackColor8888(*in);
	float4 pOut = rsUnpackColor8888(*out);

	pOut.r = pIn.r * alpha + pOut.r * beta;
	pOut.g = pIn.g * alpha + pOut.g * beta;
	pOut.b = pIn.b * alpha + pOut.b * beta;
	pOut.a = 1.0;

	*out = rsPackColorTo8888(pOut);
}