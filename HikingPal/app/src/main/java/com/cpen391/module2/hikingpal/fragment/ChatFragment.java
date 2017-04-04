package com.cpen391.module2.hikingpal.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.cpen391.module2.hikingpal.HikingPalStorage;
import com.cpen391.module2.hikingpal.R;
import com.cpen391.module2.hikingpal.module.Message;
import com.cpen391.module2.hikingpal.module.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YueyueZhang on 2017-04-01.
 */
public class ChatFragment extends Fragment {

    public ChatFragment() {
    }

    EditText sendText;

    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static LinearLayoutManager mLayoutManager;
    private static List<Message> messageList = new ArrayList<Message>();
    static int msg_id;
    HikingPalStorage hikingPalStorage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View ll = inflater.inflate(R.layout.chat_layout, container, false);

        final Button btn = (Button) ll.findViewById(R.id.buttonSend);
        sendText = (EditText)ll.findViewById(R.id.chatText);


        mRecyclerView = (RecyclerView) ll.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        hikingPalStorage = new HikingPalStorage(getContext());
        if(hikingPalStorage.getAllMessages()!=null) {
            messageList = hikingPalStorage.getAllMessages();
            msg_id = messageList.size();
        }else {
            msg_id=0;
        }

        mAdapter = new MessageAdapter(getActivity().getBaseContext(), messageList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(mRecyclerView.getAdapter().getItemCount()>=1) {
                                mRecyclerView.smoothScrollToPosition(
                                        mRecyclerView.getAdapter().getItemCount() - 1);
                            }
                        }
                    }, 100);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017-04-04 need to check if the bluetooth is connected
                String textContent = sendText.getText().toString();
                if(textContent!=null) {
                    messageList.add(new Message(msg_id,textContent,0));
                    hikingPalStorage.writeToMessages(msg_id,0,textContent);
                    msg_id++;
                    mAdapter.notifyDataSetChanged();
                    sendText.setText("");
                    if(mRecyclerView.getAdapter().getItemCount()>=1) {
                        mRecyclerView.smoothScrollToPosition(
                                mRecyclerView.getAdapter().getItemCount() - 1);
                    }
                    Log.d("msg", String.valueOf(messageList));
                }
            }
        });

        return ll;
    }


    public void hindkb() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getActivity().getCurrentFocus()!=null){
            if (getActivity().getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    // TODO: 2017-04-04 get data from bluetooth
    public void received_msg(String content){
        messageList.add(new Message(msg_id,content,1));
        hikingPalStorage.writeToMessages(msg_id,0,content);
        msg_id++;
        mAdapter.notifyDataSetChanged();
    }


}
