#!/bin/sh
# Starts the API with an in-memory database (no MySQL needed). Requires JDK 17+ and Maven.
mvn spring-boot:run -Dspring-boot.run.profiles=h2
