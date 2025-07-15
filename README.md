# Course Search API with Spring Boot and Elasticsearch

This project is a Spring Boot application that provides a powerful search API for a catalog of courses. It indexes course data into Elasticsearch and exposes a RESTful endpoint to search, filter, sort, and paginate through the results.

## Features

-   **Full-Text Search:** Search across course titles and descriptions.
-   **Advanced Filtering:** Filter courses by category, type, age range, price range, and upcoming session dates.
-   **Flexible Sorting:** Sort results by relevance, price (ascending/descending), or soonest upcoming session.
-   **Pagination:** Easily navigate through large result sets.
-   **Automatic Data Ingestion:** Sample course data is automatically indexed into Elasticsearch on the first application startup.

## Prerequisites

Before you begin, ensure you have the following installed on your system:

-   **Java Development Kit (JDK):** Version 17 or newer.
-   **Apache Maven:** Version 3.8.x or newer, to build the project.
-   **Docker and Docker Compose:** To run the Elasticsearch instance.

## Getting Started

Follow these steps to set up the environment and run the application.

### 1. Set Up Elasticsearch

This project uses Docker Compose to run a single-node Elasticsearch cluster.

**Start the Cluster:**
Navigate to the project's root directory (where `docker-compose.yml` is located) and run the following command:

```bash
docker-compose up -d
```

This will download the Elasticsearch 8.x image if you don't have it and start the container in the background.

**Verify Elasticsearch is Running:**
To confirm that the cluster is running, execute the following `curl` command.

```bash
curl http://localhost:9200

```

You should see a JSON response with details about the Elasticsearch cluster, similar to this:

```json
{
  "name" : "es01",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "...",
  "version" : {
    "number" : "8.14.0",
    ...
  },
  "tagline" : "You Know, for Search"
}
```
![alt text](<Screenshot 2025-07-16 024508.png>)


### 2. Build the Application

Use Maven to compile the source code and package it into an executable JAR file.

```bash
mvn clean install
```

### 3. Run the Application

Once the build is complete, run the application using the following command:

```bash
java -jar target/course-search-0.0.1-SNAPSHOT.jar
```

The application will start up(i used port=8081) and connect to the Elasticsearch instance running on `localhost:9200`.

## Data Ingestion

-   **Automatic Indexing:** On the very first startup, the application detects that the `courses` index does not exist. It then reads the `src/main/resources/sample-courses.json` file and bulk-indexes all 50+ course objects into Elasticsearch.
-   **Verification:** You can verify that the data has been ingested by checking the document count in the `courses` index:
    ```bash
    curl -X GET "localhost:9200/courses/_count"
    ```
    This should return a count of 50 or more.
-   **Re-triggering Ingestion:** To force the application to re-index the data, simply delete the Elasticsearch index and restart the application:
    ```bash
    curl -X DELETE "localhost:9200/courses"
    ```

## Project Configuration

The application is configured to connect to Elasticsearch out-of-the-box. If you need to change the connection settings (e.g., if Elasticsearch is running on a different host or port), you can modify the `application.properties` file.

**File:** `src/main/resources/application.properties`

```properties
# The URI of the Elasticsearch cluster
spring.elastic-search.uris=http://localhost:9200
```

## API Documentation

The application exposes a single, powerful search endpoint.

### Endpoint

`GET /api/search`

### Query Parameters

| Parameter     | Type    | Description                                                                                             | Default      |
|---------------|---------|---------------------------------------------------------------------------------------------------------|--------------|
| `q`           | String  | A search keyword for full-text search on `title` and `description`.                                     | (none)       |
| `minAge`      | Integer | The minimum age for the course.                                                                         | (none)       |
| `maxAge`      | Integer | The maximum age for the course.                                                                         | (none)       |
| `category`    | String  | The exact category of the course (e.g., "Arts", "Science", "Technology").                               | (none)       |
| `type`        | String  | The exact type of course. Values: `ONE_TIME`, `COURSE`, or `CLUB`.                                      | (none)       |
| `minPrice`    | Double  | The minimum price of the course.                                                                        | (none)       |
| `maxPrice`    | Double  | The maximum price of the course.                                                                        | (none)       |
| `startDate`   | String  | Show courses on or after this date. Format: `YYYY-MM-DD`.                                               | (none)       |
| `sort`        | String  | The sorting order. Values: `upcoming` (by date), `priceAsc`, `priceDesc`.                               | `upcoming`   |
| `page`        | Integer | The page number for pagination (0-indexed).                                                             | `0`          |
| `size`        | Integer | The number of results per page.                                                                         | `10`         |

### Response Format

The API returns a JSON object containing the total number of matching courses and an array of the course documents for the current page.

```json
{
    "total": 15,
    "courses": [
        {
            "id": "course-01",
            "title": "Introduction to Python Programming",
            "category": "Technology",
            "price": 149.99,
            "nextSessionDate": "2025-08-15T10:00:00Z"
        },
        // ... more courses
    ]
}
```
![alt text](<Screenshot 2025-07-16 024450.png>)



### Usage Examples (`curl`)

1.  **Search for a keyword:** Find all courses related to "art".

    ```bash
    curl "http://localhost:8081/api/search?q=art"
    ```
    ![alt text](<Screenshot 2025-07-16 025418.png>)

2.  **Filter by category and price:** Find "Science" courses that cost between $50 and $100.

    ```bash
    curl "http://localhost:8081/api/search?category=Science&minPrice=50&maxPrice=100"
    ```
![alt text](<Screenshot 2025-07-16 025055.png>)

3.  **Sort by price (low to high):**

    ```bash
    curl "http://localhost:8081/api/search?sort=priceAsc"
    ```
    ![alt text](<Screenshot 2025-07-16 025201.png>)

4.  **Find courses for a specific age range starting after a certain date:**

    ```bash
    curl "http://localhost:8081/api/search?minAge=10&maxAge=14&startDate=2025-09-01"
    ```
![alt text](<Screenshot 2025-07-16 025243.png>)


5.  **Use pagination:** Get the second page of results for "math" courses.

    ```bash
    curl "http://localhost:8081/api/search?q=math&page=1&size=5"
    ```
![alt text](<Screenshot 2025-07-16 025336.png>)

## Implementation Details

-   **Project Initialization:** The project was created using Spring Initializr with dependencies for `Spring Web`, `Spring Data Elasticsearch`, and `Lombok`.
-   **Elasticsearch Client:** The application uses the modern `elasticsearch-java` client, which is now required for Spring Boot 3+ applications.
-   **Course Document Structure:** The `CourseDocument.java` entity uses Spring Data annotations (`@Document`, `@Field`) to map Java fields to the correct Elasticsearch types (`text`, `keyword`, `integer`, `double`, `date`).
-   **Search Service Logic:** The `CourseSearchService` builds a dynamic boolean query using the modern Elasticsearch Java Client's fluent DSL. Full-text search uses a `multi_match` query on the `must` clause, while all other filters are added to the more efficient `filter` clause.
-   **Testing:** No integration tests were included as this part was optional, but the project structure is set up to easily accommodate them in the `src/test/java` directory.
