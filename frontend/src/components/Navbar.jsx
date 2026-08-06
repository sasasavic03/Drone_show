import { useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { ROLES } from '../lib/constants.js'
import { Button } from './ui.jsx'

function links(user) {
  if (!user) {
    return [
      { to: '/', label: 'Pocetna' },
      { to: '/galerija', label: 'Galerija' },
      { to: '/#paketi', label: 'Paketi' },
    ]
  }
  if (user.role === ROLES.ADMIN) {
    return [
      { to: '/', label: 'Pocetna' },
      { to: '/galerija', label: 'Galerija' },
      { to: '/admin/zahtevi', label: 'Zahtevi' },
    ]
  }
  return [
    { to: '/', label: 'Pocetna' },
    { to: '/galerija', label: 'Galerija' },
    { to: '/zakazivanje', label: 'Zakazi show' },
    { to: '/moja-zakazivanja', label: 'Moja zakazivanja' },
  ]
}

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [open, setOpen] = useState(false)

  async function handleLogout() {
    await logout()
    navigate('/')
  }

  const nav = links(user)

  return (
    <header className="sticky top-0 z-50 border-b border-haze/40 bg-night/80 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-5 py-4">
        <Link to="/" className="font-display text-lg font-extrabold tracking-tight">
          NOCNO<span className="text-beam">·</span>NEBO
        </Link>

        <nav className="hidden items-center gap-7 md:flex">
          {nav.map((l) => (
            <NavLink
              key={l.to}
              to={l.to}
              className={({ isActive }) =>
                `text-sm transition ${isActive ? 'text-paper' : 'text-mute hover:text-paper'}`
              }
            >
              {l.label}
            </NavLink>
          ))}
        </nav>

        <div className="hidden items-center gap-3 md:flex">
          {user ? (
            <>
              <span className="font-mono text-xs text-mute">
                {user.firstName ?? user.email}
                {user.role === ROLES.ADMIN && <span className="text-beam"> · admin</span>}
              </span>
              <Button variant="quiet" onClick={handleLogout} className="px-3 py-2">
                Odjava
              </Button>
            </>
          ) : (
            <>
              <Button as={Link} to="/prijava" variant="quiet" className="px-3 py-2">
                Prijava
              </Button>
              <Button as={Link} to="/registracija" className="px-5 py-2.5">
                Otvori nalog
              </Button>
            </>
          )}
        </div>

        <button
          className="rounded-lg border border-haze px-3 py-2 font-mono text-xs md:hidden"
          onClick={() => setOpen((v) => !v)}
          aria-expanded={open}
          aria-label="Meni"
        >
          {open ? 'ZATVORI' : 'MENI'}
        </button>
      </div>

      {open && (
        <div className="border-t border-haze/40 px-5 py-4 md:hidden">
          <nav className="flex flex-col gap-4">
            {nav.map((l) => (
              <Link key={l.to} to={l.to} onClick={() => setOpen(false)} className="text-sm text-mute">
                {l.label}
              </Link>
            ))}
            {user ? (
              <button onClick={handleLogout} className="text-left text-sm text-mute">
                Odjava
              </button>
            ) : (
              <>
                <Link to="/prijava" onClick={() => setOpen(false)} className="text-sm text-mute">
                  Prijava
                </Link>
                <Link to="/registracija" onClick={() => setOpen(false)} className="text-sm text-beam">
                  Otvori nalog
                </Link>
              </>
            )}
          </nav>
        </div>
      )}
    </header>
  )
}
