import axios from 'axios'
import router from '@/router'
import { useToast } from 'vue-toastification'

const toast = useToast()

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Interceptor para adicionar token atualizado em cada requisição
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  } else {
    delete config.headers.Authorization
  }
  return config
})

// Interceptor para tratar erros de resposta
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // 401 = redirecionar para login
    if (error.response && error.response.status === 401) {
      localStorage.clear()
      sessionStorage.clear()
      delete api.defaults.headers['Authorization']
      router.push({ name: 'Login' })
      return Promise.reject(error)
    }

    // Pega mensagem da API se existir
    if (error.response && error.response.data && error.response.data.message) {
      toast.error(error.response.data.message)
    } else if (error.message) {
      // Mensagem genérica do Axios
      toast.error(error.message)
    } else {
      toast.error('Erro desconhecido')
    }

    return Promise.reject(error)
  }
)

export default api
