import { z } from "zod"

const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':",./<>?]).{8,20}$/

export const loginSchema = z.object({
    email: z
        .string()
        .min(1, "Email is required")
        .email("Invalid email format"),
    password: z
        .string()
        .min(1, "Password is required")
        .regex(
            passwordRegex,
            "The password must contain at least one uppercase letter, one lowercase letter, one number, one special character, and be at least 8 characters long"
        ),
})

export const signupSchema = z.object({
    name: z
        .string()
        .min(1, "Name is required"),
    cpf: z
        .string()
        .min(1, "CPF is required")
        .regex(/^\d{11}$/, "CPF must contain exactly 11 digits"),
    email: z
        .string()
        .min(1, "Email is required")
        .email("Invalid email format"),
    phone: z
        .string()
        .regex(/^\+?[0-9]{10,20}$/, "Invalid phone format")
        .or(z.literal(""))
        .optional(),
    password: z
        .string()
        .min(1, "Password is required")
        .regex(
            passwordRegex,
            "The password must contain at least one uppercase letter, one lowercase letter, one number, one special character, and be at least 8 characters long"
        ),
    confirmPassword: z
        .string()
        .min(1, "Please confirm your password"),
    transactionPin: z
        .string()
        .min(1, "Transaction PIN is required")
        .regex(/^\d{4,6}$/, "PIN must contain only digits, between 4 and 6 numbers"),
})
.refine((data) => data.password === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
})