# --- Estágio 1: O Construtor (Build) ---
# Usa a imagem Maven com o JDK 21 para construir o projeto
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

# --- Estágio 2: O Executor (Runtime) ---
# Usa uma imagem Java 21 enxuta (slim) para rodar a aplicação
FROM eclipse-temurin:21-jre-jammy

# Define o diretório de trabalho
WORKDIR /app

# Copia APENAS o .jar construído do estágio anterior
# Substitua 'uex-map-application-1.0-SNAPSHOT.jar' se o seu artifactId ou version forem diferentes
COPY --from=builder /app/target/uex-map-application-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]