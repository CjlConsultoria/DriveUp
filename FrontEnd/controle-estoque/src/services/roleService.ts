// services/roleService.ts
import api from './api' // seu axios configurado

export async function listarRoles() {
  const response = await api.get('/roles')
  return response.data
}
