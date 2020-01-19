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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.divyanshu.draw.widget.CircleView;
import com.divyanshu.draw.widget.DrawView;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.util.UUID;

//java imports


public class MainActivity extends AppCompatActivity implements OnClickListener, ColorPickerDialogListener {
    private DrawView draw;
    private ImageButton colorSelect, drawBtn, eraseBtn, newBtn, saveBtn;
    static ImageButton redoBtn, undoBtn;
    private float smallBrush, mediumBrush, largeBrush;

    Color currentColor;
    private View toolbar;
    private CircleView preview;
    private SeekBar seekBarWidth;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final int DIALOG_ID = 0;

    boolean eraser;
    boolean toolbarOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        draw = (DrawView) findViewById(R.id.drawing);

        colorSelect = findViewById(R.id.image_draw_color);
        colorSelect.setOnClickListener(this);
        currentColor = Color.valueOf(Color.BLACK);

        smallBrush = getResources().getInteger(R.integer.smallSize);
        mediumBrush = getResources().getInteger(R.integer.mediumSize);
        largeBrush = getResources().getInteger(R.integer.largeSize);

        drawBtn = findViewById(R.id.image_draw_width);
        drawBtn.setOnClickListener(this);

        draw.setStrokeWidth(15);

        eraseBtn = findViewById(R.id.image_draw_eraser);
        eraseBtn.setOnClickListener(this);
        eraser = false;

        newBtn = findViewById(R.id.newBtn);
        newBtn.setOnClickListener(this);

        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        redoBtn = findViewById(R.id.image_draw_redo);
        undoBtn = findViewById(R.id.image_draw_undo);

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

        toolbar = findViewById(R.id.draw_tools);
        toolbarOpen = false;

        preview = findViewById(R.id.circle_view_preview);

        seekBarWidth = findViewById(R.id.seekBar_width);
        seekBarWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                draw.setStrokeWidth((float) progress);
                preview.setCircleRadius((float) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
        }
    }



    public void onClick(View view) {
        if (view.getId() == R.id.image_draw_width) {
            toggleToolbar();
        }
        else if(view.getId() == R.id.image_draw_brush){
            draw.setColor(currentColor.toArgb());
            preview.setColor(currentColor.toArgb());
        }
        else if (view.getId() == R.id.image_draw_eraser) {
            draw.setColor(Color.WHITE);
            preview.setColor(Color.WHITE);
        }
        else if(view.getId() == R.id.image_draw_color){
//            ColorPickerDialogBuilder
//                    .with(getApplicationContext())
//                    .setTitle("Choose color")
//                    .initialColor(currentColor.toArgb())
//                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
//                    .density(12)
//                    .setOnColorSelectedListener(new OnColorSelectedListener() {
//                        @Override
//                        public void onColorSelected(int selectedColor) {
//                            Toast.makeText(getApplicationContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .setPositiveButton("ok", new ColorPickerClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//                            currentColor = Color.valueOf(selectedColor);
//                            draw.setColor(currentColor.toArgb());
//                        }
//                    })
//                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    })
//                    .build()
//                    .show();
            ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                    .setAllowPresets(false)
                    .setDialogId(DIALOG_ID)
                    .setColor(currentColor.toArgb())
                    .setShowAlphaSlider(true)
                    .show(this);
        }
        else if (view.getId() == R.id.newBtn)
        {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New Drawing");
            newDialog.setMessage("Start a new_img drawing? (This will erase your current drawing.)");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    draw.reset();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            newDialog.show();
        }
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

    private void toggleToolbar() {
        toolbar.animate().translationY((toolbarOpen ? 56 : 0) * getResources().getDisplayMetrics().density);
        toolbarOpen = !toolbarOpen;
    }

    @Override public void onColorSelected(int dialogId, int color) {
        Log.d("Color","onColorSelected() called with: dialogId = [" + dialogId + "], color = [" + color + "]");
        switch (dialogId) {
            case DIALOG_ID:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                currentColor = Color.valueOf(color);
                draw.setColor(color);

                break;
        }
    }
    @Override public void onDialogDismissed(int dialogId) {

    }
//    public void colorClicked(View view) {
//        if (view != currPaint) {
//            ImageButton imgView = (ImageButton) view;
//            String color = view.getTag().toString();
//
////            draw.setErase(false);
////            draw.setStrokeWidth(draw.getLastBrushSize());
//            draw.setColor(Color.parseColor(color));
//
//            imgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
//            currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint, null));
//            currPaint = (ImageButton) view;
//        }
//    }

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
