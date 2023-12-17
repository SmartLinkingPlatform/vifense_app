package com.obd2.dgt.ui.InfoActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obd2.dgt.R;
import com.obd2.dgt.dbManage.TableInfo.MessageInfoTable;
import com.obd2.dgt.ui.AppBaseActivity;
import com.obd2.dgt.ui.ListAdapter.MessageList.MessageAdapter;
import com.obd2.dgt.ui.ListAdapter.MessageList.MessageItem;
import com.obd2.dgt.ui.MainActivity;
import com.obd2.dgt.utils.MyUtils;

import java.util.ArrayList;

public class MessageActivity extends AppBaseActivity {
    ImageView msg_all_delete_btn, msg_prev_btn;
    RecyclerView msg_list_recycle_view;
    ArrayList<MessageItem> messageItems = new ArrayList<>();
    MessageAdapter messageAdapter;
    Dialog dialog;
    private static MessageActivity instance;
    public static MessageActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        instance = this;

        initLayout();
    }

    private void initLayout() {
        msg_prev_btn = findViewById(R.id.msg_prev_btn);
        msg_prev_btn.setOnClickListener(view -> onMessagePrevClick());

        msg_all_delete_btn = findViewById(R.id.msg_all_delete_btn);
        msg_all_delete_btn.setOnClickListener(view -> onMessageAllDeleteClick());

        msg_list_recycle_view = findViewById(R.id.msg_list_recycle_view);
        LinearLayoutManager verticalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        msg_list_recycle_view.setLayoutManager(verticalLayoutManager);
        MessageItem item;
        for (int i = 0; i < MyUtils.messageInfo.size(); i++) {
            String[] info = MyUtils.messageInfo.get(i);
            item = new MessageItem(false, info[0], info[1], info[2], info[3], info[4]);
            messageItems.add(item);
        }
        messageAdapter = new MessageAdapter(getContext(), messageItems, messageListListener);
        msg_list_recycle_view.setAdapter(messageAdapter);
    }
    private MessageAdapter.ItemClickListener messageListListener = new MessageAdapter.ItemClickListener() {
        @SuppressLint({"ResourceAsColor", "NotifyDataSetChanged"})
        @Override
        public void onItemClick(View v, int position) {
            for (int i = 0; i < messageItems.size(); i++) {
                if (position == i) {
                    if (messageItems.get(i).selected) {
                        messageItems.get(i).selected = false;
                    } else {
                        messageItems.get(i).selected = true;
                    }
                    MessageInfoTable.updateMessageTable("show", "1", messageItems.get(i).id);
                } else {
                    messageItems.get(i).selected = false;
                }
            }
            messageAdapter.notifyDataSetChanged();
        }
    };
    private void onMessageAllDeleteClick(){
        showDialog();
    }
    @SuppressLint("ResourceType")
    private void showDialog() {
        runOnUiThread(() -> {
            dialog = new Dialog(MessageActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dlg_two_button);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView dialog_two_title_text = dialog.findViewById(R.id.dialog_two_title_text);
            dialog_two_title_text.setText(R.string.delete_message_title);
            TextView dialog_two_button_text = dialog.findViewById(R.id.dialog_two_content_text);
            dialog_two_button_text.setText(R.string.delete_all_message);
            dialog_two_button_text.setGravity(Gravity.CENTER);
            ImageView dialog_two_no_btn = dialog.findViewById(R.id.dialog_two_no_btn);
            dialog_two_no_btn.setOnClickListener(view -> {
                dialog.dismiss();
            });
            ImageView dialog_two_ok_btn = dialog.findViewById(R.id.dialog_two_ok_btn);
            dialog_two_ok_btn.setOnClickListener(view -> {
                MessageInfoTable.updateAllMessageTable();
                MessageInfoTable.getMessageInfoTable();

                MessageItem item;
                messageItems.clear();
                for (int i = 0; i < MyUtils.messageInfo.size(); i++) {
                    String[] info = MyUtils.messageInfo.get(i);
                    item = new MessageItem(false, info[0], info[1], info[2], info[3], info[4]);
                    messageItems.add(item);
                }
                messageAdapter.setData(messageItems);
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    public void onMessageShow(String id) {
        MessageInfoTable.updateMessageTable("show", "1", id);
        SystemClock.sleep(100);
        MessageInfoTable.getMessageInfoTable();
    }

    public void onMessageDeleteClick(String id){
        MessageInfoTable.updateMessageTable("active", "0", id);
        SystemClock.sleep(100);
        MessageInfoTable.getMessageInfoTable();

        MessageItem item;
        messageItems.clear();
        for (int i = 0; i < MyUtils.messageInfo.size(); i++) {
            String[] info = MyUtils.messageInfo.get(i);
            item = new MessageItem(false, info[0], info[1], info[2], info[3], info[4]);
            messageItems.add(item);
        }
        messageAdapter.setData(messageItems);
    }

    private void onMessagePrevClick(){
        MainActivity.getInstance().showMessageIcon();
        onLRChangeLayount(MessageActivity.this, MainActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.getInstance().showMessageIcon();
        onLRChangeLayount(MessageActivity.this, MainActivity.class);
        finish();
    }

}