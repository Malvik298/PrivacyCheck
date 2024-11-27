# Privacy Check - INSE 6120

### Technologies used
- LLM: Gemini 1.5 Flash
- Mobile Platform: Android 15
- Backend: N8N no-code platform

### LLM: Gemini 1.5 Flash
- Due to large context windows, Gemini was selected for privacy analysis.
- It is available to free to use.
- Other LLM models were short on context window and were inefficient for analysis due to restriction.
- Self Hosted solutions such as Ollama with Mistral was used but due to limited hardware capabilities, we were able to use just 11000 capabilities over GPU (RTX 4060 8GB) before switching to CPU based processing. The results were inconsistent and were limited. 

### Android Application 
- The android application targets android 15 SDK.
- Kotlin language was selected for coding.
- Application uses a service based worker checking the app installation status every 5 seconds.
- Sends a notification to user on new app installation and send a query to server to analyse the privacy policy of the application from the playstore.
- Provides a summary of the privacy Policy.

### N8N 
N8N is a no-code simple workflow automation to speed up the server logic creation process. This tool was selected due to simplicty and ease of server setup and code logic flow. More details about N8N can be on [found here](https://n8n.io).

The Workflow automation can be found in the file [Privacy_Analysis_n8n.json](./Policy_Analysis_n8n.json). Below is the workflow that was created.

![Screenshot 2024-11-26 024506](https://github.com/user-attachments/assets/aa40adf0-cf18-43dd-b93a-5b0314f6548e)

Find out the video demo in Demo folder

