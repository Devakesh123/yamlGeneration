# yamlGeneration
This repo is used to create swagger YAML by using UI

# 🧾 YAML Generator - OpenAPI 3.0 Spec

A lightweight Spring Boot application that allows users to input API operation details via a browser, generate a valid OpenAPI 3.0 YAML file, store metadata in MySQL, and download the YAML file directly from the browser.

---

## 📌 Features

- 📝 Dynamic form-based API spec creation
- 📂 Generates valid OpenAPI 3.0 YAML (Swagger compatible)
- 💾 Stores API metadata in MySQL
- ⬇️ Download YAML files directly via browser
- 🧠 Auto-detects path parameters
- 🔁 Converts JSON input to OpenAPI-compatible schema

---

## 🛠 Tech Stack

- **Java 17+**
- **Spring Boot**
- **MySQL**
- **SnakeYAML**
- **Lombok**
- **HTML + JS** (for frontend)

---

## 🚀 How It Works

1. User fills in operation details (operation name, HTTP method, URL, request/response JSON).
2. Data is posted to the Java backend (`/generate-yaml`).
3. Backend:
   - Saves metadata to MySQL
   - Converts JSON to OpenAPI YAML format
   - Generates a downloadable `.yaml` file
4. User receives a link to download the OpenAPI YAML.

---

## 📂 API Endpoints

### `POST /generate-yaml`
Generates OpenAPI YAML based on user input.

#### Request Body:
```json
{
  "operationName": "getUser",
  "url": "/users/{id}",
  "method": "GET",
  "requestBodyJson": "{\"id\": \"123\"}",
  "responseBodyJson": "{\"name\": \"John\", \"email\": \"john@example.com\"}"
}
