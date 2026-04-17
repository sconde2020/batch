package com.example.batch.processor;

import com.example.batch.exception.InvalidPersonException;
import com.example.batch.exception.TransientPersonException;
import com.example.batch.model.Person;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private final Map<Long, Integer> attemptsById = new ConcurrentHashMap<>();

    @Override
    public Person process(Person person) {
        if (person.getFirstName() == null || person.getFirstName().isBlank()
                || person.getLastName() == null || person.getLastName().isBlank()) {
            throw new InvalidPersonException("Missing firstName/lastName");
        }

        if ("RETRY".equalsIgnoreCase(person.getFirstName())) {
            int attempt = attemptsById.merge(person.getId(), 1, Integer::sum);
            if (attempt < 3) {
                throw new TransientPersonException("Simulated transient error attempt=" + attempt);
            }
        }

        person.setFirstName(person.getFirstName().toUpperCase());
        return person;
    }
}

