import cv2
import numpy as np
import sys

fname = sys.argv[1]
img = cv2.imread(fname)
cv2.imshow("orig", img)

p0 = 0
p1 = 0.00001
p2 = 0.99999
p3 = 1

def bezier(x):
	val = (1-x)**3 *p0 + 3*x* (1-x)**2 * p1 + 3 * x**2 * (1-x)*p2 + x**3 * p3
	# if (x > 0.55 and x <= 0.65):
		# print "x:", x, "\tf(x):", val
	return val

bezier = np.vectorize(bezier, otypes=[np.float])

def resize(img, factor):
	if factor == 1:
		return img

	h,w,_ = img.shape
	return cv2.resize(img, (h/factor, w/factor))

def eqaulizeHist(img):
	yuv = cv2.cvtColor(img, cv2.COLOR_BGR2YUV)
	yuv[:,:,0] = cv2.equalizeHist(yuv[:,:,0])
	return cv2.cvtColor(yuv, cv2.COLOR_YUV2BGR)

def unsharpMask(img, radius, show=True):
	blurred = cv2.GaussianBlur(img, (radius, radius), 10)
	if show:
		cv2.imshow("blurred", blurred)
	unsharp = cv2.addWeighted(img, 1.3, blurred, -0.3, 0)
	return unsharp

def applyBezierRGB(img):
	return (bezier(img.astype(np.float)/255.0) * 255.0).astype(np.uint8)

def applyBezierS(img):
	hls = cv2.cvtColor(img, cv2.COLOR_BGR2HLS)
	hls = hls.astype(np.float32)/255.0
	hls[:,:,1] = bezier(hls[:,:,1])
	hls = hls * 255.0
	hls = hls.astype(np.uint8)
	bgr = cv2.cvtColor(hls, cv2.COLOR_HLS2BGR)
	return bgr

# img = resize(img, 4)

# img = eqaulizeHist(img)
# cv2.imshow("hist eq",img)

img = unsharpMask(img, 25)
cv2.imshow("unsharp", img)

bgr = applyBezierRGB(img)
cv2.imshow("RGB-S curve", bgr)

hls_s = applyBezierS(img)
cv2.imshow("HLS-S curve", hls_s)

cv2.waitKey(0)