package com.xlsoft.kudanar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView t;
    int c=0;
    //tarunlink_private static final String SCENE_URL = "https://us-east-1.sumerian.aws/daf4be2168664f4e8ce85da7ee7a460f.scene/?arMode=true";
    private static final String SCENE_URL = "https://us-east-1.sumerian.aws/844994c1877449faaaab134218cba968.scene/?arMode=true";
    //private static final String SCENE_URL = "https://www.google.co.in/search?q=how+to+interact+with+sumerian+host+in+android+studio&ie=&oe=";
    private static final String IMAGE_FILENAME = "SumerianAnchorImage.png";
    private static final float IMAGE_WIDTH_IN_METERS = (float)0.18;

    private GLSurfaceView mSurfaceView;
    private Session mSession;
    private SumerianConnector mSumerianConnector;
    private View mlayout;

    // Set to true ensures requestInstall() triggers installation if necessary.
    private boolean mUserRequestedInstall = true;

    private final BackgroundRenderer mBackgroundRenderer = new BackgroundRenderer();

    private AugmentedImageDatabase createImageDatabase(Session mSession) {
        AugmentedImageDatabase imageDatabase = new AugmentedImageDatabase(mSession);
        Bitmap bitmap = null;
        try (InputStream inputStream = getAssets().open(IMAGE_FILENAME)) {
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "I/O exception loading augmented image bitmap.", e);
        }

        imageDatabase.addImage("SumerianAnchorImage", bitmap, IMAGE_WIDTH_IN_METERS);
        return imageDatabase;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
t=findViewById(R.id.textview1);

        mSurfaceView = findViewById(R.id.gl_surface_view);
        mlayout=findViewById(R.id.parent1);

        // Set up renderer.
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        mSurfaceView.setRenderer(this);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(c==0)
        {
            c=1;
            t.setText("LISTENING...");
        }
        else
        {
            c=0;
            t.setText("Touch to Interact");
        }
        return super.onTouchEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onResume() {
        super.onResume();

        if (mSession == null) {
            // ARCore requires camera permissions to operate. If we did not yet obtain runtime
            // permission on Android M and above, now is a good time to ask the user for it.
            if (!CameraPermissionHelper.hasCameraPermission(this)) {
                CameraPermissionHelper.requestCameraPermission(this);
                return;
            }
            if (!microphnpermissionhelper.hasMicPermission(this)) {
                microphnpermissionhelper.requestMicPermission(this);
                return;
            }
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    case INSTALLED:
                        // Success, create the AR session.
                        mSession = new Session(this);
                        break;
                    case INSTALL_REQUESTED:
                        // Ensures next invocation of requestInstall() will either return
                        // INSTALLED or throw an exception.
                        mUserRequestedInstall = false;
                        return;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final WebView webView = findViewById(R.id.activity_main_webview);
            webView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            mSumerianConnector = new SumerianConnector(webView, mSession, mSurfaceView);

            // Create config and check if camera access that is not blocking is supported.
            Config config = new Config(mSession);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            config.setFocusMode(Config.FocusMode.AUTO);
            if (!mSession.isSupported(config)) {
                throw new RuntimeException("This device does not support AR");
            }

            config.setAugmentedImageDatabase(createImageDatabase(mSession));
            mSession.configure(config);
            mSumerianConnector.loadUrl(SCENE_URL);
        }

        try {
            mSession.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }
        mSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Note that the order matters - GLSurfaceView is paused first so that it does not try
        // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
        // still call mSession.update() and get a SessionPausedException.
        mSurfaceView.onPause();
        if (mSession != null) {
            mSession.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this,
                    "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
        if (!microphnpermissionhelper.hasMicPermission(this)) {
            Toast.makeText(this,
                    "Microphone permission is needed to run this application", Toast.LENGTH_LONG).show();
            if (!microphnpermissionhelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                microphnpermissionhelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Create the texture and pass it to ARCore session to be filled during update().
        mBackgroundRenderer.createOnGlThread(/*context=*/this);
        mSession.setCameraTextureName(mBackgroundRenderer.getTextureId());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mSession.setDisplayGeometry(getSystemService(WindowManager.class).getDefaultDisplay().getRotation(), width, height);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mSession == null) {
            return;
        }

        try {
            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            final Frame frame = mSession.update();

            // Draw background.
            mBackgroundRenderer.draw(frame);
            mSumerianConnector.update();
        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }
}
