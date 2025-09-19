# 📂 folder-ai


**Instantly generate structured learning paths and project folders from a single prompt.**

`folder-ai` tackles the "blank page" problem when starting a new project or learning a new skill. Instead of wondering what topics to cover or how to structure your files, simply provide a prompt like "learn advanced java" and let AI build a comprehensive directory structure for you. 🚀



This project provides a logical, step-by-step folder hierarchy that acts as a roadmap for your learning journey or a solid foundation for your new project.

---

## ## Architecture Overview 🏗️

The project is divided into two main components:

* **`folderai-service` (Backend):** A Spring Boot application that uses **Spring AI** to connect with large language models. It manages conversation context with `ChatMemory` and exposes a simple REST API to generate the directory structure. **(Status: Complete ✅)**
* **`folderai-web` (Frontend):** A Vue.js 3 application that will provide a user-friendly chat interface to interact with the backend service. **(Status: Not Started / Help Wanted 🙋‍♀️)**

The two services communicate via a RESTful API. The frontend sends a user's prompt to the backend, which then returns the generated folder structure for display.

---

## ## Features ✨

* **AI-Powered Scaffolding:** Leverage the power of modern LLMs to generate meaningful and structured directories.
* **Context-Aware:** Uses Spring AI's `ChatMemory` to remember previous interactions for more refined results.
* **RESTful API:** A simple and clean API makes the backend service easy to integrate with any frontend or tool.
* **Open for Expansion:** The project is open-source and ready for new features and contributors.

---

## ## Getting Started

Follow these instructions to get the backend service up and running on your local machine.

### ### Prerequisites

Make sure you have the following installed:

* **Java Development Kit (JDK):** Version 21.
* **Maven:** For dependency management and building the project.
* **LLM API Key:** An API key from an AI provider like OpenAI, Google, etc. The service is configured for OpenAI by default.

### ### Installation & Running the Backend

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/RamiRBY/folder-ai.git
    cd folder-ai/folderai-service
    ```

2.  **Configure your API Key:**
    Open the `src/main/resources/application.yml` file and add your OpenAI API key:
    ```properties
    # For OpenAI
		spring:
		  ai:
			openai:
			  apikey: ${OPENAI_API_KEY}


3.  **Build the project:**
    Use Maven to compile the application and download dependencies.
    ```bash
    mvn clean install
    ```

4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The server will start on `http://localhost:8080`.

---

## ## API Usage

You can test the API using any HTTP client, like cURL or Postman.

* **Endpoint:** `POST /api/project-directory`
* **Body:** A JSON object with a `prompt` key.
* **Example Request:**

    ```bash
    curl -X POST http://localhost:8080/api/project-directory \
         -H "Content-Type: application/json" \
         -d '{"prompt": "A project plan to learn Java"}'
    ```

* **Example Response:**
    The API returns a plain text response representing the folder structure.

	projectName: `learn-spring`
	```tree
	learn-spring/
	├── prerequisites/
	│   ├── java-basics/
	│   ├── oops/
	│   └── maven-gradle/
	├── core/
	│   ├── ioc/
	│   ├── di/
	│   ├── beans-lifecycle/
	│   └── applicationcontext/
	├── spring-mvc/
	│   ├── controllers/
	│   ├── views/
	│   └── rest-api/
	├── spring-boot/
	│   ├── auto-configuration/
	│   ├── starters/
	│   └── spring-cli/

    ```

---

