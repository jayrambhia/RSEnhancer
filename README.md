## RSEnhancer

A demo project to show that Renderscript can be used to process and enhance images very effectively. This app lets you change `Brightness` and `Contrast` of an image using Rendescript. This app also provides an option to **Auto Enhance** the image. It tries to mimic Instagram's Lux feature (and fails horribly).

### AutoEnhance

 - Use unsharp mask method to make image sharper
 - Implement a S-curve on RGB to increase contrast and get better colors (I tried applying S-curve on Saturation of HLS but RGB gives better results)
 - I also tried histogram equalization on Y channel of YUV but it doesn't go with two other methods so chucked it at the end.

### Features
 
 - It tries to copy Google Photos' editing UI with animations and reveal ripple (I implemented them myself)
 - Change brightness and contrast. Code taken from https://github.com/LouisPeng/RenderScript-ImageFilter
 - Auto Enhancement. Algorithm taken from [here](http://photo.stackexchange.com/a/38572)
 - Histogram Equalization(Not used). Referrenced from [here](https://medium.com/@qhutch/android-simple-and-fast-image-processing-with-renderscript-2fa8316273e1)