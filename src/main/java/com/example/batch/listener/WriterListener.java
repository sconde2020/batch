package com.example.batch.listener;

import com.example.batch.model.Person;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemWriteListener;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Component;

@Component
public class WriterListener implements ItemWriteListener<Person> {

    private static final Logger log = LoggerFactory.getLogger(WriterListener.class);

    @Override
    public void beforeWrite(@NonNull Chunk<? extends Person> items) {
        log.debug("About to write {} items", items.size());
    }

    @Override
    public void afterWrite(@NonNull Chunk<? extends Person> items) {
        log.info("Successfully wrote {} items", items.size());
    }

    @Override
    public void onWriteError(@NonNull Exception ex, @NonNull Chunk<? extends Person> items) {
        log.error("Error writing {} items: {}", items.size(), ex.getMessage(), ex);
    }
}

