package org.ling.memchat;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenTokenizer;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.ling.common.Assistant;
import org.ling.common.MemAssistant;
import org.ling.common.MemDepartAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/mem")
public class MemController {
    private final static String API_KEY = "YOUR_API_KEY";
    private final static String MODEL_NAME = "qwen-turbo";
    @Autowired
    private ChatLanguageModel chatLanguageModel;
    //简单的MessageWindow
    private final ChatMemory simpleMessageWindow = MessageWindowChatMemory.builder().
            chatMemoryStore(new InMemoryChatMemoryStore()).maxMessages(10).id("ling").build();
    //简单的TokenWindow,注意这里的Token尽量设置大一点，小了会报错
    private final ChatMemory simpleTokenWindow = TokenWindowChatMemory.builder().
            maxTokens(1000,new QwenTokenizer(API_KEY,MODEL_NAME)).chatMemoryStore(new InMemoryChatMemoryStore())
            .id("wang").build();
    //对于不同userID的请求，做记忆分离，事实证明和高级API的思路是一样的，它也是用ConcurrentHashMap实现的
    private final static ConcurrentHashMap<String,ChatMemory> map = new ConcurrentHashMap<>(16);
    @Autowired//高级API
    private MemAssistant memAssistant;
    @Autowired//用户分离记忆高级API
    private MemDepartAssistant memDepartAssistant;
    @GetMapping("/low/simpleMessageWindow")
    public String simpleMessageWindow(@RequestParam(value = "message") String message) {
        simpleMessageWindow.add(UserMessage.from(message));//往window里边放消息
        ChatResponse chat = chatLanguageModel.chat(simpleMessageWindow.messages());//得到返回结果，同时也要把返回消息放到window里边
        simpleMessageWindow.add(chat.aiMessage());
        return chat.aiMessage().text();
    }
    @GetMapping("/low/simpleTokenWindow")
    public String simpleTokenWindow(@RequestParam(value = "message") String message) {
        simpleTokenWindow.add(UserMessage.from(message));//往window里边放消息
        ChatResponse chat = chatLanguageModel.chat(simpleTokenWindow.messages());//得到返回结果，同时也要把返回消息放到window里边
        simpleTokenWindow.add(chat.aiMessage());
        return chat.aiMessage().text();
    }
    @GetMapping("/low/departMessageWindow")
    public String departMessage(@RequestParam(value = "id") String id,@RequestParam(value = "message") String message) {
        if(!map.containsKey(id)) {
            map.put(id,MessageWindowChatMemory.builder().chatMemoryStore(new InMemoryChatMemoryStore()).
                    maxMessages(20).id(id).build());
        }
        ChatMemory memory = map.get(id);
        memory.add(UserMessage.from(message));
        ChatResponse chat = chatLanguageModel.chat(memory.messages());
        memory.add(chat.aiMessage());
        return chat.aiMessage().text();
    }
    /**
     * 前边的都是低级API，需要自己对消息进行处理，下边是高级API，还是借助AIService类,详见Common的mAssistant的创建
     */
    @GetMapping("/high/simpleMessageWindow")
    public String highSimpleWindowMessage(@RequestParam(value = "message") String message) {
        return memAssistant.chat(message);
    }

    @GetMapping("/high/departMessage")
    public String highDepartWindowMessage(@RequestParam(value = "id") String id,@RequestParam(value = "message") String message) {
        return memDepartAssistant.chat(id,message);
    }

}
