package com.cpen391.module2.hikingpal.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpen391.module2.hikingpal.HikingPalStorage;
import com.cpen391.module2.hikingpal.MainActivity;
import com.cpen391.module2.hikingpal.R;

import java.io.File;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.cpen391.module2.hikingpal.MainActivity.curFrag2;
import static com.cpen391.module2.hikingpal.MainActivity.waiting_view;

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
        LinearLayout img = (LinearLayout) frame.getChildAt(1);
        LinearLayout title_line = (LinearLayout) frame.getChildAt(2);
        LinearLayout del_line = (LinearLayout) frame.getChildAt(0);

        TextView no_trail = (TextView) ll.findViewById(R.id.no_trail);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300, WRAP_CONTENT);

        final File myFolder = new File("sdcard/hikingPal/saveTrail/");
        final File[] myFiles = myFolder.listFiles();

        if (myFolder.isDirectory()) {
            if (myFiles != null && myFiles.length != 0) {
                no_trail.setVisibility(View.INVISIBLE);
                for (final File child : myFiles) {
                    img.getLayoutParams().width = WRAP_CONTENT;
                    img.getLayoutParams().height = 470;
                    //ic.LayoutParams.setMargins();
                    //left, top, right, bot
                    img.setPadding(20, 40, 20, 20);
                    Bitmap myBitmap = BitmapFactory.decodeFile(child.getAbsolutePath());
                    final ImageView myImage = imageFiller(myBitmap, child.getAbsolutePath());
                    myImage.setScaleType(ImageView.ScaleType.FIT_START);
                    img.addView(myImage, lp);

                    final TextView title = new TextView(this.getActivity());
                    final String img_name = child.getName();
                    title.setText(child.getName());
                    title.setPadding(30, 0, 20, 10);
                    title.setTextColor(0xFFFDFDFD);
                    title_line.addView(title, lp);

                    final ImageButton del = new ImageButton(this.getActivity());
                    del.setImageResource(R.drawable.delete);
                    del.setPadding(230, 15, 0, 0);
                    del.setBackgroundColor(0x00ffffff);
                    del_line.addView(del, lp);
                    del.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Log.d("del clicked", img_name);
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Delete?")
                                    .setMessage("Do you want to delete this saved trail permanently? ")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            File current_img = new File(myFolder, img_name);
                                            boolean deleted = current_img.delete();
                                            if (deleted == true) {
                                                Log.d("del clicked", "deleted");
                                            }
                                            // TODO: 2017-04-03  delete the trail from the database

                                            FragmentTransaction tr = getFragmentManager().beginTransaction();
                                            curFrag2 = new ViewHistoryFragment();
                                            HikingPalStorage hikingPalStorage = new HikingPalStorage(getContext());
                                            hikingPalStorage.removeMapImage(current_img.getAbsolutePath());

                                            tr.replace(R.id.fragment_container_med1, curFrag2);
                                            tr.commit();

                                            Toast.makeText(getActivity(), "trail deleted!", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //do nothing
                                        }
                                    })
                                    .show();

                        }
                    });
                }
            } else {
                no_trail.setVisibility(View.VISIBLE);
            }
        } else {
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
                                HikingPalStorage hikingPalStorage = new HikingPalStorage(getContext());
                                String mapImageString = hikingPalStorage.getMapImage(mapImagePath, true);
                                MainActivity mainActivity = (MainActivity) getActivity();
                                mainActivity.sendMessageSlow(mapImageString);
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

    public void sendAllTrails() {

        final HikingPalStorage hikingPalStorage = new HikingPalStorage(getContext());

        File myFolder = new File("sdcard/hikingPal/saveTrail/");
        final File[] myFiles = myFolder.listFiles();
        final StringBuilder sb = new StringBuilder();
        sb.append(hikingPalStorage.BT_MAP_INIT);

        if (myFolder.isDirectory()) {
            if (myFiles.length != 0) {

                new CountDownTimer(5000, 1000) {
                    public void onTick(long millisUntilFinished) {

                        for (final File child : myFiles) {
                            String mapImageString = hikingPalStorage.getMapImage(child.getAbsolutePath(), false);
                            sb.append(mapImageString);
                            sb.setLength(sb.length() - 1); // Remove last map delimiter (otherwise 2 in a row)
                            Log.d("sending", child.getAbsolutePath());
                        }

                        sb.append(hikingPalStorage.BT_MAP_DELIMITER);
                        sb.append(hikingPalStorage.BT_MAP_INIT);
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.sendMessageSlow(sb.toString());

                        waiting_view.setVisibility(View.VISIBLE);

                    }
                    public void onFinish() {
                        waiting_view.setVisibility(View.INVISIBLE);
                    }

                }.start();

            } else {
                Toast.makeText(getActivity(), "No saved trails", Toast.LENGTH_SHORT).show();
            }
        }


    }
}