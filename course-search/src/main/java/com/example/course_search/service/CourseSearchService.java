package com.example.course_search.service;

import com.example.course_search.document.CourseDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchHits<CourseDocument> search(
            String q, Integer minAge, Integer maxAge, String category, String type,
            Double minPrice, Double maxPrice, Date startDate, String sort, int page, int size) {

        Criteria criteria = new Criteria();

        // Full-text search
        if (q != null && !q.isBlank()) {
            criteria = criteria.subCriteria(new Criteria().or(new Criteria("title").matches(q))
                                                          .or(new Criteria("description").matches(q)));
        }

        // Filters
        if (minAge != null) {
            criteria = criteria.and(new Criteria("minAge").greaterThanEqual(minAge));
        }
        if (maxAge != null) {
            criteria = criteria.and(new Criteria("maxAge").lessThanEqual(maxAge));
        }
        if (category != null) {
            criteria = criteria.and(new Criteria("category").is(category));
        }
        if (type != null) {
            criteria = criteria.and(new Criteria("type").is(type));
        }
        if (minPrice != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(minPrice));
        }
        if (maxPrice != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(maxPrice));
        }
        if (startDate != null) {
            criteria = criteria.and(new Criteria("nextSessionDate").greaterThanEqual(startDate));
        }

        // Sorting
        Sort sortOrder = "priceAsc".equals(sort) ? Sort.by("price").ascending() :
                         "priceDesc".equals(sort) ? Sort.by("price").descending() :
                         Sort.by("nextSessionDate").ascending();

        CriteriaQuery query = new CriteriaQuery(criteria, PageRequest.of(page, size, sortOrder));

        return elasticsearchOperations.search(query, CourseDocument.class);
    }
}
