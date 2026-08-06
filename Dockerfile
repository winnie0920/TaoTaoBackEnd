# ============ Stage 1: Build ============
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# 先只複製 pom 相關檔案，讓 Maven 依賴下載這一層可以被 Docker cache
# （原始碼沒改的話，重新 build 就不用重抓依賴，加快建置速度）
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY parent/pom.xml parent/pom.xml
COPY pojo/pom.xml pojo/pom.xml
COPY management/pom.xml management/pom.xml

RUN chmod +x mvnw

# 用 go-offline 先把所有模組需要的依賴抓下來（可省略，但能加速重建）
RUN ./mvnw -B dependency:go-offline -pl parent,pojo,management -am || true

# 複製剩下的原始碼
COPY parent parent
COPY pojo pojo
COPY management management

# 打包整個多模組專案，只 build management 及其依賴的模組 (parent, pojo)
# -DskipTests 可依需求拿掉，讓 CI 中還是跑測試
RUN ./mvnw -B clean package -pl management -am -DskipTests

# ============ Stage 2: Runtime ============
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# 建立非 root 使用者執行，較安全
RUN useradd -r -u 1001 -m spring
USER spring

# 從 build 階段複製最終產出的 app.jar
# 如果 management 模組打包後檔名不是 app.jar，請依實際路徑調整
COPY --from=build /app/management/target/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
