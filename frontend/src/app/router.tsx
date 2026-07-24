import { createBrowserRouter } from "react-router-dom";
import LoginPage from "@/features/auth/pages/LoginPage";
import RegisterPage from "@/features/auth/pages/RegisterPage";
import HomePage from "@/features/home/pages/HomePage";
import DashboardPage from "@/features/account/pages/DashboardPage";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <HomePage />
    },
    {
        path: "/login",
        element: <LoginPage />
    },
    {
        path: "/signup",
        element: <RegisterPage />
    },
    {
        path: "/dashboard",
        element: <DashboardPage />
    }
])