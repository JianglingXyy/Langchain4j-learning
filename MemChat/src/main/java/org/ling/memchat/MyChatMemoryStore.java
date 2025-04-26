package org.ling.memchat;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

import java.util.ArrayList;
import java.util.List;
//自定义chatmemoryStore，每次add的时候都会update，我们可以在updateMessage中自己实现持久化存储
public class MyChatMemoryStore implements ChatMemoryStore {
    private List<ChatMessage> messages = new ArrayList<ChatMessage>();
    private Object id;
    @Override
    public List<ChatMessage> getMessages(Object o) {
        return messages;
    }
    @Override
    public void updateMessages(Object o, List<ChatMessage> list) {
        if(!o.equals(id)) {
            System.out.println("error");
        }
        else{
            messages = list;
            System.out.println("updating success");
        }
    }
    @Override
    public void deleteMessages(Object o) {
        messages.clear();
        System.out.println("deleting success");
    }
}
