# 🤖 AI PR Reviewer for Spring AI

This repository contains a GitHub Action and a Spring Boot project that uses **Spring AI** to perform automated code reviews on pull requests.  
The bot analyzes the code diff using an AI model and posts review comments directly on the pull request.

By default, it works with **Azure OpenAI** models, but you can adapt it to other providers by modifying the `pom.xml` and the `application.yaml`.

## 🛠️ Usage

### ⚙️ GitHub Action Setup

Create a workflow file in your repository, for example: `.github/workflows/ai-pr-review.yaml`.

Example configuration:

```yaml
name: Review PR with AI

on:
  pull_request:

jobs:
  ai-pr-review:
    uses: PabloGarciaFernandez/spring-ai-pr-reviewer/.github/workflows/ai-pr-review.yaml@master
    with:
      project-name: ${{ github.repository }}
      pull-request-id: ${{ github.event.pull_request.number }}
    secrets:
      MODEL_API_KEY: ${{ secrets.MODEL_API_KEY }}
      MODEL_NAME: ${{ secrets.MODEL_NAME }}
      MODEL_ENDPOINT: ${{ secrets.MODEL_ENDPOINT }}
      DEPLOYMENT_NAME: ${{ secrets.DEPLOYMENT_NAME }}
      PERSONAL_ACCESS_TOKEN: ${{ secrets.JSON_WEB_TOKEN }}
      INSTALLATION: ${{ secrets.INSTALLATION }}
```

### 🔑 Required Secrets

| 🔐 Secret Name    | 📄 Description                                                    |
|-------------------|-------------------------------------------------------------------|
| `MODEL_API_KEY`   | API key for the LLM provider (Azure OpenAI by default).           |
| `MODEL_NAME`      | Name of the model (e.g., `o4-mini`).                              |
| `MODEL_ENDPOINT`  | Endpoint URL of the Azure OpenAI resource.                        |
| `DEPLOYMENT_NAME` | Name of the Azure OpenAI deployment.                              |
| `PEM`             | GitHub App private key (PEM format, PKCS#8) used to sign the JWT. |
| `CLIENT_ID`       | GitHub App ID (not a token).                                      |
| `INSTALLATION`    | GitHub App installation ID.                                       |

> **Note:**  
> The GitHub App private key (`.pem`) must be converted to a PKCS#8 DER format and then Base64 encoded to be properly loaded in Java.
>
> Example conversion commands:
>
> On **Linux/macOS**:
> ```bash
> openssl pkcs8 -topk8 -inform PEM -outform DER -in ~/github-api-app.private-key.pem -out ~/github-api-app.private-key.der -nocrypt
> base64 ~/github-api-app.private-key.der > private-key.base64
> ```
>
> On **Windows (PowerShell or Command Prompt)**:
> ```bash
> openssl pkcs8 -topk8 -inform PEM -outform DER -in github-api-app.private-key.pem -out github-api-app.private-key.der -nocrypt
> certutil -encode github-api-app.private-key.der private-key.base64
> ```
>
> The resulting `private-key.base64` file can then be copied into your environment variable (`PEM`), include the headers, it will be removed in the code.


## 🌍 Notes on Model Providers

This project is built on **Spring AI** and only supports Azure OpenAI.

If you want to use other providers (like OpenAI, Hugging Face, Anthropic, etc.), you’ll need to:
- Add the corresponding Spring AI starter in your `pom.xml`.
- Update the `application.yaml` with the new provider’s configuration.
- For further information, look into this [documentation](https://docs.spring.io/spring-ai/reference/api/chatmodel.html).

## 🎯 Goal of the Project

The objective of this project is to create an automated, AI-powered pull request reviewer using Spring AI.  
The reviews are customized based on the given prompt and posted as comments in the pull request.
