/*auth 8081  users 8082  packages 8083  bookings 8084  media 8085*/

const BASE_URL = import.meta.env.VITE_API_URL ?? ''

const ACCESS_KEY = 'ds_access_token'
const REFRESH_KEY = 'ds_refresh_token'

export const ENDPOINTS = {
  // AUTH SERVICE (8081)
  login: '/auth/login',
  register: '/auth/register',
  refresh: '/auth/refresh',
  logout: '/auth/logout',
  me: '/auth/me',

  // USER SERVICE (8082)
  userMe: '/users/me',
  users: '/users',

  // PACKAGE SERVICE (8083)
  packages: '/packages',
  packageById: (id) => `/packages/${id}`,
  calculatePrice: '/packages/calculate-price',

  // BOOKING SERVICE (8084)
  bookings: '/bookings',
  bookingById: (id) => `/bookings/${id}`,
  bookingStatus: (id) => `/bookings/${id}/status`,
  availability: '/bookings/availability', // ?date=YYYY-MM-DD

  // MEDIA SERVICE (8085)
  media: '/media',
}

export const tokenStore = {
  access: () => localStorage.getItem(ACCESS_KEY),
  refresh: () => localStorage.getItem(REFRESH_KEY),
  set({ accessToken, refreshToken }) {
    if (accessToken) localStorage.setItem(ACCESS_KEY, accessToken)
    if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken)
  },
  clear() {
    localStorage.removeItem(ACCESS_KEY)
    localStorage.removeItem(REFRESH_KEY)
  },
}

export class ApiError extends Error {
  constructor(message, status, data) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.data = data
  }
}

/* access token traje 15 minuta, pa je obnova obavezna —
   bez ovoga bi korisnika izbacivalo usred zakazivanja */
let refreshing = null

async function renewSession() {
  const refreshToken = tokenStore.refresh()
  if (!refreshToken) return false

  // ako vise zahteva istovremeno dobije 401, obnavljamo samo jednom.
  refreshing ??= (async () => {
    try {
      const data = await rawRequest(ENDPOINTS.refresh, {
        method: 'POST',
        body: { refreshToken },
        auth: false,
      })
      tokenStore.set(data ?? {})
      return Boolean(data?.accessToken)
    } catch {
      tokenStore.clear()
      return false
    } finally {
      refreshing = null
    }
  })()

  return refreshing
}

async function rawRequest(path, { method = 'GET', body, auth = true, signal } = {}) {
  const headers = { Accept: 'application/json' }
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  const token = tokenStore.access()
  if (auth && token) headers.Authorization = `Bearer ${token}`

  let res
  try {
    res = await fetch(`${BASE_URL}${path}`, {
      method,
      headers,
      signal,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiError('Server nije dostupan. Proveri da li servisi rade (docker-compose up).', 0)
  }

  const text = await res.text()
  let data = null
  if (text) {
    try {
      data = JSON.parse(text)
    } catch {
      data = text
    }
  }

  if (!res.ok) {
    const message =
      (data && (data.message || data.error)) ||
      (res.status === 401
        ? 'Pogresni podaci za prijavu.'
        : res.status === 403
          ? 'Nemate pravo pristupa.'
          : res.status === 404
            ? 'Endpoint ne postoji na backendu.'
            : 'Zahtev nije uspeo.')
    throw new ApiError(message, res.status, data)
  }

  return data
}

export async function request(path, options = {}) {
  try {
    return await rawRequest(path, options)
  } catch (err) {
    const canRetry = err.status === 401 && options.auth !== false && !options._retried
    if (!canRetry) throw err

    const renewed = await renewSession()
    if (!renewed) throw err
    return rawRequest(path, { ...options, _retried: true })
  }
}
