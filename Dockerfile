# Використовуємо офіційний образ OpenJDK як базовий
# Use the official OpenJDK image as base
# Используем официальный образ OpenJDK как базовый
FROM openjdk:11-jre-slim

# Встановлюємо робочу директорію
# Set the working directory
# Устанавливаем рабочую директорию
WORKDIR /app

# Копіюємо jar-файл з=target директорії
# Copy the jar file from the target directory
# Копируем jar-файл из=target директории
COPY target/sparky-kubernetes-operator-1.0-SNAPSHOT.jar app.jar

# Відкриваємо порт (якщо потрібно)
# Expose port (if needed)
# Открываем порт (если нужно)
EXPOSE 8080

# Встановлюємо точку входу
# Set the entry point
# Устанавливаем точку входа
ENTRYPOINT ["java", "-jar", "app.jar"]