 attribute vec4 aPosition;
 attribute vec4 aTexCoord;
 varying vec2 vTexCoord;
 varying vec2 vTexCoord2;
 uniform mat4 uMatrix;
 uniform mat4 uSTMatrix;
 void main() {
 vTexCoord = aTexCoord.xy;//(uSTMatrix * aTexCoord).xy;
 gl_Position = aPosition;
}