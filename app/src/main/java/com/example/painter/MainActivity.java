package com.example.painter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

//java imports


public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private DrawView draw;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, redoBtn, undoBtn;
    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        draw = (DrawView)findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.colors);

        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));

        smallBrush = getResources().getInteger(R.integer.smallSize);
        mediumBrush = getResources().getInteger(R.integer.mediumSize);
        largeBrush = getResources().getInteger(R.integer.largeSize);

        drawBtn = (ImageButton)findViewById(R.id.brushBtn);
        drawBtn.setOnClickListener(this);

        draw.setBrushSize(mediumBrush);

        eraseBtn = (ImageButton)findViewById(R.id.eraseBtn);
        eraseBtn.setOnClickListener(this);

//        newBtn = (ImageButton)findViewById(R.id.newBtn);
//        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton)findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);

        redoBtn = findViewById(R.id.redoBtn);
        undoBtn = findViewById(R.id.undoBtn);

        redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.onClickRedo();
            }
        });
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw.onClickUndo();
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},  112);
        }
    }

    public void onClick(View view)
    {
        if (view.getId() == R.id.brushBtn)
        {
            final Dialog brushDialog = new Dialog(this, R.style.CustomDialog);
            brushDialog.setTitle("Brush Size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    draw.setErase(false);
                    draw.setBrushSize(smallBrush);
                    draw.setLastBrushSize(smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    draw.setErase(false);
                    draw.setBrushSize(mediumBrush);
                    draw.setLastBrushSize(mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    draw.setErase(false);
                    draw.setBrushSize(largeBrush);
                    draw.setLastBrushSize(largeBrush);
                    brushDialog.dismiss();
                }
            });

            brushDialog.show();
        }
        else if (view.getId() == R.id.eraseBtn)
        {
            final Dialog brushDialog = new Dialog(this, R.style.CustomDialog);
            brushDialog.setTitle("Eraser Size:");
            brushDialog.setContentView(R.layout.brush_chooser);

            ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
            smallBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    draw.setErase(true);
                    draw.changeBrushSize((int)smallBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
            mediumBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    draw.setErase(true);
                    draw.changeBrushSize((int)mediumBrush);
                    brushDialog.dismiss();
                }
            });

            ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
            largeBtn.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    draw.setErase(true);
                    draw.changeBrushSize((int)largeBrush);
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
        else if (view.getId() == R.id.saveBtn)
        {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save Drawing");
            saveDialog.setMessage("Save drawing to device gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    draw.saveDrawing();
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

    public void colorClicked(View view)
    {
        if (view != currPaint)
        {
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();

            draw.setErase(false);
            draw.setBrushSize(draw.getLastBrushSize());
            draw.setColor(color);

            imgView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint_pressed, null));
            currPaint.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.paint, null));
            currPaint = (ImageButton)view;
        }
    }
}
