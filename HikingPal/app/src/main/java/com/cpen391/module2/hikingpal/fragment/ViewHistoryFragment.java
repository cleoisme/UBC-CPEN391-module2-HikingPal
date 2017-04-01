package com.cpen391.module2.hikingpal.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cpen391.module2.hikingpal.R;

import java.io.File;

/**
 * Created by YueyueZhang on 2017-03-12.
 */

public class ViewHistoryFragment extends Fragment {

    public ViewHistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.view_history_frag, container, false);
        HorizontalScrollView sv = (HorizontalScrollView) ll.getChildAt(0);
       // sv.setForegroundGravity(HorizontalScrollView.SCROLL_INDICATOR_LEFT);
        LinearLayout ic = (LinearLayout) sv.getChildAt(0);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        File myFolder = new File("sdcard/hikingPal/saveTrail/");
        File[] myFiles = myFolder.listFiles();

        if(myFolder.isDirectory()){
            if( myFiles != null){
                for (File child : myFiles){
                    Bitmap myBitmap = BitmapFactory.decodeFile(child.getAbsolutePath());
                    final ImageView myImage = imageFiller(myBitmap);
                    ic.addView(myImage, lp);
                    //ic.getLayoutParams().width = 230;
                    //ic.getLayoutParams().height = 260;
                    //ic.setPadding(10, 0, 10, 0);
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
        //iv.setPadding(10, 10, 10, 10);
        return iv;
    }
}
