package com.example.batch.listener;

import com.example.batch.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class BatchSkipListener implements SkipListener<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(BatchSkipListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("Skipped item during read: {}", t.getMessage(), t);
    }

    @Override
    public void onSkipInProcess(Person item, Throwable t) {
        log.warn("Skipped item during process: id={} reason={}", item.getId(), t.getMessage(), t);
    }

    @Override
    public void onSkipInWrite(Person item, Throwable t) {
        log.warn("Skipped item during write: id={} reason={}", item.getId(), t.getMessage(), t);
    }
}

