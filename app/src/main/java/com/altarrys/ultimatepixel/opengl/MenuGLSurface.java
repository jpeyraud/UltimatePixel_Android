package com.altarrys.ultimatepixel.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import com.altarrys.ultimatepixel.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.altarrys.ultimatepixel.opengl.OpenGLHelper.*;

/**
 * Created by jpeyraux on 11/02/2016.
 */
public class MenuGLSurface extends GLSurfaceView implements GLSurfaceView.Renderer {


    private static final String TAG = "MenuGLSurface";

    private final int mFragShaderID;

    private static int COMPONENT_PER_VERTEX = 3;
    private final int vertexStride = COMPONENT_PER_VERTEX * 4; // 4 bytes per vertex

    float[] positions = {
            1.0f,  1.0f,  0.0f,
            -1.0f,  1.0f,  0.0f,
            1.0f, -1.0f,  0.0f,
            -1.0f, -1.0f,  0.0f,
    };

    float mWidth;
    float mHeight;

    protected FloatBuffer mVertices;
    protected FloatBuffer mUV;

    long mStart;
    long mCurrent;
    long mPrevious;

    double mDuration = 1300.0;

    int glProgram;

    private int mVerticesLocation;
    private int mResolutionLocation;
    private int mGlobalTimeLocation;
    private int mTexture0Location;

    public MenuGLSurface(Context context, int fragShader) {
        super(context);

        mFragShaderID = fragShader;

        MenuGLSurfaceInit();
    }

    public void MenuGLSurfaceInit() {
        // Create an OpenGL ES 2.0 context
        /*
        this.setRenderMode(0);
        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setPreserveEGLContextOnPause(true);
        this.getHolder().setFormat(-3);
        this.setZOrderMediaOverlay(true);
        */
        this.setEGLContextClientVersion(2);
        this.setRenderer(this);

        ByteBuffer bbVertices = ByteBuffer.allocateDirect(positions.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        mVertices = bbVertices.asFloatBuffer();
        mVertices.put(positions);
        mVertices.position(0);

        /*ByteBuffer bbUV = ByteBuffer.allocateDirect(uv.length * 4);
        bbUV.order(ByteOrder.nativeOrder());
        mUV = bbUV.asFloatBuffer();
        mUV.put(uv);*/
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig eglConfig) {

        mStart = System.currentTimeMillis();

        // Compile shaders
        int vertexShader = loadGLShader(GL_VERTEX_SHADER, getContext(), R.raw.basic_vertex_shader);
        int passthroughShader = loadGLShader(GL_FRAGMENT_SHADER, getContext(),  mFragShaderID);

        // Set up gl program
        glProgram = glCreateProgram();
        glAttachShader(glProgram, vertexShader);
        glAttachShader(glProgram, passthroughShader);
        glLinkProgram(glProgram);
        validateProgram(glProgram);
        glUseProgram(glProgram);

        checkGLError("Gl program");

        // Enable functionalities
        //glEnable(GL_DEPTH_TEST);
        //glEnable(GL_BLEND);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // location
        mVerticesLocation = glGetAttribLocation(glProgram, "a_Position");
        mResolutionLocation = glGetUniformLocation(glProgram, "iResolution");
        mGlobalTimeLocation = glGetUniformLocation(glProgram, "iGlobalTime");

        // Texture
        mTexture0Location = OpenGLHelper.loadTexture(getContext(), R.drawable.hell, GL_NEAREST, GL_REPEAT);

        // Enable vertex array
        glEnableVertexAttribArray(mVerticesLocation);

        checkGLError("Surface created");
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        glViewport(0,0,width,height);

        Log.d(TAG, "ScreenSize: "+width+" "+height);

        mWidth = width;
        mHeight = height;

        checkGLError("Surface Changed");
    }

    @Override
    public void onDrawFrame(GL10 unused) {

        // Redraw background color
        glClear(GL_DEPTH_BUFFER_BIT);
        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(0.2f, 0.2f, 0.8f, 0.4f);

        mPrevious = mCurrent;
        mCurrent = System.currentTimeMillis();

        Log.d(TAG, "FPS: "+1.0/(mCurrent-mPrevious)*1000.0);

        // Bind textures
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture0Location);

        // Send time in ms
        glUniform1f(mGlobalTimeLocation, ((float)((mStart-mCurrent)))/1000.0f);

        // Send screen resolution
        glUniform2f(mResolutionLocation, mWidth, mHeight);

        // Send the position of the square
        glVertexAttribPointer(mVerticesLocation, COMPONENT_PER_VERTEX, GL_FLOAT, false, vertexStride, mVertices);

        // Draw
        //glDrawElements(GL_TRIANGLES, 0, GL_UNSIGNED_SHORT, mVertices);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        checkGLError("OnDraw");
    }

}
