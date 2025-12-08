# 🔌 Embabel Framework: How to Build MCP Server

This repository demonstrates how to build a **Model Context Protocol (MCP) Server** using the Embabel Framework and Spring Boot. The application exposes local file system capabilities (CRUD operations) as "Tools" that external AI agents (like Claude Desktop) can connect to and utilize.

📖 **Complete Guide**: For detailed explanations and a full code walkthrough, read our comprehensive tutorial.<br>
👉 [**Embabel Framework: How to Build MCP Server**](https://bootcamptoprod.com/embabel-build-mcp-server/)

🎥 **Video Tutorial**: Prefer hands-on learning? Watch our step-by-step implementation guide.<br>
👉 YouTube Tutorial - [**Embabel Framework: How to Build MCP Server**](https://youtu.be/gtlGojiGw8Q)

<p align="center">
  <a href="https://youtu.be/gtlGojiGw8Q">
    <img src="https://img.youtube.com/vi/gtlGojiGw8Q/0.jpg" alt="Embabel Framework: How to Build MCP Server" />
  </a>
</p>

<p align="center">
  ▶️ <a href="https://youtu.be/gtlGojiGw8Q">Watch on YouTube</a>
</p>

---

## ✨ What This Project Demonstrates

This application showcases how to **give AI agents access to tools**:

- **Model Context Protocol (MCP)** implementation using Embabel and Spring Boot.
- **Tool Exposure** using Embabel's `@Export` annotation to turn Java methods into AI tools.
- **File System Operations** allowing an AI to Create, Read, Edit, and Delete files.
- **SSE (Server-Sent Events)** transport layer for real-time agent communication.

---

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 21** or higher
- **OpenRouter API Key** (free tier available at [OpenRouter.ai](https://openrouter.ai/))
- **Node.js** (Optional, required if testing with MCP Inspector)
- **Claude Desktop App** (Optional, for real-world agent testing)

---

## 🚀 Quick Start

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/BootcampToProd/embabel-mcp-file-server.git
cd embabel-mcp-file-server
```

### 2️⃣ Configure API Key
Provide your OpenRouter API key as an environment variable.
```bash
OPENAI_API_KEY={YOUR_OPENROUTER_API_KEY}
```

### 3️⃣ Build the Project
```bash
mvn clean install
```

### 4️⃣ Run the Application
```bash
mvn spring-boot:run
```
The server will start on `http://localhost:8080`. The MCP endpoint is exposed at `/sse`.

---

## 💡 How to Test

You can test the server using **Claude Desktop** or the **MCP Inspector**.

### 🤖 Option 1: Claude Desktop (Recommended)

1. Open your Claude Desktop configuration file:
    - **Mac:** `~/Library/Application Support/Claude/claude_desktop_config.json`
    - **Windows:** `%APPDATA%\Claude\claude_desktop_config.json`

2. Add the following configuration:
```json
{
  "mcpServers": {
    "embabel-file-manager": {
      "command": "npx",
      "args": [
        "-y",
        "mcp-remote",
        "http://localhost:8080/sse"
      ]
    }
  }
}
```

3. Restart Claude Desktop. You should see a connection icon.

4. **Ask Claude:** "Create a file named `hello_world.txt` with a short poem about Java."

### 🔍 Option 2: MCP Inspector

If you want to debug the tools manually:

1. Ensure the Spring Boot app is running.
2. Run the inspector in your terminal:
   ```bash
   npx @modelcontextprotocol/inspector
   ```
3. In the browser window that opens:
    - Select **SSE**.
    - Enter URL: `http://localhost:8080/sse`.
    - Click **Connect** and test the tools via the UI.