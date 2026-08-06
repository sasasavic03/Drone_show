import { request, ENDPOINTS, tokenStore } from './client.js'

/* Backend vraca accessToken, refreshToken, user */
function store(data) {
  tokenStore.set(data ?? {})
  return data?.user ?? null
}

export async function login({ email, password }) {
  const data = await request(ENDPOINTS.login, {
    method: 'POST',
    body: { email, password },
    auth: false,
  })
  return store(data)
}

export async function register({ firstName, lastName, email, password }) {
  const data = await request(ENDPOINTS.register, {
    method: 'POST',
    body: { firstName, lastName, email, password },
    auth: false,
  })
  return store(data)
}

export function me() {
  return request(ENDPOINTS.me)
}

export async function logout() {
  try {
    await request(ENDPOINTS.logout, { method: 'POST', body: {} })
  } catch {
    // Odjava lokalno vazi i ako server ne odgovori
  } finally {
    tokenStore.clear()
  }
}
