#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 vTexCoord;
uniform samplerExternalOES sTexture;
uniform sampler2D sTexture2;
uniform float displayVideo;

uniform vec4 uInputColor;
void main() {
    vec4 leftVideoColor;
    vec4 rightVideoColor;

    vec2 coordinateLeft;
    coordinateLeft = vTexCoord;
    coordinateLeft.x = coordinateLeft.x*0.5;
    leftVideoColor = texture2D(sTexture, coordinateLeft);//视频帧纹理(左图）

    vec2 coordinateRight;
    coordinateRight = vTexCoord;
    coordinateRight.x = vTexCoord.x*0.5 + 0.5;
    rightVideoColor = texture2D(sTexture, coordinateRight);//视频帧纹理(右图）

    /*黑白图的本质就是一个由各个像素系数组成的集合*/
    vec4 blackWhiteColor = texture2D(sTexture2, vTexCoord);//黑白图纹理
    vec4 finalColor;
    if (displayVideo > 0.5) {
        //在大多数shader语言里向量的四则运算都是逐个分量的四则运算，和线性代数里定义的不一样。（但矩阵与向量的乘法符合线代定义）
        finalColor = leftVideoColor*blackWhiteColor+ rightVideoColor*vec4((1.0-blackWhiteColor.r), (1.0-blackWhiteColor.g), (1.0-blackWhiteColor.b), 1.0);
    } else {
        finalColor = blackWhiteColor;
    }

    gl_FragColor = finalColor*uInputColor;
    //其他


}

