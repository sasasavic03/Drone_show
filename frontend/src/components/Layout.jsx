import { Outlet } from 'react-router-dom'
import Navbar from './Navbar.jsx'

export default function Layout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1">
        <Outlet />
      </main>
      <footer className="border-t border-haze/40 px-5 py-8">
        <div className="mx-auto flex max-w-6xl flex-col gap-2 text-xs text-mute sm:flex-row sm:items-center sm:justify-between">
          <span className="font-mono uppercase tracking-[0.14em]">
            Letimo uz dozvolu CGV-a · Srbija
          </span>
          <span>© {new Date().getFullYear()} Nocno nebo</span>
        </div>
      </footer>
    </div>
  )
}
