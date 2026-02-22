# ──────────────────────────────────────────────────────────────────────────────
# Stage 1: Build
# ──────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copia os wrappers do Maven primeiro (camada cacheável)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Baixa as dependências (aproveitando cache do Docker se o pom.xml não mudou)
RUN ./mvnw dependency:go-offline -q

# Copia o código-fonte e compila
COPY src/ src/
RUN ./mvnw package -DskipTests -q

# Extrai as camadas do fat JAR para melhorar o cache de layers
RUN java -Djarmode=layertools -jar target/*.jar extract --destination target/extracted

# ──────────────────────────────────────────────────────────────────────────────
# Stage 2: Runtime
# ──────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine AS runtime

# Usuário não-root para segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# Copia as camadas extraídas (ordem importa para cache eficiente)
COPY --from=builder /app/target/extracted/dependencies/          ./
COPY --from=builder /app/target/extracted/spring-boot-loader/   ./
COPY --from=builder /app/target/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/target/extracted/application/          ./

# Porta da aplicação
EXPOSE 8080

# Health check básico
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/health || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "org.springframework.boot.loader.launch.JarLauncher"]