package com.example.batch.config;

import com.example.batch.listener.JobListener;
import com.example.batch.listener.ProcessorListener;
import com.example.batch.listener.ReaderListener;
import com.example.batch.listener.StepListener;
import com.example.batch.listener.WriterListener;
import com.example.batch.model.Person;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personReader")
                .resource(new ClassPathResource("data.csv"))
                .delimited()
                .names("id", "firstName", "lastName")
                .linesToSkip(1)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<Person, Person> processor() {
        return person -> {
            person.setFirstName(person.getFirstName().toUpperCase());
            return person;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .sql("INSERT INTO person (id, first_name, last_name) VALUES (:id, :firstName, :lastName)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Step importPersonStep(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     ItemReader<Person> reader,
                     ItemProcessor<Person, Person> processor,
                     ItemWriter<Person> writer,
                     StepListener stepListener,
                     ReaderListener readerListener,
                     ProcessorListener processorListener,
                     WriterListener writerListener) {

        return new StepBuilder("importPersonStep", jobRepository)
                .<Person, Person>chunk(10)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(stepListener)
                .listener(readerListener)
                .listener(processorListener)
                .listener(writerListener)
                .build();


    }

    @Bean
    public Job importPersonJob(JobRepository jobRepository, Step step, JobListener jobListener) {
        return new JobBuilder("importPersonJob", jobRepository)
                .listener(jobListener)
                .start(step)
                .build();
    }
}
