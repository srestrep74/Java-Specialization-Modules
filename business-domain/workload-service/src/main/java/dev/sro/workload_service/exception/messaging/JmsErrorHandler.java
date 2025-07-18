package dev.sro.workload_service.exception.messaging;


import org.springframework.lang.NonNull;
import org.springframework.util.ErrorHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JmsErrorHandler implements ErrorHandler {
    
    @Override
    public void handleError(@NonNull Throwable t) {
        log.error("Error processing JMS message: {}", t.getMessage(), t);
    }
}
