package com.example.batch.listener;

import com.example.batch.model.Person;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemProcessListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessorListener implements ItemProcessListener<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(ProcessorListener.class);

    @Override
    public void beforeProcess(@NonNull Person item) {
        log.debug("About to process: id={}", item.getId());
    }

    @Override
    public void afterProcess(@NonNull Person item, @Nullable Person result) {
        if (result != null) {
            log.debug("Processed: id={} firstName transformed to '{}'", item.getId(), result.getFirstName());
        } else {
            log.debug("Item filtered out: id={}", item.getId());
        }
    }

    @Override
    public void onProcessError(@NonNull Person item, @NonNull Exception ex) {
        log.error("Error processing item id={}: {}", item.getId(), ex.getMessage(), ex);
    }
}

