#!/bin/bash

# Script pour construire et démarrer l'application avec Docker

echo "Construction de l'application avec Maven..."
mvn clean package -DskipTests

echo "Construction des images Docker..."
docker-compose build

echo "Démarrage des conteneurs..."
docker-compose up -d

echo "L'application est accessible à l'adresse http://localhost:8080"
echo "La documentation Swagger est disponible à l'adresse http://localhost:8080/swagger-ui.html"
