import React, { useState } from "react"
import { useNavigate, Link } from "react-router-dom"
import { Button } from "@/shared/components/ui/button"
import { Eye, EyeOff } from "lucide-react"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/components/ui/card"
import {
  Field,
  FieldDescription,
  FieldGroup,
  FieldLabel,
} from "@/shared/components/ui/field"
import { signupSchema } from "../schemas/auth-schemas"
import { Input } from "@/shared/components/ui/input"
import { signUp } from "../api/auth-api"

export function SignupForm({ ...props }: React.ComponentProps<typeof Card>) {

  const navigate = useNavigate()
  const [name, setName] = useState("")
  const [cpf, setCpf] = useState("")
  const [email, setEMail] = useState("")
  const [phone, setPhone] = useState("")
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [transactionPin, setTransactionPin] = useState("")
  const [showPin, setShowPin] = useState(false)
  const [errors, setErrors] = useState<string[]>([])

  async function handleSubmit(e: React.SubmitEvent){
    e.preventDefault()
    setErrors([])

    const zodResult = signupSchema.safeParse({name, cpf, email, phone, password, confirmPassword, transactionPin})
    if(!zodResult.success){
      setErrors(zodResult.error.issues.map((issue) => issue.message))
      return
    }

    setLoading(true)
    try{
      const normalizedPhone = zodResult.data.phone?.trim() === "" ? null : zodResult.data.phone!.replace(/[^\d+]/g, "")
      const signupResponse = await signUp({
        name: zodResult.data.name,
        cpf: zodResult.data.cpf,
        email: zodResult.data.email,
        phone: normalizedPhone,
        password: zodResult.data.password,
        transactionPin: zodResult.data.transactionPin,
      })
      localStorage.setItem("token", signupResponse.token)
      navigate("/dashboard")
    }catch(err: any){
      let messages: string[] = ["Could not create account. Check your data and try again"]
      
      if(!err.response){
        messages = ["Server is unreachable. Please try again later"]
      }
      else{
        const data = err.response?.data
        if (typeof data === "string") {
          messages = [data]
        } else if (data?.errors) {
          messages = Object.values(data.errors) as string[]
        } else if (data?.message) {
          messages = [data.message]
        }
      }

      setErrors(messages)
    }finally{
      setLoading(false)
    }
  }

  return (
    <Card {...props}>
      <CardHeader>
        <CardTitle>Create an account</CardTitle>
        <CardDescription>
          Enter your information below to create your account
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit}>
          <FieldGroup>
            <Field>
              <FieldLabel htmlFor="name">Full Name</FieldLabel>
              <Input 
                id="name" 
                type="text" 
                placeholder="John Doe" 
                value={name}
                onChange={(e) => setName(e.target.value)}
                required 
                />
            </Field>
            <Field>
              <FieldLabel htmlFor="cpf">CPF</FieldLabel>
              <Input 
                id="cpf" 
                type="text" 
                inputMode="numeric"
                placeholder="12345678910" 
                value={cpf}
                onChange={(e) => setCpf(e.target.value.replace(/\D/g, ""))}
                required 
                />
            </Field>
            <Field>
              <FieldLabel htmlFor="email">Email</FieldLabel>
              <Input
                id="email"
                type="email"
                placeholder="email@example.com"
                value={email}
                onChange={(e) => setEMail(e.target.value)}
                required
              />
            </Field>
            <Field>
              <FieldLabel htmlFor="phone">Phone (optional)</FieldLabel>
              <Input 
                id="phone" 
                type="text" 
                placeholder="+5511999999999" 
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                />
            </Field>
            <Field>
              <FieldLabel htmlFor="password">Password</FieldLabel>
              <div className="relative">
                <Input 
                  id="password" 
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)} 
                  className="pr-10"
                  required />
                <button
                  type="button"
                  onClick={() => setShowPassword((prev) => !prev)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  tabIndex={-1}
                >
                  {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
              <FieldDescription>
                Must be at least 8 characters, with uppercase, lowercase, number and special character
              </FieldDescription>
            </Field>
            <Field>
              <FieldLabel htmlFor="confirm-password">
                Confirm Password
              </FieldLabel>
              <div className="relative">
              <Input 
                  id="confirm-password" 
                  type={showConfirmPassword ? "text" : "password"}
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="pr-10"
                  required 
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword((prev) => !prev)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  tabIndex={-1}
                >
                  {showConfirmPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
              <FieldDescription>Please confirm your password</FieldDescription>
            </Field>
            <Field>
              <FieldLabel htmlFor="pin">Transaction PIN</FieldLabel>
              <div className="relative">
              <Input 
                  id="pin" 
                  type={showPin ? "text" : "password"}
                  inputMode="numeric"
                  placeholder="1234" 
                  value={transactionPin}
                  onChange={(e) => setTransactionPin(e.target.value.replace(/\D/g, ""))}
                  className="pr-10"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPin((prev) => !prev)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                  tabIndex={-1}
                >
                  {showPin ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
              <FieldDescription>
                4 to 6 digits, used to confirm transactions
              </FieldDescription>
            </Field>
            {errors.length > 0 && (
              <ul className="text-sm text-destructive list-disc pl-5 space-y-1">
                {errors.map((msg, i) => (
                  <li key={i}>{msg}</li>
                ))}
              </ul>
            )}
            <FieldGroup>
              <Field>
                <Button type="submit" disabled={loading}>
                  {loading ? "Creating account..." : "Create Account"}
                </Button>
                <FieldDescription className="px-6 text-center">
                  Already have an account? <Link to={"/login"}>Sign in</Link>
                </FieldDescription>
              </Field>
            </FieldGroup>
          </FieldGroup>
        </form>
      </CardContent>
    </Card>
  )
}
