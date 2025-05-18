FROM eclipse-temurin:17-jdk

# Création du dossier de travail
WORKDIR /app

# Copie du JAR compilé
COPY target/*.jar app.jar

# Exposition du port de l'app (optionnel)
EXPOSE 8080

# Lancement de l'app
ENTRYPOINT ["java", "-jar", "app.jar"]
