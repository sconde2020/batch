package com.example.batch.listener;

import com.example.batch.model.Person;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
public class ReaderListener implements ItemReadListener<Person> {

    private static final Logger log = LoggerFactory.getLogger(ReaderListener.class);

    @Override
    public void beforeRead() {
        log.debug("About to read next item");
    }

    @Override
    public void afterRead(@NonNull Person item) {
        log.debug("Read item: id={} name={} {}", item.getId(), item.getFirstName(), item.getLastName());
    }

    @Override
    public void onReadError(@NonNull Exception ex) {
        log.error("Error reading item: {}", ex.getMessage(), ex);
    }
}
