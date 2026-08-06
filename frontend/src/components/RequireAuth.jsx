import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function RequireAuth({ roles }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <span className="eyebrow">Ucitavanje…</span>
      </div>
    )
  }

  // Nije prijavljen — pamtimo odakle je dosao da ga vratimo posle prijave
  if (!user) {
    return <Navigate to="/prijava" state={{ from: location.pathname }} replace />
  }

  // Prijavljen je, ali nema pravo na ovu stranicu
  if (roles && !roles.includes(user.role)) {
    return <Navigate to="/" replace />
  }

  return <Outlet />
}
