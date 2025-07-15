package com.example.course_search.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "courses")
public class CourseDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Keyword, name = "category")
    private String category;

    @Field(type = FieldType.Keyword, name = "type")
    private String type;

    @Field(type = FieldType.Integer, name = "minAge")
    private Integer minAge;

    @Field(type = FieldType.Integer, name = "maxAge")
    private Integer maxAge;

    @Field(type = FieldType.Double, name = "price")
    private Double price;

    @Field(type = FieldType.Date, name = "nextSessionDate")
    private Date nextSessionDate;
}