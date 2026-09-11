# ArchBank - Frontend

ArchBank is a digital banking application. This repository contains the frontend client, built with React and TypeScript, which consumes the [ArchBank backend API](../backend/BACKEND_README.md) for authentication, accounts, and transactions.

## Technologies Used

- React
- TypeScript
- Vite
- Tailwind CSS
- Shadcn UI
- React Router
- Zod
- Axios
- Vitest + Testing Library

## Prerequisites

- Node.js (v24.18.0 or higher)
- npm (v11.16.0 or higher)
- The [ArchBank backend](../backend/BACKEND_README.md) running locally (see its README for setup)

## Environment Variables

This project uses a `.env` file to configure the backend API URL. Copy the example file and adjust it if needed:

```bash
cp .env.example .env
```

| Variable        | Description                          | Default                        |
| --------------- | ------------------------------------- | ------------------------------- |
| `VITE_API_URL`  | Base URL of the backend API           | `http://localhost:8080/api`    |


## How to Run the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/RafaelTomazGraciano/archbank.git
   ```

2. Navigate to the project directory:

   ```bash
   cd archbank/frontend
   ```

3. Install dependencies:

   ```bash
   npm install
   ```

4. Set up your environment variables (see [Environment Variables](#environment-variables) above).

5. Make sure the *backend* is running locally on `http://localhost:8080`.

6. Start the development server:

   ```bash
   npm run dev
   ```

7. Open your browser and go to `http://localhost:3000` to view the application.

## Running Tests

Tests are written with [Vitest](https://vitest.dev/) and [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/):

```bash
npm run test
```

## Other READMEs

This README covers only the **frontend**. Continue reading the project's documentation.

Read the frontend documentation: [BACKEND_README](./../backend/BACKEND_README.md)

Read the project documentation: [README](./../README.md)