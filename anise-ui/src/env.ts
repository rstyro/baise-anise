declare const process: {
  env: {
    NODE_ENV?: string
  }
}

const env = process.env.NODE_ENV || 'development'

interface EnvConfig {
  baseUrl: string
  appBaseUrl: string
  timeout: number
  retryCount: number
  retryDelay: number
}

const config: Record<string, EnvConfig> = {
  development: {
    baseUrl: 'http://localhost:8800',
    appBaseUrl: 'http://localhost:8800/',
    timeout: 10000,
    retryCount: 2,
    retryDelay: 1000
  },
  test: {
    baseUrl: 'https://api-test.example.com',
    appBaseUrl: 'http://localhost:8800',
    timeout: 10000,
    retryCount: 2,
    retryDelay: 1000
  },
  production: {
    baseUrl: 'https://api.example.com',
    appBaseUrl: 'https://api.example.com',
    timeout: 15000,
    retryCount: 1,
    retryDelay: 500
  }
}

export const currentEnv = env
export const envConfig = config[env]
export const baseUrl = envConfig.baseUrl
export const appBaseUrl = envConfig.appBaseUrl