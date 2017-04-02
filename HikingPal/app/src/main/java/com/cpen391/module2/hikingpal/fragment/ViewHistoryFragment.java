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

import com.cpen391.module2.hikingpal.MainActivity;
import com.cpen391.module2.hikingpal.MapImageStorage;
import com.cpen391.module2.hikingpal.R;

import java.io.File;

import static com.cpen391.module2.hikingpal.R.color.red;

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
        LinearLayout ic = (LinearLayout) sv.getChildAt(0);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        File myFolder = new File("sdcard/hikingPal/saveTrail/");
        File[] myFiles = myFolder.listFiles();

        if(myFolder.isDirectory()){
            if( myFiles != null){
                for (File child : myFiles){
                    ic.getLayoutParams().width = 20;
                    ic.getLayoutParams().height = 300;
                    ic.setPadding(0, 20, 0, 0);
                    ic.setBackgroundColor(getResources().getColor(red));
                    Bitmap myBitmap = BitmapFactory.decodeFile(child.getAbsolutePath());
                    final ImageView myImage = imageFiller(myBitmap, child.getAbsolutePath());
                    ic.addView(myImage, lp);
                }
            }else{
                //display nothing
            }
        }else{
            //display nothing
        }

        return ll;
    }

    private ImageView imageFiller(Bitmap myBitmap, String path) {

        final String mapImagePath = path;
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
                                MapImageStorage mapImageStorage = new MapImageStorage(getContext());
                                String mapImageString = mapImageStorage.getMapImage(mapImagePath);
                                MainActivity mainActivity = (MainActivity) getActivity();
                                mainActivity.sendMessageSlow("Z" + mapImageString+ "Z");

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
