package org.ling.streamchat;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.TokenStream;
import jakarta.servlet.http.HttpServletResponse;
import org.ling.common.MemStreamAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.function.Consumer;

@RestController
@CrossOrigin(origins = "*")
public class StreamController {
    @Autowired
    private  MemStreamAssistant streamAssistant;
    @Autowired
    private StreamingChatLanguageModel languageModel;
    @GetMapping("/low/chat")
    public SseEmitter streamLowChat(@RequestParam(value = "message") String message){
        SseEmitter emitter = new SseEmitter();
        languageModel.chat(message, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String s) {
                try {
                    emitter.send(SseEmitter.event().data(s));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {

            }
            @Override
            public void onError(Throwable throwable) {

            }
        });
        return emitter;
    }
    @GetMapping(path="/high/chat")
    public SseEmitter streamMemChat(@RequestParam(value = "id") String id, @RequestParam(value = "message") String message){
        SseEmitter emitter = new SseEmitter();
        TokenStream chat = streamAssistant.chat(id, message);
        chat.onPartialResponse(new Consumer<String>() {
            @Override
            public void accept(String s) {
                try {
                    emitter.send(SseEmitter.event().data(s));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).onError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
            }
        }).start();
        return emitter;
    }
}
