package org.ling.common;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface MemStreamAssistant {

    TokenStream chat(@MemoryId String id, @UserMessage String message);
}
