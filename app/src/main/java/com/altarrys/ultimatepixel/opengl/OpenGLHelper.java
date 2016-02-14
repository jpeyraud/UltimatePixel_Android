package com.altarrys.ultimatepixel.opengl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLUtils;
import android.util.Log;

import com.altarrys.ultimatepixel.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.opengl.GLES20.*;

/**
 * Created by jpeyraux on 22/12/2015.
 */
public class OpenGLHelper {

    private static final String TAG = "OpenGLHelper";

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The shader object handler.
     */
    public static int loadGLShader(int type, Context context, int resId) {
        String code = readRawTextFile(context, resId);
        int shader = glCreateShader(type);
        glShaderSource(shader, code);
        glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0);

        Log.d(TAG, "Log compiling shader: " + glGetShaderInfoLog(shader));
        Log.d(TAG, ""+shader);
        Log.d(TAG, ""+code);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     */
    public static void checkGLError(String label) {
        int error;
        while ((error = glGetError()) != GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resourceId The resource ID of the raw text file about to be turned into a shader.
     * @return The context of the text file, or null in case of error.
     */
    public static String readRawTextFile(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId); InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) { body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }
        return body.toString();
    }

    /**
     * Function to create a gl program and link shaders to it
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {

        // Create a GL program
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            Log.w(TAG, "Could not create new program");
            return 0;
        }

        // Attaching our shaders
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);

        // Linking program
        glLinkProgram(programObjectId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        Log.v(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programObjectId));

        // Verifying link status
        if (linkStatus[0] == 0) {
            Log.w(TAG, "Linking of program failed.");
            return 0;
        }

        return programObjectId;
    }

    /**
     * Function to validate a gl program and print log
     */
    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);
        final int[] validateStatus = new int[1]; glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programObjectId)); return validateStatus[0] != 0;
    }

    /**
     * Draw a texture
     */
    public static int drawStringOnTexture(String text, int texSize)
    {
        final int[] textureHandle = new int[1];

        glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            // Draw text on bitmap
            Bitmap bitmap = Bitmap.createBitmap(texSize, texSize, Bitmap.Config.ARGB_8888); // this creates a MUTABLE bitmap

            for (int i = 0; i<texSize; i++)
                for (int j = 0; j<texSize; j++)
                    bitmap.setPixel(i,j, Color.BLUE);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(18);
            canvas.drawText(text, 4 , 22, paint);
            canvas.drawText(" km/h ", 4 , 40, paint);
            //paint.setStrokeWidth(7);
            //canvas.drawRect(20,20,400,400, paint);

            // Bind to the texture in OpenGL
            glBindTexture(GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }


    /**
     * Load texture
     */
    public static int loadTexture(final Context context, final int resourceId, int filter, int wrap)
    {
        final int[] textureHandle = new int[1];

        glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            glBindTexture(GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrap);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrap);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }


}
