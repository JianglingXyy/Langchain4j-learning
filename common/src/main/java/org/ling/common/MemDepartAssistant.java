package org.ling.common;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface MemDepartAssistant {
    String chat(@MemoryId String memoryId, @UserMessage String message);
}
