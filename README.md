# CPI Transport Tool

A simple Java GUI application to transport SAP CPI iFlows from Dev to QA/Prod environments with OAuth 2.0 and Basic Auth support.

## ✅ Features
- Select package → iFlow → Target Environment (QA/Prod)
- Transport with one click
- Editable `config.properties` for credentials

## 📦 Prerequisites (macOS)

- Java 17+
- [Maven](https://maven.apache.org/)
- Run:
  ```bash
  mvn clean package
  java -jar target/cpi-transport-tool-1.0.0-jar-with-dependencies.jar
