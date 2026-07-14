FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw

EXPOSE 8080

# Em vez de empacotar em um JAR rígido, rodamos o Spring dinamicamente em modo Dev
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=local"]