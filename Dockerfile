# 使用基础的 Java 镜像
FROM openjdk:11

# 设置工作目录
WORKDIR /app

# 将 JAR 包复制到工作目录
COPY test-platform.jar /app/

# 暴露应用程序使用的端口，这里假设应用使用 8080 端口，根据实际情况修改
EXPOSE 8080

# 定义启动命令
CMD ["java", "-jar", "test-platform.jar"]