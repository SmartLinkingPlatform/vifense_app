package com.obd2.dgt.ui.ListAdapter.MessageList;

public class MessageItem {
    public boolean selected;
    public String id;
    public String msg_time;
    public String msg_user;
    public String msg_title;
    public String msg_content;

    public MessageItem(boolean selected, String id, String msg_time, String msg_user, String msg_title, String msg_content){
        this.selected = selected;
        this.id = id;
        this.msg_time = msg_time;
        this.msg_user = msg_user;
        this.msg_title = msg_title;
        this.msg_content = msg_content;
    }
}
