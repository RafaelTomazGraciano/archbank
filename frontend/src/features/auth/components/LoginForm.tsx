import React, { useState } from "react"
import { useNavigate, Link } from "react-router-dom"
import { cn } from "@/shared/lib/utils"
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
import { loginSchema } from "../schemas/auth-schemas"
import { Input } from "@/shared/components/ui/input"
import { login } from "../api/auth-api"

export function LoginForm({
  className,
  ...props
}: React.ComponentProps<"div">) {

  const navigate = useNavigate();
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [errors, setErrors] = useState<string[]>([])
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.SubmitEvent){
    e.preventDefault()
    setErrors([])

    const zodResult = loginSchema.safeParse({ email, password})
    if(!zodResult.success) {
      setErrors(zodResult.error.issues.map((issue) => issue.message))
      return
    }

    setLoading(true)
    try{
      const loginResponse = await login(zodResult.data)
      localStorage.setItem("token", loginResponse.token)
      navigate("/dashboard")
    }catch(err: any){
      let messages: string[] = ["Invalid email or password"]

      if(!err.response){
        messages = ["Server is unreachable. Please try again later"]
      }
      else{
        const data = err.response.data
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
    <div className={cn("flex flex-col gap-6", className)} {...props}>
      <Card>
        <CardHeader>
          <CardTitle>Login to your account</CardTitle>
          <CardDescription>
            Enter your email below to login to your account
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit}>
            <FieldGroup>
              <Field>
                <FieldLabel htmlFor="email">Email</FieldLabel>
                <Input
                  id="email"
                  type="email"
                  placeholder="email@example.com"
                  value={email}
                  onChange={(e => setEmail(e.target.value))}
                  required
                />
              </Field>
              <Field>
                <div className="flex items-center">
                  <FieldLabel htmlFor="password">Password</FieldLabel>
                  <a
                    href="#"
                    className="ml-auto inline-block text-sm underline-offset-4 hover:underline"
                  >
                    Forgot your password?
                  </a>
                </div>
                <div className="relative">
                  <Input 
                    id="password" 
                    type={showPassword ? "text" : "password"}
                    value={password}
                    onChange={(e => setPassword(e.target.value))}
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
              </Field>
              {errors.length > 0 && (
                <ul className="text-sm text-destructive list-disc pl-5 space-y-1">
                  {errors.map((msg, i) => (
                    <li key={i}>{msg}</li>
                  ))}
                </ul>
              )}
              <Field>
                <Button type="submit" disabled={loading}>
                  {loading ? "Logging in..." : "Login"}
                </Button>
                <FieldDescription className="text-center">
                  Don&apos;t have an account?{" "}
                  <Link to={"/signup"}>Sign Up</Link>
                </FieldDescription>
              </Field>
            </FieldGroup>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
