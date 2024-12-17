FROM maven AS build

WORKDIR /app/khachSan

COPY . .

RUN mvn clean package

FROM openjdk:17-slim

WORKDIR /app/khachSan

COPY --from=build /app/khachSan/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

