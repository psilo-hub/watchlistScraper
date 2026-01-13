# Spring Boot based microservice with REST API for scraping infos about Movies and Tv Shows from pages like [IMDb](https://www.imdb.com)

This is part of a "watchlist app" project I'm working on

## What works so far

### Scraping IMDb 
- Search movies and Tv Shows
- Get details about movies, tv shows, seasons, episodes
  - rating
  - actors/actresses
  - runtime
  - release date
  - poster url
  - ...

### Caching

Downloaded pages get cached (retention period: 3 days)

## How to try it

- Clone this repo.
- Start the service using: `mvn spring-boot:run`
- Head to [localhost:8080](localhost:8080). This will redirect you to a Swagger UI that lists the API endpoints.

### Examples

**Search for movies or tv shows:**

![movieSearch](./picsForReadme/movieSearch.jpg)

**Fetching details about a movie or tv show:**

![movieSearch](./picsForReadme/movieDetails.jpg)

## Planned

- Scraping other sites like TMDb
- Search for sources (torrents, streams, etc.)

## Todo

You can find details [here](./src/main/resources/todo.md).
Wanna help? I'm happy to receive PRs.

