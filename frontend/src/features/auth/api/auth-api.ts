import { api } from "@/shared/lib/api";

export interface LoginRequest {
    email: string;
    password: string;
}

export interface SignUpRequest {
    name: string;
    cpf: string;
    email: string;
    phone: string | null;
    password: string;
    transactionPin: string;
}

export interface TokenResponse {
    token: string;
    name: string;
    email: string;
}

export async function login(data: LoginRequest): Promise<TokenResponse> {
    const response = await api.post<TokenResponse>("auth/login", data);
    console.log(response.data)
    return response.data;
}

export async function signUp(data: SignUpRequest): Promise<TokenResponse> {
    const response = await api.post<TokenResponse>("auth/signup", data);
    return response.data;
}