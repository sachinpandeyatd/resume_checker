# AI Resume Checker

AI Resume Checker is a full-stack web application that allows users to upload their resumes (PDF, DOCX, TXT) and receive an AI-powered analysis, providing feedback on strengths, areas for improvement, and overall suitability for software engineering roles. This project utilizes React with Tailwind CSS for the frontend, Spring Boot for the backend, and integrates with the Google Gemini Pro API for resume analysis.

## Features

*   **User-Friendly Interface:** Simple drag-and-drop or click-to-select file upload.
*   **Multiple File Formats Supported:** Accepts PDF, DOCX, and TXT resume files.
*   **AI-Powered Analysis:** Leverages Google's Gemini Pro API to:
    *   Identify strengths in the resume.
    *   Pinpoint areas for improvement with actionable suggestions.
    *   Provide an overall assessment of suitability for software engineering roles.
*   **Structured Feedback:** Analysis is presented in clear, easy-to-read sections.
*   **Responsive Design:** UI adapts to different screen sizes.

## Tech Stack

**Frontend:**

*   **React.js (v18.x):** JavaScript library for building user interfaces.
*   **Vite:** Next-generation frontend tooling (fast development server and build tool).
*   **Tailwind CSS (v3.x):** A utility-first CSS framework for rapid UI development.
*   **`react-dropzone`:** For handling file uploads with drag-and-drop.
*   **`react-markdown` & `remark-gfm`:** For rendering AI-generated Markdown feedback.
*   **`@tailwindcss/typography`:** For styling Markdown content.
*   **`axios` (or `fetch` API):** For making API calls to the backend.
*   **`react-icons` (Optional):** For UI icons.

**Backend:**

*   **Java (JDK 17+ recommended):** Programming language.
*   **Spring Boot (v3.x.x):** Framework for building robust Java applications.
    *   Spring Web (for REST APIs)
    *   Spring Data (if database integration is added later)
*   **Maven (or Gradle):** Build automation tool.
*   **Apache PDFBox:** For extracting text from PDF files.
*   **Apache POI:** For extracting text from DOCX files.
*   **Jackson:** For JSON serialization/deserialization.
*   **SLF4J & Logback:** For logging.

**AI Integration:**

*   **Google Gemini Pro API:** (via `generativelanguage.googleapis.com`)

**Development Environment:**

*   **Node.js (v18.x+ recommended):** For running the frontend.
*   **npm (or yarn/pnpm):** Package manager for Node.js.
*   **Git:** Version control.

## Project Structure
resume_checker/
├── frontend/ # React (Vite) Application
│ ├── public/
│ ├── src/
│ ├── package.json
│ ├── tailwind.config.js
│ ├── vite.config.js
│ └── ...
│
└── backend/ # Spring Boot Application
├── src/
│ ├── main/
│ │ ├── java/com/yourdomain/resumechecker/ # Main Java code
│ │ └── resources/
│ │ ├── application.properties # Spring Boot config (placeholders)
│ │ └── static/ # (if serving static files from backend)
│ │ └── templates/ # (if using server-side templates)
│ └── test/
├── pom.xml # Maven project file
└── .env # Local environment variables (GITIGNORED!) - Contains API Key


## Prerequisites

*   **Java Development Kit (JDK):** Version 17 or higher.
*   **Apache Maven:** Version 3.6 or higher (or Gradle).
*   **Node.js:** Version 18.x or higher.
*   **npm (or yarn/pnpm):** Comes with Node.js.
*   **Git:** For version control.
*   **Google Gemini API Key:**
    1.  Go to [Google AI Studio](https://aistudio.google.com/app/apikey) (or your Google Cloud Console for Vertex AI).
    2.  Create a new API key.
    3.  **Important:** Keep this key secure and do not commit it to version control.

## Setup and Installation

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/sachinpandeyatd/resume_checker.git
    cd resume_checker
    ```

2.  **Backend Setup (Spring Boot):**
    *   Navigate to the `backend` directory:
        ```bash
        cd backend
        ```
    *   Create a `.env` file in the `backend` directory (this file is gitignored):
        ```
        GEMINI_API_KEY=YOUR_NEW_GEMINI_API_KEY_HERE
        # Optional: GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent
        ```
        Replace `YOUR_NEW_GEMINI_API_KEY_HERE` with your actual Gemini API key.
    *   (Optional, if using `java-dotenv` for `.env` loading as shown in development):
        Ensure the `java-dotenv` dependency is in `pom.xml` and the main application class loads it.
    *   Build the project using Maven:
        ```bash
        mvn clean install
        ```
        (This will also download all necessary dependencies).

3.  **Frontend Setup (React):**
    *   Navigate to the `frontend` directory:
        ```bash
        cd ../frontend
        # or from root: cd frontend
        ```
    *   Install dependencies:
        ```bash
        npm install
        # or yarn install / pnpm install
        ```

## Running the Application

1.  **Start the Backend (Spring Boot):**
    *   From the `backend` directory:
        ```bash
        mvn spring-boot:run
        ```
        Alternatively, you can run the main application class (e.g., `ResumeCheckerApplication.java`) from your IDE.
    *   The backend server will typically start on `http://localhost:8080`.

2.  **Start the Frontend (React with Vite):**
    *   From the `frontend` directory (in a new terminal window/tab):
        ```bash
        npm run dev
        ```
    *   The frontend development server will typically start on `http://localhost:5173` (or another port if 5173 is busy - check the terminal output).

3.  **Access the Application:**
    *   Open your web browser and navigate to `http://localhost:5173` (or the port shown by Vite).

## API Endpoints (Backend)

*   **`POST /api/v1/resumes/check`**:
    *   **Description:** Accepts a resume file (`MultipartFile`) for analysis.
    *   **Request Body:** `multipart/form-data` with a field named `resumeFile`.
    *   **Response:** JSON object containing the AI-generated analysis (e.g., `AnalysisResponseDto`).
        ```json
        {
          "message": "Markdown formatted analysis text...",
          "extractedTextPreview": "First few characters of the extracted resume text..."
        }
        ```

## Environment Variables (Backend - `.env` file)

*   `GEMINI_API_KEY`: **Required.** Your secret API key for accessing the Google Gemini API.
*   `GEMINI_API_URL`: (Optional) The endpoint URL for the Gemini API. Defaults to the `gemini-pro:generateContent` endpoint if not set.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## Future Enhancements / To-Do

*   [ ] User authentication and accounts to save analysis history.
*   [ ] More granular feedback categories (e.g., technical skills, soft skills, formatting).
*   [ ] Option to specify target job roles for more tailored analysis.
*   [ ] Database integration to store user data and analysis results.
*   [ ] Enhanced UI/UX for displaying results (e.g., progress bars, interactive elements).
*   [ ] Unit and integration tests for both frontend and backend.
*   [ ] Dockerization for easier deployment.
*   [ ] CI/CD pipeline setup.
*   [ ] More sophisticated error handling and user feedback.


## Acknowledgements

*   Google Gemini API
*   React & Spring Boot communities
*   Tailwind CSS

---
