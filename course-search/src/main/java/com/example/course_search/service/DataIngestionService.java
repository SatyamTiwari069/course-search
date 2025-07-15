package com.example.course_search.service;

import com.example.course_search.document.CourseDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataIngestionService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void ingestData() throws Exception {
        if (!elasticsearchOperations.indexOps(CourseDocument.class).exists()) {
            ClassPathResource resource = new ClassPathResource("sample-courses.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<CourseDocument> courses = objectMapper.readValue(inputStream, new TypeReference<>() {});
                elasticsearchOperations.save(courses);
            }
        }
    }
}