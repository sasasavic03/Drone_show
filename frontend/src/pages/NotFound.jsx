import { Link } from 'react-router-dom'
import { Button } from '../components/ui.jsx'

export default function NotFound() {
  return (
    <div className="mx-auto max-w-xl px-5 py-32 text-center">
      <p className="eyebrow mb-4">404</p>
      <h1 className="font-display text-3xl font-extrabold">Ova strana nije poletela</h1>
      <p className="mt-3 text-mute">Adresa ne postoji ili je promenjena.</p>
      <Button as={Link} to="/" className="mt-8">
        Nazad na pocetnu
      </Button>
    </div>
  )
}
