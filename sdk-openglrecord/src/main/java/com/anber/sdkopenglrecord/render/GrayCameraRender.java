package com.anber.sdkopenglrecord.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.anber.sdkopenglrecord.R;
import com.anber.sdkopenglrecord.YuvUtils;
import com.goldze.base.utils.FileUtils;
import com.goldze.base.utils.GlUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 从openglstudy/camera_filter_watermark中复制
 * 带灰色蒙层，水印
 */
public class GrayCameraRender implements GLSurfaceView.Renderer {
    private SurfaceTexture mSurfaceTexture; //使用共享，在应用层创建，并传进来获取摄像头数据
    private int mTextureID; //预览Camera画面对应的纹理id，通过该id画图
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private int mBitmapWidth; //水印图片的宽高，用来保证图片不变形
    private int mBitmapHeight;

    public GrayCameraRender(Context context, SurfaceTexture surfaceTexture, int textureID) {
        this.mSurfaceTexture = surfaceTexture;
        this.mTextureID = textureID;
        this.mContext = context;
    }

    /*****************画水印的数据***********************/
    private int mWaterMarkTextureID;
    private int mWaterMarkProgram;
    private int mWarterMarkPositionHandler;
    private int mWarterMarkTextureCoordHandler;
    private int mWarterMarkMVPMatrixHandler;
    private FloatBuffer mWaterMarkVertexBuffer;
    private FloatBuffer mWaterMarkTextureCoordBuffer;
    private float mWMVertices[] = {
            -1.0f, -1.0f, //左下
            1.0f, -1.0f, //右下
            -1.0f, 1.0f, //左上
            1.0f, 1.0f, //右上
    };
    private float mWMTextureCoords[] = {
            0.0f, 1.0f, //左下
            1.0f, 1.0f, //右下
            0.0f, 0.0f, //左上
            1.0f, 0.0f, //右上
    };
    private float[] mWaterMarkMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    /***************************相机所需要的相关参数，整合在这里*************************************/
    private FloatBuffer mCameraVertexBuffer; // 顶点缓存
    private FloatBuffer mCameraTextureCoordsBuffer; // 纹理坐标映射缓存
    private ShortBuffer mCameradrawListBuffer; // 绘制顺序缓存
    private int mCameraProgram; // OpenGL 可执行程序
    private int mCameraVertexLocation;
    private int mCameraTextureCoordLocation; //纹理坐标对应位置
    private int mCameraMatrixLocation;
    private int mCameraTextureLocation; //纹理对应位置

    private short mCameraDrawOrder[] =
            {0, 2, 1, 0, 3, 2}; // 绘制顶点的顺序

    private final int COORDS_PER_VERTEX = 2; // 每个顶点的坐标数
    private float mCameraVertices[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,
    };
    private float mCameraTextureCoords[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 0.9f,
            0.0f, 0.9f,
    };
    private float[] mCameraMatrix = {
            -1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, -1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    /*****************画灰度数据*********************/
    private int mGrayProgram;
    private int mGrayVertexLocation;
    private int mGrayCoordLocation;
    private int mGrayMatrixLocation;
    private FloatBuffer mGrayVertexBuffer;
    private FloatBuffer mGrayCoordBuffer;
    private float[] mGrayVertices = {
            -1.0f, -1.0f, //左下
            1.0f, -1.0f, //右下
            -1.0f, 1.0f, //左上
            1.0f, 1.0f, //右上
    };
    private float[] mGrayTextureCoords = {
            0.0f, 1.0f, //左下
            1.0f, 1.0f, //右下
            0.0f, 0.0f, //左上
            1.0f, 0.0f, //右上
    };
    private float[] mGrayMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    /*****************在gpu中rgb转yuv******************************/
    private int mYuvProgram;
    private int mYuvVertexLocation;
    private int mYuvTextureCoordLocation;
    private int mYuvMatrixLocation;
    private int mYuvTextureLocation;
    private int mYuvWidthLocation;
    private int mYuvheightLocation;

    private FloatBuffer mYuvVertexBuffer;
    private FloatBuffer mYuvCoordBuffer;
    private float[] mYuvVertices = {
            -1.0f, -1.0f, //左下
            1.0f, -1.0f, //右下
            -1.0f, 1.0f, //左上
            1.0f, 1.0f, //右上
    };
    private float[] mYuvTextureCoords = {
            0.0f, 1.0f, //左下
            1.0f, 1.0f, //右下
            0.0f, 0.0f, //左上
            1.0f, 0.0f, //右上
    };
    private float[] mYuvMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    /*****************渲染到屏幕上用的last数据*************************/
    private int mLastProgram;
    private int mLastVertexLocation;
    private int mLastCoordLocation;
    private int mLastMatrixLocation;
    private FloatBuffer mLastVertexBuffer;
    private FloatBuffer mLastCoordBuffer;
    private float[] mLastVertices = {
            -1.0f, -1.0f, //左下
            1.0f, -1.0f, //右下
            -1.0f, 1.0f, //左上
            1.0f, 1.0f, //右上
    };
    private float[] mLastTextureCoords = {
            0.0f, 1.0f, //左下
            1.0f, 1.0f, //右下
            0.0f, 0.0f, //左上
            1.0f, 0.0f, //右上
    };
    private float[] mLastMatrix = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); //清理屏幕,设置屏幕为白板
        /***********先画相机*****************/
        //(1)根据vertexShader，fragmentShader设置绘图程序
        String vertexShader = FileUtils.readTextFileFromResource(mContext, R.raw.camera_vertex_shader);
        String fragmentShader = FileUtils.readTextFileFromResource(mContext, R.raw.camera_fragment_shader);
        mCameraProgram = GlUtil.createProgram(vertexShader, fragmentShader);
        //(2)获取gl程序中参数，进行赋值
        mCameraVertexLocation = GLES20.glGetAttribLocation(mCameraProgram, "vPosition");
        mCameraTextureCoordLocation = GLES20.glGetAttribLocation(mCameraProgram, "inputTextureCoordinate");
        mCameraMatrixLocation = GLES20.glGetUniformLocation(mCameraProgram, "uMVPMatrix");
        mCameraTextureLocation = GLES20.glGetUniformLocation(mCameraProgram, "s_texture");

        //(3)初始化显示的顶点等坐标，在这些坐标范围内显示相机预览数据?
        mCameraVertexBuffer = GlUtil.createFloatBuffer(mCameraVertices);
        mCameraTextureCoordsBuffer = GlUtil.createFloatBuffer(mCameraTextureCoords);
        mCameradrawListBuffer = GlUtil.createShortBuffer(mCameraDrawOrder);
        /*****************画水印的数据***********************/
        int[] temp = GlUtil.createImageTexture(mContext, R.mipmap.logo); //可以获取textureid，宽高
        mWaterMarkTextureID = temp[0];
        mBitmapWidth = temp[1];
        mBitmapHeight = temp[2];

        String wmVertexShader = FileUtils.readTextFileFromResource(mContext, R.raw.logo_vertex_shader);
        String wmFragmentShader = FileUtils.readTextFileFromResource(mContext, R.raw.logo_fragment_shader);
        mWaterMarkProgram = GlUtil.createProgram(wmVertexShader, wmFragmentShader);
        mWarterMarkPositionHandler = GLES20.glGetAttribLocation(mWaterMarkProgram, "aPosition");
        mWarterMarkTextureCoordHandler = GLES20.glGetAttribLocation(mWaterMarkProgram, "aCoord");
        mWarterMarkMVPMatrixHandler = GLES20.glGetUniformLocation(mWaterMarkProgram, "uMatrix");

        mWaterMarkVertexBuffer = GlUtil.createFloatBuffer(mWMVertices);
        mWaterMarkTextureCoordBuffer = GlUtil.createFloatBuffer(mWMTextureCoords);
        /****************画滤镜数据（灰度）**********************/
        String grayVertexShader = FileUtils.readTextFileFromResource(mContext, R.raw.gray_vertex_shader);
        String grayFragmentShader = FileUtils.readTextFileFromResource(mContext, R.raw.gray_fragment_shader);
        mGrayProgram = GlUtil.createProgram(grayVertexShader, grayFragmentShader);
        mGrayVertexLocation = GLES20.glGetAttribLocation(mGrayProgram, "aPosition");
        mGrayCoordLocation = GLES20.glGetAttribLocation(mGrayProgram, "aCoord");
        mGrayMatrixLocation = GLES20.glGetUniformLocation(mGrayProgram, "uMatrix");

        mGrayVertexBuffer = GlUtil.createFloatBuffer(mGrayVertices);
        mGrayCoordBuffer = GlUtil.createFloatBuffer(mGrayTextureCoords);

        /***************准备rgb转yuv数据**********************/
        //仍然是画texture，根据rgb调整每一个点的值，存储yuv值
        String yuvVertexShader = FileUtils.readTextFileFromResource(mContext, R.raw.yuv_vertex_shader);
        String yuvFragmentShader = FileUtils.readTextFileFromResource(mContext, R.raw.yuv_fragment_shader);
        mYuvProgram = GlUtil.createProgram(yuvVertexShader, yuvFragmentShader);
        mYuvVertexLocation = GLES20.glGetAttribLocation(mYuvProgram, "aPosition");
        mYuvTextureCoordLocation = GLES20.glGetAttribLocation(mYuvProgram, "aCoord");
        mYuvMatrixLocation = GLES20.glGetUniformLocation(mYuvProgram, "uMatrix");
        mYuvTextureLocation = GLES20.glGetAttribLocation(mYuvProgram, "uTexture");
        mYuvWidthLocation = GLES20.glGetUniformLocation(mYuvProgram, "uWidth");
        mYuvheightLocation = GLES20.glGetUniformLocation(mYuvProgram, "uHeight");

        mYuvVertexBuffer = GlUtil.createFloatBuffer(mYuvVertices);
        mYuvCoordBuffer = GlUtil.createFloatBuffer(mYuvTextureCoords);
        /***************准备last数据*************************/
        String lastVertexShader = FileUtils.readTextFileFromResource(mContext, R.raw.last_vertex_shader);
        String lastFragmentShader = FileUtils.readTextFileFromResource(mContext, R.raw.last_fragment_shader);
        mLastProgram = GlUtil.createProgram(lastVertexShader, lastFragmentShader);
        mLastVertexLocation = GLES20.glGetAttribLocation(mLastProgram, "aPosition");
        mLastCoordLocation = GLES20.glGetAttribLocation(mLastProgram, "aCoord");
        mLastMatrixLocation = GLES20.glGetUniformLocation(mLastProgram, "uMatrix");

        mLastVertexBuffer = GlUtil.createFloatBuffer(mLastVertices);
        mLastCoordBuffer = GlUtil.createFloatBuffer(mLastTextureCoords);

        // mLastMatrix = MatrixUtils.flip(mLastMatrix, false, true); //过了两层filter，所以不需要转换了
        /****************准备yuv数据发送线程********************/
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);// GlSurfaceView基本参数设置
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (mSurfaceTexture) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            mSurfaceTexture.attachToGLContext(mTextureID);

            int[] fbo = GlUtil.createFBO(mWidth, mHeight);
            int[] frameBuffer = new int[]{fbo[0]};
            int[] rbo = new int[]{fbo[1]};
            int[] textureColorBuffer = new int[]{fbo[2]};
            /********************以上生成framebuffer完成，现在绑定了这个fbo，渲染都是在这个fbo上，
             并不在默认屏幕上，这时候可以进行多重滤镜，水印等组合处理渲染结果中颜色纹理都在textureColorBuffer中，最后一次性在渲染到屏幕上****************************/
            /****************进行相机预览，需要一个Fbo，提供输入TextureID**********************/
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
            mSurfaceTexture.updateTexImage(); //拿到最新的数据
            //绘制预览数据
            GLES20.glUseProgram(mCameraProgram);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
            GLES20.glUniform1f(mCameraTextureLocation, 0);
            GLES20.glEnableVertexAttribArray(mCameraVertexLocation);
            GLES20.glVertexAttribPointer(mCameraVertexLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mCameraVertexBuffer);
            GLES20.glEnableVertexAttribArray(mCameraTextureCoordLocation);
            GLES20.glVertexAttribPointer(mCameraTextureCoordLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mCameraTextureCoordsBuffer);
            //进行图形的转换
            GLES20.glUniformMatrix4fv(mCameraMatrixLocation, 1, false, mCameraMatrix, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mCameraDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, mCameradrawListBuffer);
            GLES20.glDisableVertexAttribArray(mCameraVertexLocation);
            GLES20.glDisableVertexAttribArray(mCameraTextureCoordLocation);

            /****************先画水印，进行深度检测，融合等，重新创建一个Fbo，根据相机提供的TextureID，重新渲染，提供输出ID********************/
            GLES20.glUseProgram(mWaterMarkProgram);
            GLES20.glViewport(mWidth - mBitmapWidth * 2, 20, mBitmapWidth * 2, mBitmapHeight * 2);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_DST_ALPHA);
            //放位置，矩阵变换
            GLES20.glUniformMatrix4fv(mWarterMarkMVPMatrixHandler, 1, false, mWaterMarkMatrix, 0);
            GLES20.glEnableVertexAttribArray(mWarterMarkPositionHandler);
            GLES20.glVertexAttribPointer(mWarterMarkPositionHandler, 2, GLES20.GL_FLOAT, false, 0, mWaterMarkVertexBuffer);
            GLES20.glEnableVertexAttribArray(mWarterMarkTextureCoordHandler);
            GLES20.glVertexAttribPointer(mWarterMarkTextureCoordHandler, 2, GLES20.GL_FLOAT, false, 0, mWaterMarkTextureCoordBuffer);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //把活动的纹理单元设置为纹理单元0
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mWaterMarkTextureID); //把纹理绑定到纹理单元0上
            GLES20.glUniform1i(mWarterMarkTextureCoordHandler, 0); //把纹理单元0传给片元着色器进行渲染
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glDisable(GLES20.GL_BLEND);
            GLES20.glViewport(0, 0, mWidth, mHeight);

            /******************渲染灰色蒙层，根据滤镜提供的输出id，重新渲染，提供输出ID*************************************/
            int[] grayFbo = GlUtil.createFBO(mWidth, mHeight);
            int[] grayFrameBuffer = new int[]{grayFbo[0]};
            int[] grayRbo = new int[]{grayFbo[1]};
            int[] grayTextureColorBuffer = new int[]{grayFbo[2]};
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, grayFrameBuffer[0]);
            GLES20.glUseProgram(mGrayProgram);
            //放位置，矩阵变换
            GLES20.glUniformMatrix4fv(mGrayMatrixLocation, 1, false, mGrayMatrix, 0);
            GLES20.glEnableVertexAttribArray(mGrayVertexLocation);
            GLES20.glVertexAttribPointer(mGrayVertexLocation, 2, GLES20.GL_FLOAT, false, 0, mGrayVertexBuffer);
            GLES20.glEnableVertexAttribArray(mGrayCoordLocation);
            GLES20.glVertexAttribPointer(mGrayCoordLocation, 2, GLES20.GL_FLOAT, false, 0, mGrayCoordBuffer);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //把活动的纹理单元设置为纹理单元0
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureColorBuffer[0]); //把相机采集的纹理绑定到纹理单元0上
            GLES20.glUniform1i(mGrayCoordLocation, 0); //把纹理单元0传给片元着色器进行渲染
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            //绑定到默认纹理，渲染最后的纹理
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0); //绑定回默认输出buffer，就是屏幕，然后绘画

            /*********************在glsl中转换为YUV数据**********************/
            int[] yuvFbo = GlUtil.createFBO(mWidth, mHeight);
            int[] yuvFrameBuffer = new int[]{yuvFbo[0]};
            int[] yuvRbo = new int[]{yuvFbo[1]};
            int[] yuvTextureColorBuffer = new int[]{yuvFbo[2]};
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, yuvFrameBuffer[0]);
            GLES20.glUseProgram(mYuvProgram);
            //放位置，矩阵变换
            GLES20.glUniformMatrix4fv(mYuvMatrixLocation, 1, false, mYuvMatrix, 0);
            GLES20.glEnableVertexAttribArray(mYuvVertexLocation);
            GLES20.glVertexAttribPointer(mYuvVertexLocation, 2, GLES20.GL_FLOAT, false, 0, mYuvVertexBuffer);
            GLES20.glEnableVertexAttribArray(mYuvTextureCoordLocation);
            GLES20.glVertexAttribPointer(mYuvTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 0, mYuvCoordBuffer);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //把活动的纹理单元设置为纹理单元0
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureColorBuffer[0]); //把相机采集的纹理绑定到纹理单元0上
            GLES20.glUniform1i(mYuvTextureLocation, 0); //把纹理单元0传给片元着色器进行渲染
            GLES20.glUniform1f(mYuvWidthLocation, mWidth);
            GLES20.glUniform1f(mYuvheightLocation, mHeight);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            //绑定到默认纹理，渲染最后的纹理
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0); //绑定回默认输出buffer，就是屏幕，然后绘画
            /*********************把颜色纹理画出*****************************/
            GLES20.glUseProgram(mLastProgram);
            GLES20.glUniformMatrix4fv(mLastMatrixLocation, 1, false, mLastMatrix, 0);
            GLES20.glEnableVertexAttribArray(mLastVertexLocation);
            GLES20.glVertexAttribPointer(mLastVertexLocation, 2, GLES20.GL_FLOAT, false, 0, mLastVertexBuffer);
            GLES20.glEnableVertexAttribArray(mLastCoordLocation);
            GLES20.glVertexAttribPointer(mLastCoordLocation, 2, GLES20.GL_FLOAT, false, 0, mLastCoordBuffer);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0); //把活动的纹理单元设置为纹理单元0
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, grayTextureColorBuffer[0]); //把纹理绑定到纹理单元0上
            GLES20.glUniform1i(mLastCoordLocation, 0); //把纹理单元0传给片元着色器进行渲染
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            // 这里读取纹理，建议使用PBO，效率更高，不用pbo，会掉帧严重
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(mWidth * mHeight * 4); // 4，每个像素4个byte
            byteBuffer.position(0);//readPixel是很耗时，并被阻塞的过程
            GLES20.glReadPixels(0, 0, mWidth, mHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
            if (mListener != null) {
                //获取的数据对倒的。转90度，这里还是rgb数据，需要转yuv，mediaCodec才能解析，使用libYuv，速度快（待）
                //转方向可以给MediaCodec传反转矩阵，播放器播放mp4时，会读入这个矩阵，转向播放
                //使用libyuv做镜像转换
                byte[] data = byteBuffer.array();
                byte[] dst = new byte[data.length];
                YuvUtils.rgba_mirror(data, dst, mWidth, mHeight); //转换为rgba！！！不是argb
                mListener.output(dst);
                // mListener.output(data);
            }
            GLES20.glDeleteTextures(1, textureColorBuffer, 0); //先删除
            GLES20.glDeleteRenderbuffers(1, rbo, 0);
            GLES20.glDeleteFramebuffers(1, fbo, 0);
            GLES20.glDeleteTextures(1, grayTextureColorBuffer, 0); //先删除
            GLES20.glDeleteRenderbuffers(1, grayRbo, 0);
            GLES20.glDeleteFramebuffers(1, grayFbo, 0);
            GLES20.glDeleteTextures(1, yuvTextureColorBuffer, 0); //先删除
            GLES20.glDeleteRenderbuffers(1, yuvRbo, 0);
            GLES20.glDeleteFramebuffers(1, yuvFbo, 0);
            mSurfaceTexture.detachFromGLContext();
        }
    }


    private OnFrameOK mListener;

    /**
     * 设置一帧数据生成回调
     *
     * @param listener
     */
    public void setOnFrameOKListener(OnFrameOK listener) {
        this.mListener = listener;
    }

    /**
     * 数据生成后回调
     */
    public interface OnFrameOK {
        void output(byte[] data);
    }
}
