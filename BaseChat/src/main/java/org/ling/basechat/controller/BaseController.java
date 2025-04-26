package org.ling.basechat.controller;


import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.ling.common.Assistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//主要看的是chat方法
@RestController
@RequestMapping("/base")
public class BaseController {
    @Autowired
    private ChatLanguageModel chatLanguageModel;
    @Autowired
    private Assistant assistant;
    /**
     * low api
     * @param message
     * @return
     */
    @RequestMapping("/low/chat")
    public String baseLowChat(@RequestParam(value = "message") String message){
         //return chatLanguageModel.chat(UserMessage.from(message)).aiMessage().text();
         return chatLanguageModel.chat(ChatRequest.builder().messages(UserMessage.from("123")).
                 build()).aiMessage().text();
    }
    @RequestMapping("/high/chat")
    public String baseHighChat(@RequestParam(value = "message") String message){
            return assistant.chat(message);
    }

}