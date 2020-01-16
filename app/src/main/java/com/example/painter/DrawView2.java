package com.example.painter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class DrawView2 extends View
{
    //public vars
    public Bitmap canvasBitmap;

    //private vars
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private float brushSize, lastBrushSize;
    private boolean erase = false;

    private Integer currentBrushSize = 5;
    private ArrayList<Path> mPaths;
    private ArrayList<Path> undonePaths = new ArrayList<>();
    private ArrayList<Paint> undonePaints = new ArrayList<>();
    private ArrayList<Paint> mPaints;

    public boolean flagline = false;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private static float MIN_ZOOM = 1f;
    private static float MAX_ZOOM = 5f;

    private float scaleFactor = 1.f;
    private ScaleGestureDetector detector;


    public DrawView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDraw();
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            invalidate();
            return true;
        }
    }

    public void startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        setupDraw();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    private void setupDraw()
    {
        this.mPaths = new ArrayList<Path>();
        this.mPaints = new ArrayList<Paint>();

        this.addPath(false);

        drawPaint = new Paint();
        drawPath = new Path();

        drawPaint.setColor(paintColor);
        drawPaint.setStrokeWidth(currentBrushSize);
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);

        drawPaint.setPathEffect(new CornerPathEffect(10) );
        canvasPaint = new Paint(Paint.DITHER_FLAG);
//        lastBrushSize = currentBrushSize;
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());

        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize)
    {
        lastBrushSize = lastSize;
    }

    public float getLastBrushSize()
    {
        return lastBrushSize;
    }

    public void setErase(boolean isErase) {
        this.setColor("#FFFFFF");
        erase = isErase;

        if (erase)
        {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else
        {
            drawPaint.setXfermode(null);
        }
    }



    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(scaleFactor, scaleFactor);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
//        canvas.drawPath(drawPath, drawPaint);

        for (int i = 0; i < mPaths.size(); ++i) {
            canvas.drawPath(mPaths.get(i), mPaints.get(i));
            invalidate();
        }

        canvas.restore();
    }

    private void addPath(boolean fill)
    {
        drawPath = new Path();
        mPaths.add(drawPath);

        drawPaint = new Paint();
        mPaints.add(drawPaint);
        System.out.print(paintColor);
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(currentBrushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        invalidate();

    }

    public void onClickUndo() {
        if (mPaths.size() > 0) {
            undonePaths.add(mPaths.remove(mPaths.size() - 1));
            undonePaints.add(mPaints.remove(mPaints.size() - 1));
            invalidate();
        } else {}
        if (mPaths.size() > 0) {
            undonePaths.add(mPaths.remove(mPaths.size() - 1));
            undonePaints.add(mPaints.remove(mPaints.size() - 1));
            invalidate();
        }
        else{}
    }

    public void onClickRedo() {
        if (undonePaths.size() > 0) {
            mPaths.add(undonePaths.remove(undonePaths.size() - 1));
            mPaints.add(undonePaints.remove(undonePaints.size() - 1));
            invalidate();
        }
        else{}
        if (undonePaths.size() > 0) {
            mPaths.add(undonePaths.remove(undonePaths.size() - 1));
            mPaints.add(undonePaints.remove(undonePaints.size() - 1));
            invalidate();
        }
        else{}
    }

    public void drawLine(boolean flag) {
        flagline = flag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        detector.onTouchEvent(event);

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                this.addPath(true);
                drawPath.moveTo(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                drawPath.lineTo(touchX, touchY);

                this.addPath(true);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                undonePaths.clear();
                undonePaints.clear();
                MainActivity.redoBtn.setEnabled(false);
                invalidate();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void changeBrushSize(int size) {
        currentBrushSize = size;
        invalidate();
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setColor(String newColor) {
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        invalidate();
    }

    public void saveDrawing() {
        fixMediaDir();

        if (checkPermissionREAD_EXTERNAL_STORAGE(getContext())) {
            this.setDrawingCacheEnabled(true);
            String imgSaved = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), this.getDrawingCache(), UUID.randomUUID().toString() + ".png", "drawing");
            if (imgSaved != null) {
                Toast savedtoast = Toast.makeText(getContext().getApplicationContext(), "Drawing saved to Gallery", Toast.LENGTH_SHORT);
                savedtoast.show();
            } else {
                Toast unsaved = Toast.makeText(getContext().getApplicationContext(), "Image could not saved", Toast.LENGTH_SHORT);
                unsaved.show();
            }
            this.destroyDrawingCache();
        }
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                }
                break;
            default:
        }
    }

    void fixMediaDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard != null) {
            File mediaDir = new File(sdcard, "DCIM/Camera");
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
        }
    }
}