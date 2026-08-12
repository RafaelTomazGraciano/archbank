import { describe, it, expect } from "vitest"
import { loginSchema, signupSchema } from "./auth-schemas"

function getFieldError(result: any, field: string) {
  if (result.success) return undefined
  return result.error.issues.find((issue: any) => issue.path[0] === field)?.message
}

describe("loginSchema", () => {
    it("accepts valid email and password", () => {
        const result = loginSchema.safeParse({
            email: "user@email.com",
            password: "Password@123"
        })
        expect(result.success).toBe(true)
    })

    it("rejects invalid email", () => {
        const result = loginSchema.safeParse({
            email: "not-an-email",
            password: "Password@123"
        })
        expect(getFieldError(result, "email")).toContain("Invalid email format")
    })

    it("reject password without special character", () => {
        const result = loginSchema.safeParse({
            email: "user@email.com",
            password: "Password123"
        })
        expect(getFieldError(result, "password")).toContain("The password must contain at least one uppercase letter, one lowercase letter, one number, one special character, and be at least 8 characters long")
    })
})

describe("signupSchema", () => {
    it("accepts all valid fields", () => {
        const result = signupSchema.safeParse({
            name: "User",
            cpf: "12345678910",
            email: "user@email.com",
            phone: "+5511989999999",
            password: "Password@123",
            confirmPassword: "Password@123",
            transactionPin: "1234"
        })
        expect(result.success).toBe(true)
    })

    it("rejects when password and confirmation do not match", () => {
        const result = signupSchema.safeParse({
            name: "User",
            cpf: "12345678910",
            email: "user@email.com",
            phone: "",
            password: "Password@123",
            confirmPassword: "Different@123",
            transactionPin: "1234"
        })
        expect(getFieldError(result, "confirmPassword")).toBe("Passwords do not match")
    })

    it("rejects CPF with less than 11 digits", () => {
        const result = signupSchema.safeParse({
            name: "User",
            cpf: "123",
            email: "user@email.com",
            phone: "",
            password: "Password@123",
            confirmPassword: "Password@123",
            transactionPin: "1234"
        })
        expect(getFieldError(result, "cpf")).toBe("CPF must contain exactly 11 digits")
    })

    it("reject password without special character", () => {
        const result = signupSchema.safeParse({
            name: "User",
            cpf: "12345678910",
            email: "user@email.com",
            phone: "",
            password: "Password123",
            confirmPassword: "Password123",
            transactionPin: "1234"
        })
        expect(getFieldError(result, "password")).toContain("The password must contain at least one uppercase letter, one lowercase letter, one number, one special character, and be at least 8 characters long")
    })

    it("reject transaction PIN must be between 4 and 6 numbers", () => {
        const result = signupSchema.safeParse({
            name: "User",
            cpf: "12345678910",
            email: "user@email.com",
            phone: "",
            password: "Password123",
            confirmPassword: "Password@123",
            transactionPin: "123"
        })
        expect(getFieldError(result, "transactionPin")).toContain("PIN must contain only digits, between 4 and 6 numbers")
    })

    it("reject phone in invalid format", () => {
        const result = signupSchema.safeParse({
            name: "User",
            cpf: "12345678910",
            email: "user@email.com",
            phone: "+55119899",
            password: "Password123",
            confirmPassword: "Password@123",
            transactionPin: "123"
        })
        expect(getFieldError(result, "phone")).toContain("Invalid phone format")
    })
})