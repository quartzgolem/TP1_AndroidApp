package com.example.final_ig2i;

import java.util.ArrayList;

public class ListeConversations {

    private ArrayList<Conversation> list;

    public ListeConversations() {
        this.list = new ArrayList<Conversation>();
    }

    public ArrayList<Conversation> getList() {
        return list;
    }

//    public void addConversation(Conversation c) {
//        list.add(c);
//    }

    public void setList(ArrayList<Conversation> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ListeConversations{" +
                "list=" + list +
                '}';
    }
}
