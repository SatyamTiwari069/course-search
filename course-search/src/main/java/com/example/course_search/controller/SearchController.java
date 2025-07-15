package com.example.course_search.controller;

import com.example.course_search.service.CourseSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final CourseSearchService searchService;

    @GetMapping
    public Map<String, Object> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // This line will now work because the service has a correct return type.
        var searchHits = searchService.search(q, minAge, maxAge, category, type, minPrice, maxPrice, startDate, sort, page, size);

        // These lines will work because the compiler knows what `searchHits` is.
        var courses = searchHits.getSearchHits().stream().map(hit -> {
            var doc = hit.getContent();
            return Map.of(
                "id", doc.getId(),
                "title", doc.getTitle(),
                "category", doc.getCategory(),
                "price", doc.getPrice(),
                "nextSessionDate", doc.getNextSessionDate()
            );
        }).collect(Collectors.toList());

        return Map.of("total", searchHits.getTotalHits(), "courses", courses);
    }
}