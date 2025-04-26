package org.ling.common.configuration;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.ling.common.Assistant;
import org.ling.common.MemAssistant;
import org.ling.common.MemDepartAssistant;
import org.ling.common.MemStreamAssistant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class CommonAutoConfiguration {
    private static final String API_KEY = "YOUR_API_KEY";
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return QwenChatModel.builder().baseUrl(" https://dashscope.aliyuncs.com/api/v1")
                .apiKey(API_KEY)
                .modelName("qwen-turbo")
                .temperature(0.8F)
                .build();
    }
    @Bean
    public StreamingChatLanguageModel streamingChatLanguageModel() {
        return QwenStreamingChatModel.builder().baseUrl(" https://dashscope.aliyuncs.com/api/v1")
                .apiKey(API_KEY)
                .modelName("qwen-turbo")
                .temperature(0.8F)
                .build();
    }

    @Bean
    public Assistant assistant() {
        return AiServices.create(Assistant.class,chatLanguageModel());
    }
    @Bean
    public MemAssistant memAssistant() {
        return AiServices.builder(MemAssistant.class).chatLanguageModel(chatLanguageModel())
                .chatMemory(MessageWindowChatMemory.builder().maxMessages(20).build()).build();
    }
    @Bean
    public MemDepartAssistant memDepartAssistant() {
        return AiServices.builder(MemDepartAssistant.class).chatLanguageModel(chatLanguageModel())
                .chatMemoryProvider(new ChatMemoryProvider() {
                    @Override
                    public ChatMemory get(Object o) {
                        return MessageWindowChatMemory.builder().maxMessages(20).build();
                    }
                }).build();
    }
    @Bean
    public MemStreamAssistant memStreamAssistant() {
        return AiServices.builder(MemStreamAssistant.class).streamingChatLanguageModel(streamingChatLanguageModel())
                .chatMemoryProvider(new ChatMemoryProvider() {
                    @Override
                    public ChatMemory get(Object o) {
                        return MessageWindowChatMemory.builder().maxMessages(20).build();
                    }
                })
                .build();
    }
}
