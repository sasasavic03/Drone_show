import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import * as authApi from '../api/auth.js'
import { tokenStore } from '../api/client.js'
import { ROLES } from '../lib/constants.js'

const USER_KEY = 'ds_user'
const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  })
  const [loading, setLoading] = useState(Boolean(tokenStore.access()))

  function persist(nextUser) {
    setUser(nextUser ?? null)
    if (nextUser) localStorage.setItem(USER_KEY, JSON.stringify(nextUser))
    else localStorage.removeItem(USER_KEY)
  }

  // Posle refresh-a stranice proveravamo sesiju. Access token traje 15 min,
  // pa client.js u pozadini pokusava obnovu preko refresh tokena.
  useEffect(() => {
    if (!tokenStore.access()) {
      setLoading(false)
      return
    }
    let alive = true
    authApi
      .me()
      .then((fresh) => {
        if (alive && fresh) persist(fresh)
      })
      .catch((err) => {
        if (err.status === 401 && alive) {
          tokenStore.clear()
          persist(null)
        }
      })
      .finally(() => alive && setLoading(false))
    return () => {
      alive = false
    }
  }, [])

  const value = useMemo(
    () => ({
      user,
      loading,
      isAdmin: user?.role === ROLES.ADMIN,
      async login(credentials) {
        const u = await authApi.login(credentials)
        persist(u)
        return u
      },
      async register(payload) {
        const u = await authApi.register(payload)
        persist(u)
        return u
      },
      async logout() {
        await authApi.logout()
        persist(null)
      },
    }),
    [user, loading],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth mora biti unutar <AuthProvider>')
  return ctx
}
