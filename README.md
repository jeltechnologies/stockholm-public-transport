# Ask ChatGTP "What is the next bus?"
![slbus](https://github.com/user-attachments/assets/31bac689-3e99-4fc9-bab9-74e4d3bab13e)

<sup>*Image by by [AleWi - Own work, CC BY-SA 4.0](https://commons.wikimedia.org/w/index.php?curid=131932539)*</sup>

## Answers by the AI bot 
![chatting](https://github.com/user-attachments/assets/0bc9a190-0b1a-4e2e-adba-00db1e2ff092)
<sup>*AI chatbot in [Open Web UI](https://openwebui.com/)*</sup> 

# About this tool
The tool provides AI agents access to real-time Stockholms public transport data. 

The tool gets its data from [SL](https://sl.se/), the Stockholm public transport company. 

- Translation of SL's data model, to a format that AI chatbots understand, basically by simplifying data to essential information.

- Supporting this as a Model Context Protocol (MCP) server.
- Deployment with Docker Compose

# Why this tool? 
The Model Context Protocol (MCP) is a crucial enabler for agentic AI, often referred to as the "USB-C port" for AI applications. With MCP, AI applications like Claude or ChatGPT can access key information and perform tasks.  I created this tool to understand how MCP works, its possibilities, and limitations.

## My learnings 
Getting started with the Model Context Protocol in modern frameworks like Spring is incredibly easy. It's a "plug-and-play" solution that works out of the box. 

Tailoring the server for AI bots takes most times, to manage the non-deterministic behavior of chatbots.

### The need for bot-usability
In traditional APIs, clients perform the same requests and use responses in the same way, simular to dogs. AI chatbots act more like cats - unpredictable and prone to random behavior. You need to persuade chatbots to use the tool with clear descriptions, but even then, there's no guarantee how they'll process information. 

While working with the AI bots, I've noticed similarities to user interface development in industrial software engineering. Developing human interfaces without usability has lead to high quality of horrible user interfaces. These are high quality, because thet pass all automatic tests, but with a horrible user experience, making them useless. This happens because engineers see the system from the inside out, they know how it works. Without guidance, engineers tend to expose all available options in their user interfaces, to to maintain maximum flexibility. This approach leads to complex configuration user interface with many confuzing options parameters. We must not fall in the same trap when developing interfaces for AI bots. 

Users and AI bots just want to perform tasks efficiently. A plethora of options can overwhelm them, making it difficult for them to correlate without halicunations. 

The solution lies in traditional usability design principles:
1.	Understand the tasks that bot need to perform.
2.	Collect only the data needed for these tasks. Ignore the system-level configuration in this phase. 
3.	Simplify the responses to make them easy understand and correlate, use natural language instead where possible.
4.	Strip out al data the bots do not need for their task.

The key is to build the Model Context Protocol server for the chatbot, as if it were human. 

Tweaking this will take most time, like usability takes most time at successful user interface development

# Enough ranting already, let's get started!
To get this project running you will need a Linux box with Docker, for example in the cloud or on a homeserver. 

## Installation instructions
- Get a Linux box with Docker installed.
- Clone this repository
- Edit the file `config.json` and update it with your server details:
```
{
  "mcpServers": {
    "stockholm-public-transport": {
      "type": "sse",
      "url": "http://<your-ip-here>:18107/api/v1/sse"
    }
  }
}
```


- Keep the docker running in the background to observe the logs.
- Optionally, you can also start the container with
  - `docker compose up -d` and then
  - `docker compose logs -f`

### Configuring the chatbot
MCP is an open standard, the tool thefore works with Open WebUI, and may also work with other AI tools like Claude or ChatGTP. 

#### Configuration of OpenWebUI 
- Open OpenWebUI as administrator, click on the account, choose Admin Panel, Settings, External Tools
- Click "Add Connection" 
- Use URL `http://<your-ip-here>:18108/stockholm-public-transport` and change the IP to the IP of the Linux box.
- Choose a name and description. 
![editconnectionopenwebui](https://github.com/user-attachments/assets/a85cb55c-c5d8-4052-bb20-a68123f6ee42)

- Add the tool to models, to make it available in all chats.
![addtomodel](https://github.com/user-attachments/assets/ed1699f3-2e7f-4c1e-b5dd-0ee5c1fb1c8e)












