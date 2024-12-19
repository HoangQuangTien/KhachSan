FROM maven AS build

WORKDIR /app/khachSan

COPY . .

RUN mvn clean