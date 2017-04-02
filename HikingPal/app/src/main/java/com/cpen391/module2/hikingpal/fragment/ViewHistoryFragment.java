package com.cpen391.module2.hikingpal.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpen391.module2.hikingpal.R;

import java.io.File;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by YueyueZhang on 2017-03-12.
 */

public class ViewHistoryFragment extends Fragment {

    public ViewHistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout ll = (FrameLayout) inflater.inflate(R.layout.view_history_frag, container, false);

        //get the HorizontalScrollView
        HorizontalScrollView sv = (HorizontalScrollView) ll.getChildAt(0);

        //get the image_line LinearLayout
        LinearLayout frame = (LinearLayout) sv.getChildAt(0);
        LinearLayout img = (LinearLayout)frame.getChildAt(0);
        LinearLayout title_line = (LinearLayout)frame.getChildAt(1);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300, WRAP_CONTENT);

        File myFolder = new File("sdcard/hikingPal/saveTrail/");
        File[] myFiles = myFolder.listFiles();

        if(myFolder.isDirectory()){
            if( myFiles != null){
                for (File child : myFiles){
                    img.getLayoutParams().width = WRAP_CONTENT;
                    img.getLayoutParams().height = 470;
                    //ic.LayoutParams.setMargins();
                    //left, top, right, bot
                    img.setPadding(20, 20, 20, 20);
                    Bitmap myBitmap = BitmapFactory.decodeFile(child.getAbsolutePath());
                    final ImageView myImage = imageFiller(myBitmap);
                    myImage.setScaleType(ImageView.ScaleType.FIT_START);
                    img.addView(myImage, lp);
                    final TextView title = new TextView(this.getActivity());
                    title.setText(child.getName());
                    title.setPadding(30,0,20,10);
                    title_line.addView(title, lp);
                }
            }else{
                //display nothing
            }
        }else{
            //display nothing
        }

        return ll;
    }

    private ImageView imageFiller(Bitmap myBitmap) {
        ImageView iv = new ImageView(this.getActivity());
        iv.setImageBitmap(myBitmap);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("The current image has been selected")
                        .setMessage("Do you want to share the corresponding info with others? ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO: 2017-04-01 send the corresponding data to the touchscreen


                            }
                        })
                        .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do nothing
                            }
                        })
                        .show();
            }
        });
        return iv;
    }
}
