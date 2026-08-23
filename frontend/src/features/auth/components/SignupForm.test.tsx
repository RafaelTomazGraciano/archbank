import { describe, it, expect, vi, beforeEach } from "vitest"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { MemoryRouter } from "react-router-dom"
import { SignupForm } from "./SignupForm"
import { signUp } from "../api/auth-api"
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
  signUp: vi.fn(),
}))

const validFields = {
  name: "Test",
  cpf: "12345678910",
  email: "user@email.com",
  password: "Password@123",
  confirmPassword: "Password@123",
  pin: "1234",
}

async function fillValidForm(overrides: Partial<typeof validFields> = {}) {
  const fields = { ...validFields, ...overrides }
  await userEvent.type(screen.getByLabelText(/full name/i), fields.name)
  await userEvent.type(screen.getByLabelText(/^cpf$/i), fields.cpf)
  await userEvent.type(screen.getByLabelText(/^email$/i), fields.email)
  await userEvent.type(screen.getByLabelText(/^password$/i), fields.password)
  await userEvent.type(screen.getByLabelText(/^confirm password$/i), fields.confirmPassword)
  await userEvent.type(screen.getByLabelText(/transaction pin/i), fields.pin)
}

async function submit() {
  await userEvent.click(screen.getByRole("button", { name: /create account/i }))
}

describe("SignupForm", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
    render(<SignupForm />, { wrapper: MemoryRouter })
  })

  it("only accepts digits in the CPF field", async () => {
    const cpfInput = screen.getByLabelText(/^cpf$/i) as HTMLInputElement
    await userEvent.type(cpfInput, "12a34b5678910")

    expect(cpfInput.value).toBe("12345678910")
  })

  it("only accepts digits in the transaction PIN field", async () => {
    const pinInput = screen.getByLabelText(/transaction pin/i) as HTMLInputElement
    await userEvent.type(pinInput, "1a2b3c4")

    expect(pinInput.value).toBe("1234")
  })

  it("shows an error and does not call the API when passwords do not match", async () => {
    await fillValidForm({ confirmPassword: "Different@123" })
    await submit()

    expect(await screen.findByText(/passwords do not match/i)).toBeInTheDocument()
    expect(signUp).not.toHaveBeenCalled()
  })

  it("does not call the API when the CPF is invalid", async () => {
    await fillValidForm({ cpf: "123" })
    await submit()

    expect(await screen.findByText(/cpf must contain exactly 11 digits/i)).toBeInTheDocument()
    expect(signUp).not.toHaveBeenCalled()
  })

  it("does not call the API when the PIN length is out of range", async () => {
    await fillValidForm({ pin: "12" })
    await submit()

    expect(
      await screen.findByText(/pin must contain only digits, between 4 and 6 numbers/i)
    ).toBeInTheDocument()
    expect(signUp).not.toHaveBeenCalled()
  })

  it("strips formatting from the phone number before sending it to the API", async () => {
    vi.mocked(signUp).mockResolvedValue({
      token: "fake-token"
    })

    await fillValidForm()
    await userEvent.type(screen.getByLabelText(/phone/i), "+55 11 99999-9999")
    await submit()

    expect(signUp).toHaveBeenCalledWith(
      expect.objectContaining({ phone: "+5511999999999" })
    )
  })

  it("sends null as the phone when the field is left empty", async () => {
    vi.mocked(signUp).mockResolvedValue({
      token: "fake-token"
    })

    await fillValidForm()
    await submit()

    expect(signUp).toHaveBeenCalledWith(
      expect.objectContaining({ phone: null })
    )
  })

  it("stores the token and navigates to the dashboard on successful signup", async () => {
    vi.mocked(signUp).mockResolvedValue({
      token: "fake-token"
    })

    await fillValidForm()
    await submit()

    expect(localStorage.getItem("token")).toBe("fake-token")
    expect(mockNavigate).toHaveBeenCalledWith("/dashboard")
  })

  it("shows every field message returned by the backend validation", async () => {
    vi.mocked(signUp).mockRejectedValue({
      response: {
        data: {
          message: "Validation failed",
          errors: {
            phone: "Invalid phone format",
            transactionPin: "PIN must contain only digits, between 4 and 6 numbers",
          },
        },
      },
    })

    await fillValidForm()
    await submit()

    expect(await screen.findByText("Invalid phone format")).toBeInTheDocument()
    expect(
      await screen.findByText("PIN must contain only digits, between 4 and 6 numbers")
    ).toBeInTheDocument()
  })

  it("shows a server-unreachable message when there is no response at all", async () => {
    vi.mocked(signUp).mockRejectedValue({ request: {} })

    await fillValidForm()
    await submit()

    expect(await screen.findByText(/server is unreachable/i)).toBeInTheDocument()
  })

  it("toggles all three password-type fields between hidden and visible text", async () => {
    const passwordInput = screen.getByLabelText(/^password$/i) as HTMLInputElement
    const confirmInput = screen.getByLabelText(/^confirm password$/i) as HTMLInputElement
    const pinInput = screen.getByLabelText(/transaction pin/i) as HTMLInputElement

    expect(passwordInput).toHaveAttribute("type", "password")
    expect(confirmInput).toHaveAttribute("type", "password")
    expect(pinInput).toHaveAttribute("type", "password")

    await userEvent.click(screen.getByRole("button", { name: /toggle password visibility/i }))
    await userEvent.click(screen.getByRole("button", { name: /toggle confirm password visibility/i }))
    await userEvent.click(screen.getByRole("button", { name: /toggle pin visibility/i }))

    expect(passwordInput).toHaveAttribute("type", "text")
    expect(confirmInput).toHaveAttribute("type", "text")
    expect(pinInput).toHaveAttribute("type", "text")
  })

  it("disables the submit button while the request is in flight", async () => {
    let resolveSignUp: (value: TokenResponse) => void = () => {}
    vi.mocked(signUp).mockImplementation(
      () => new Promise((resolve) => { resolveSignUp = resolve })
    )

    await fillValidForm()
    await submit()

    expect(screen.getByRole("button", { name: /creating account/i })).toBeDisabled()

    resolveSignUp({ token: "t" })
  })
})