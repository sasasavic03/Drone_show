import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { Alert, Button, Card, Field, Input } from '../components/ui.jsx'

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirm: '',
  })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  function change(e) {
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }))
  }

  async function submit(e) {
    e.preventDefault()
    setError('')

    if (form.password.length < 6) {
      setError('Lozinka mora imati najmanje 6 znakova.')
      return
    }
    if (form.password !== form.confirm) {
      setError('Lozinke se ne poklapaju.')
      return
    }

    setBusy(true)
    try {
      // Backend vraca token odmah po registraciji - korisnik ide pravo na zakazivanje.
      await register({
        firstName: form.firstName,
        lastName: form.lastName,
        email: form.email,
        password: form.password,
      })
      navigate('/zakazivanje', { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="mx-auto max-w-md px-5 py-20">
      <p className="eyebrow mb-3">Registracija</p>
      <h1 className="font-display text-3xl font-extrabold">Otvorite nalog</h1>
      <p className="mt-3 text-sm text-mute">
        Nalog vam treba da biste zakazali termin i pratili status zahteva.
      </p>

      <Card className="mt-8">
        <form onSubmit={submit} className="space-y-5">
          <div className="grid gap-5 sm:grid-cols-2">
            <Field label="Ime">
              <Input name="firstName" value={form.firstName} onChange={change} required autoComplete="given-name" />
            </Field>
            <Field label="Prezime">
              <Input name="lastName" value={form.lastName} onChange={change} required autoComplete="family-name" />
            </Field>
          </div>
          <Field label="E-adresa">
            <Input name="email" type="email" value={form.email} onChange={change} required autoComplete="email" />
          </Field>
          <Field label="Lozinka" hint="Najmanje 6 znakova.">
            <Input
              name="password"
              type="password"
              value={form.password}
              onChange={change}
              required
              autoComplete="new-password"
            />
          </Field>
          <Field label="Potvrda lozinke">
            <Input
              name="confirm"
              type="password"
              value={form.confirm}
              onChange={change}
              required
              autoComplete="new-password"
            />
          </Field>
          <Alert>{error}</Alert>
          <Button type="submit" disabled={busy} className="w-full">
            {busy ? 'Otvaranje naloga…' : 'Otvori nalog'}
          </Button>
        </form>
      </Card>

      <p className="mt-6 text-center text-sm text-mute">
        Vec imate nalog?{' '}
        <Link to="/prijava" className="text-beam hover:underline">
          Prijavite se
        </Link>
      </p>
    </div>
  )
}
