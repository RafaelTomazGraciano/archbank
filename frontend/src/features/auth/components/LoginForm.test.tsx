import { describe, it, expect, vi, beforeEach } from "vitest"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { MemoryRouter } from "react-router-dom"
import { LoginForm } from "./LoginForm"
import { login } from "../api/auth-api"
import type { TokenResponse } from "../api/auth-api"

const { mockNavigate } = vi.hoisted(() => ({ mockNavigate: vi.fn() }))

vi.mock("react-router-dom", async (importOriginal) => {
  const actual = await importOriginal<typeof import("react-router-dom")>()
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  }
})

vi.mock("../api/auth-api", () => ({
  login: vi.fn(),
}))

async function fillForm(email: string, password: string) {
  await userEvent.type(screen.getByLabelText(/email/i), email)
  await userEvent.type(screen.getByLabelText(/^password$/i), password)
}

async function submit() {
  await userEvent.click(screen.getByRole("button", { name: /login/i }))
}

describe("LoginForm", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
    render(<LoginForm />, { wrapper: MemoryRouter })
  })

  it("blocks submission and does not call the API when the email is invalid", async () => {
    await fillForm("not-an-email", "Password@123")
    await submit()

    expect(await screen.findByText(/invalid email format/i)).toBeInTheDocument()
    expect(login).not.toHaveBeenCalled()
  })

  it("blocks submission and does not call the API when the password fails the complexity rule", async () => {
    await fillForm("user@email.com", "weakpass")
    await submit()

    expect(
      await screen.findByText(/must contain at least one uppercase letter/i)
    ).toBeInTheDocument()
    expect(login).not.toHaveBeenCalled()
  })

  it("stores the token and navigates to the dashboard on successful login", async () => {
    vi.mocked(login).mockResolvedValue({
      token: "fake-token",
      name: "Test",
      email: "user@email.com",
    })

    await fillForm("user@email.com", "Password@123")
    await submit()

    expect(login).toHaveBeenCalledWith({
      email: "user@email.com",
      password: "Password@123",
    })
    expect(localStorage.getItem("token")).toBe("fake-token")
    expect(mockNavigate).toHaveBeenCalledWith("/dashboard")
  })

  it("shows the backend message when the response body is a plain string", async () => {
    vi.mocked(login).mockRejectedValue({
      response: { data: "Invalid email or password" },
    })

    await fillForm("user@email.com", "Password@123")
    await submit()

    expect(await screen.findByText(/invalid email or password/i)).toBeInTheDocument()
  })

  it("shows the backend message when the response body has a message field", async () => {
    vi.mocked(login).mockRejectedValue({
      response: { data: { message: "Account locked" } },
    })

    await fillForm("user@email.com", "Password@123")
    await submit()

    expect(await screen.findByText(/account locked/i)).toBeInTheDocument()
  })

  it("shows every field message when the response body has an errors map", async () => {
    vi.mocked(login).mockRejectedValue({
      response: {
        data: {
          message: "Validation failed",
          errors: {
            email: "Invalid email format",
            password: "Password too weak",
          },
        },
      },
    })

    await fillForm("user@email.com", "Password@123")
    await submit()

    expect(await screen.findByText("Invalid email format")).toBeInTheDocument()
    expect(await screen.findByText("Password too weak")).toBeInTheDocument()
  })

  it("shows a server-unreachable message and does not navigate when there is no response at all", async () => {
    vi.mocked(login).mockRejectedValue({ request: {} })

    await fillForm("user@email.com", "Password@123")
    await submit()

    expect(await screen.findByText(/server is unreachable/i)).toBeInTheDocument()
    expect(mockNavigate).not.toHaveBeenCalled()
  })

  it("toggles the password field between hidden and visible text", async () => {
    const passwordInput = screen.getByLabelText(/^password$/i) as HTMLInputElement
    expect(passwordInput).toHaveAttribute("type", "password")

    await userEvent.click(screen.getByRole("button", { name: /toggle password visibility/i }))

    expect(passwordInput).toHaveAttribute("type", "text")
  })

  it("disables the submit button while the request is in flight", async () => {
    let resolveLogin: (value: TokenResponse) => void = () => {}
    vi.mocked(login).mockImplementation(
      () => new Promise((resolve) => { resolveLogin = resolve })
    )

    await fillForm("user@email.com", "Password@123")
    await submit()

    expect(screen.getByRole("button", { name: /logging in/i })).toBeDisabled()

    resolveLogin({ token: "t", name: "Test", email: "user@email.com" })
  })

})