package com.example.painter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.divyanshu.draw.widget.DrawView;

import java.io.File;
import java.util.UUID;

//java imports


public class MainActivity extends AppCompatActivity implements OnClickListener {
    private DrawView draw;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn;
    static ImageButton redoBtn, undoBtn;
    private float smallBrush, mediumBrush, largeBrush;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        draw = (DrawView) findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.colors);

        currPaint = (ImageButton) paintLayout.getChildAt(0);
        currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));

        smallBrush = getResources().getInteger(R.integer.smallSize);
        mediumBrush = getResources().getInteger(R.integer.mediumSize);
        largeBrush = getResources().getInteger(R.integer.largeSize);

        drawBtn = (ImageButton) findViewById(R.id.brushBtn);
        drawBtn.setOnClickListener(this);

//        draw.setBrushSize(mediumBrush);

        eraseBtn = (ImageButton) findViewById(R.id.eraseBtn);
        eraseBtn.setOnClickListener(this);

//        newBtn = (ImageButton)findViewById(R.id.newBtn);
//        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        redoBtn = findViewById(R.id.redoBtn);
        undoBtn = findViewById(R.id.undoBtn);

        redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.redo();
            }
        });
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.undo();
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.brushBtn) {
            final Dialog brushDialog = new Dialog(this, R.style.CustomDialog);
            brushDialog.setTitle("Brush Size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    draw.setStrokeWidth(smallBrush);
//                    draw.setErase(false);
//                    draw.setBrushSize(smallBrush);
//                    draw.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    draw.setStrokeWidth(mediumBrush);
//                    draw.setErase(false);
//                    draw.setBrushSize(mediumBrush);
//                    draw.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    draw.setStrokeWidth(largeBrush);
//                    draw.setErase(false);
//                    draw.setBrushSize(largeBrush);
//                    draw.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        } else if (view.getId() == R.id.eraseBtn) {
            final Dialog brushDialog = new Dialog(this, R.style.CustomDialog);
            brushDialog.setTitle("Eraser Size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    draw.setColor(Color.WHITE);
//                    draw.setErase(true);
                    draw.setStrokeWidth(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    draw.setColor(Color.WHITE);
//                    draw.setErase(true);
                    draw.setStrokeWidth(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    draw.setColor(Color.WHITE);
//                    draw.setErase(true);
                    draw.setStrokeWidth(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        }
//        else if (view.getId() == R.id.newBtn)
//        {
//            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
//            newDialog.setTitle("New Drawing");
//            newDialog.setMessage("Start a new drawing? (This will erase your current drawing.)");
//            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    draw.startNew();
//                    dialog.dismiss();
//                }
//            });
//            newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//
//            newDialog.show();
//        }
        else if (view.getId() == R.id.saveBtn) {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save Drawing");
            saveDialog.setMessage("Save drawing to device gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   saveDrawing();
                }
            });
            saveDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }

    public void colorClicked(View view) {
        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();

//            draw.setErase(false);
//            draw.setStrokeWidth(draw.getLastBrushSize());
            draw.setColor(Color.parseColor(color));

            imgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
            currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint, null));
            currPaint = (ImageButton) view;
        }
    }

    public void saveDrawing() {
        fixMediaDir();

        if (checkPermissionREAD_EXTERNAL_STORAGE(getApplicationContext())) {
            Bitmap bmp = draw.getBitmap();
            String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, UUID.randomUUID().toString() + ".png", "drawing");
            if (imgSaved != null) {
                Toast savedtoast = Toast.makeText(getApplicationContext().getApplicationContext(), "Drawing saved to Gallery", Toast.LENGTH_SHORT);
                savedtoast.show();
            } else {
                Toast unsaved = Toast.makeText(getApplicationContext().getApplicationContext(), "Image could not saved", Toast.LENGTH_SHORT);
                unsaved.show();
            }
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
