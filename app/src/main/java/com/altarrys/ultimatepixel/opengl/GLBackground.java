package com.altarrys.ultimatepixel.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.altarrys.ultimatepixel.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.altarrys.ultimatepixel.opengl.OpenGLHelper.*;

/**
 * Created by jpeyraux on 11/02/2016.
 */
public class GLBackground extends GLSurfaceView implements GLSurfaceView.Renderer {


    private static final String TAG = "MenuGLSurface";

    private int mVertexShaderID;
    private int mFragShaderID;
    private int mFragShaderHighFpsID;

    private ArrayList<Double> mFps;

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

    double mStart;
    double mCurrent;
    double mPrevious;

    //Program
    int glCurrentProgram;
    int glProgram;
    int glProgramHighFps;

    //Location
    private int mVerticesLocation;
    private int mResolutionLocation;
    private int mGlobalTimeLocation;
    private int mTexture0Location;
    private int mProgressLocation;

    //Other values
    float mProgress;


    //-----------------------------------------------------------------------------------------------------------------------------
    public GLBackground(Context context, int fragShader) {
        super(context);

        mProgress = 0.0f;

        // Shader ID
        mVertexShaderID = R.raw.basic_vertex_shader;
        mFragShaderID = fragShader;
        mFragShaderHighFpsID = R.raw.color_circle_frag_shader;

        GLBackgroundInit();
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public void GLBackgroundInit() {
        // Create an OpenGL ES 2.0 context
        this.setEGLContextClientVersion(2);
        this.setRenderer(this);

        ByteBuffer bbVertices = ByteBuffer.allocateDirect(positions.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        mVertices = bbVertices.asFloatBuffer();
        mVertices.put(positions);
        mVertices.position(0);

        mFps = new ArrayList<Double>();
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public double getFps() {
        double fps = 0.0;
        for (Double d : mFps)
            fps+= d;
        return fps/30.0;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public void setProgress(float progress){
        mProgress = progress;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public void useHighFpsShader(){

        glCurrentProgram = glProgramHighFps;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig eglConfig) {

        mStart = System.currentTimeMillis();

        // Compile shaders
        int vertexShader = loadGLShader(GL_VERTEX_SHADER, getContext(), mVertexShaderID);
        int passthroughShader = loadGLShader(GL_FRAGMENT_SHADER, getContext(),  mFragShaderID);
        int vertexHighFpsShader = loadGLShader(GL_VERTEX_SHADER, getContext(), mVertexShaderID);
        int passthroughHighFpsShader = loadGLShader(GL_FRAGMENT_SHADER, getContext(),  mFragShaderHighFpsID);

        // Set up high fps gl program
        glProgramHighFps = glCreateProgram();
        glAttachShader(glProgramHighFps, vertexHighFpsShader);
        glAttachShader(glProgramHighFps, passthroughHighFpsShader);
        glLinkProgram(glProgramHighFps);
        validateProgram(glProgramHighFps);

        // Set up gl program
        glProgram = glCreateProgram();
        glAttachShader(glProgram, vertexShader);
        glAttachShader(glProgram, passthroughShader);
        glLinkProgram(glProgram);
        validateProgram(glProgram);

        glCurrentProgram = glProgram;

        checkGLError("Gl program");

        // Texture
        mTexture0Location = OpenGLHelper.loadTexture(getContext(), R.drawable.random2, GL_NEAREST, GL_REPEAT);
        checkGLError("Surface created");
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        glViewport(0,0,width,height);

        Log.d(TAG, "ScreenSize: "+width+" "+height);

        mWidth = width;
        mHeight = height;

        checkGLError("Surface Changed");
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onDrawFrame(GL10 unused) {

        glUseProgram(glCurrentProgram);

        // Redraw background color
        glClear(GL_DEPTH_BUFFER_BIT);
        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(0.2f, 0.2f, 0.8f, 0.4f);

        // location
        mVerticesLocation = glGetAttribLocation(glCurrentProgram, "a_Position");
        mResolutionLocation = glGetUniformLocation(glCurrentProgram, "iResolution");
        mGlobalTimeLocation = glGetUniformLocation(glCurrentProgram, "iGlobalTime");
        mProgressLocation = glGetUniformLocation(glCurrentProgram, "u_Progress");

        // Enable vertex array
        glEnableVertexAttribArray(mVerticesLocation);

        // fps counter
        mPrevious = mCurrent;
        mCurrent = System.currentTimeMillis();
        mFps.add(1.0 / (mCurrent - mPrevious) * 1000.0);
        Log.d(TAG, ""+1.0 / (mCurrent - mPrevious) * 1000.0);
        if (mFps.size() > 30)
            mFps.remove(mFps.size()-1);

        // Bind textures
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture0Location);

        // Send time in ms
        glUniform1f(mGlobalTimeLocation, ((float)((mCurrent-mStart+10000f)))/1000.0f);

        // Send Progress between 0 and 1
        glUniform1f(mProgressLocation, mProgress);

        // Send screen resolution
        glUniform2f(mResolutionLocation, mWidth, mHeight);

        // Send the position of the square
        glVertexAttribPointer(mVerticesLocation, COMPONENT_PER_VERTEX, GL_FLOAT, false, vertexStride, mVertices);

        // Draw
        //glDrawElements(GL_TRIANGLES, 0, GL_UNSIGNED_SHORT, mVertices);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        checkGLError("OnDraw");
    }
    //-----------------------------------------------------------------------------------------------------------------------------

}
