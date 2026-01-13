## Implementation Steps:

1. **Set up the project structure** with the files above
2. **Run the application** using: `mvn spring-boot:run`
3. **Access the API documentation** at: `http://localhost:8080/swagger-ui.html`
4. **Access H2 database console** at: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:file:./data/cacheDB`
    - Username: `sa`
    - Password: (empty)
5. **Implemented scraping logic for IMDb** in the `ImdbScraperService` class
