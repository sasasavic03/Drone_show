import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { Alert, Button, Card, Field, Input } from '../components/ui.jsx'
import { ROLES } from '../lib/constants.js'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  function change(e) {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }))
  }

  async function submit(e) {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      const user = await login(form)
      // Admin ide pravo na zahteve, korisnik tamo odakle je krenuo.
      const fallback = user?.role === ROLES.ADMIN ? '/admin/zahtevi' : '/zakazivanje'
      navigate(location.state?.from ?? fallback, { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="mx-auto max-w-md px-5 py-20">
      <p className="eyebrow mb-3">Prijava</p>
      <h1 className="font-display text-3xl font-extrabold">Dobrodosli nazad</h1>

      <Card className="mt-8">
        <form onSubmit={submit} className="space-y-5">
          <Field label="E-adresa">
            <Input name="email" type="email" value={form.email} onChange={change} required autoComplete="email" />
          </Field>
          <Field label="Lozinka">
            <Input
              name="password"
              type="password"
              value={form.password}
              onChange={change}
              required
              autoComplete="current-password"
            />
          </Field>
          <Alert>{error}</Alert>
          <Button type="submit" disabled={busy} className="w-full">
            {busy ? 'Prijavljivanje…' : 'Prijavi se'}
          </Button>
        </form>
      </Card>

      <p className="mt-6 text-center text-sm text-mute">
        Nemate nalog?{' '}
        <Link to="/registracija" className="text-beam hover:underline">
          Otvorite ga ovde
        </Link>
      </p>
    </div>
  )
}
