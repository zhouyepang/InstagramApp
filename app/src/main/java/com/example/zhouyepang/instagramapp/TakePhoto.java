package com.example.zhouyepang.instagramapp;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.util.SparseIntArray;
import android.view.LayoutInflater;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;


public class TakePhoto  extends Fragment {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private CameraDevice cameraDevice;
    private Button btnShot;
    private ImageButton btnConfirm;
    private ImageButton btnCancel;
    private Handler childHandler;
    private Handler mainHandler;
    private ImageReader imageReader;
    private CameraManager cameraManager;
    private CaptureRequest.Builder previewRequestBuilder;
    private CameraCaptureSession cameraCaptureSessions;
    private File file;
    private int flashOption;
    private Switch swiFlash;
    private GridOverlay gridOverlay;
    private Switch swiGrid;
    Uri imageUri;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View thisView = inflater.inflate(R.layout.camera, container, false);

        surfaceView = thisView.findViewById(R.id.surfaceView);
        flashOption = 0;
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        btnShot = thisView.findViewById(R.id.shot);
        btnConfirm = thisView.findViewById(R.id.confirm);
        btnCancel = thisView.findViewById(R.id.cancel);
        swiFlash = thisView.findViewById(R.id.FlashLight);
        gridOverlay = thisView.findViewById(R.id.drawGridView);
        swiGrid = thisView.findViewById(R.id.grid);

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        thisView.findViewById(R.id.shot).setClickable(false);
                    } else {
                        initCamera2();
                    }
                } else {
                    initCamera2();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (null != cameraDevice) {
                    cameraDevice.close();
                    cameraDevice = null;
                }
            }
        });

        btnShot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    Intent uploadOptionsIntent = new Intent(UploadSelect.getContextOfApplication(), UploadOptions.class);
                    uploadOptionsIntent.putExtra("imageUri", imageUri.toString());
                    startActivity(uploadOptionsIntent);
                }
            }
        });
        btnConfirm.setEnabled(false);

        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                takePreview();
            }
        });
        btnCancel.setEnabled(false);

        swiFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flashOption = 1;
                    setOpenFlash();
                } else {
                    flashOption = 0;
                    setCloseFlash();
                }
            }
        });

        gridOverlay.bringToFront();

        swiGrid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    gridOverlay.setVisibility(View.VISIBLE);
                } else {
                    gridOverlay.setVisibility(View.INVISIBLE);
                }
            }
        });

        return thisView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            for (int grantResult : grantResults) {
                if(grantResult != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getActivity(), "No Permission for Camera", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            initCamera2();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initCamera2() {
        btnShot.setClickable(true);
        int cameraId = 0;

        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getActivity().getMainLooper());

        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG,1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = reader.acquireNextImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                if (bitmap != null) {
                    writeToFile(bitmap);
                }

                //Refresh Media Store, It must refresh before get saved content!!!!!!!!!!!!!!!!!!!!!!!!!
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                getActivity().sendBroadcast(intent);
                //---------------------------------------------------------------------------------------

                imageUri = SendData.getImageContentUri(UploadSelect.getContextOfApplication(), file);

                image.close();

                btnConfirm.setEnabled(true);
                btnCancel.setEnabled(true);


            }
        }, mainHandler);

        cameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            cameraManager.openCamera(cameraId+"", stateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            takePreview();
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDisconnected(CameraDevice camera) {
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            Toast.makeText(getActivity(), "Active Camera Fail", Toast.LENGTH_SHORT).show();
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePreview() {
        try {
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            cameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReader.getSurface()), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == cameraDevice) return;
                    cameraCaptureSessions = cameraCaptureSession;
                    try {
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_OFF);
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.FLASH_MODE_OFF);
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        cameraCaptureSessions.setRepeatingRequest(previewRequest, null, childHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getActivity(), "Fail Configuration", Toast.LENGTH_SHORT).show();
                }
            }, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(Bitmap bitmap) {
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()+"/"+ UUID.randomUUID()+".jpg");
        try {
            OutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePicture() {
        if (cameraDevice == null) {
            return;
        }
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            cameraCaptureSessions.capture(mCaptureRequest, null, childHandler);
            swiFlash.setChecked(false);
            cameraCaptureSessions.stopRepeating();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void setOpenFlash() {
        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON);
        previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
        try {
            cameraCaptureSessions.setRepeatingRequest(previewRequestBuilder.build(),null,childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void setCloseFlash() {
        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_OFF);
        previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
        try {
            cameraCaptureSessions.setRepeatingRequest(previewRequestBuilder.build(),null,childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}