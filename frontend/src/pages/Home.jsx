import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import DroneField from '../components/DroneField.jsx'
import { Button, Card } from '../components/ui.jsx'
import { getPackages } from '../api/bookings.js'
import { getMedia } from '../api/media.js'
import MediaGrid from '../components/MediaGrid.jsx'
import Lightbox from '../components/Lightbox.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { formatPrice } from '../lib/constants.js'

const STEPS = [
  { n: '01', title: 'Izaberi termin', text: 'Kalendar prikazuje samo datume koji su jos slobodni.' },
  { n: '02', title: 'Izaberi paket', text: 'Broj dronova i trajanje odredjuju kako show izgleda sa zemlje.' },
  { n: '03', title: 'Sacekaj potvrdu', text: 'Proveravamo lokaciju i vremensku prognozu, pa javljamo odgovor.' },
]

export default function Home() {
  const { user } = useAuth()
  const [packages, setPackages] = useState([])
  const [media, setMedia] = useState([])
  const [openIndex, setOpenIndex] = useState(null)

  useEffect(() => {
    getPackages().then(setPackages)
    // Kratak izbor za pocetnu; ceo materijal je na /galerija.
    getMedia()
      .then((items) => setMedia(items.slice(0, 8)))
      .catch(() => setMedia([]))
  }, [])

  return (
    <>
      {/* Hero */}
      <section className="relative overflow-hidden">
        <div className="pointer-events-none absolute inset-0 opacity-70">
          <DroneField />
        </div>
        <div className="absolute inset-0 bg-gradient-to-b from-night/40 via-night/70 to-night" />

        <div className="relative mx-auto max-w-6xl px-5 pb-24 pt-28 sm:pt-36">
          <p className="eyebrow mb-5">300 dronova · 15 minuta · jedno nebo</p>
          <h1 className="max-w-3xl font-display text-4xl font-extrabold leading-[1.05] sm:text-6xl">
            Vasa poruka, ispisana{' '}
            <span className="text-beam">iznad grada</span>.
          </h1>
          <p className="mt-6 max-w-xl text-lg text-mute">
            Koreografisani letovi dronova za vencanja, otvaranja i gradske manifestacije.
            Izaberite datum, mi preuzimamo dozvole, opremu i sve sto leti.
          </p>
          <div className="mt-9 flex flex-wrap gap-3">
            <Button as={Link} to={user ? '/zakazivanje' : '/registracija'}>
              Zakazi termin
            </Button>
            <Button as="a" href="#paketi" variant="ghost">
              Pogledaj pakete
            </Button>
          </div>
        </div>
      </section>

      {/* Kako ide */}
      <section className="mx-auto max-w-6xl px-5 py-20">
        <p className="eyebrow mb-10">Od zahteva do poletanja</p>
        <ol className="grid gap-8 sm:grid-cols-3">
          {STEPS.map((s) => (
            <li key={s.n} className="border-t border-haze pt-5">
              <span className="font-mono text-sm text-beam">{s.n}</span>
              <h3 className="mt-3 font-display text-lg font-semibold">{s.title}</h3>
              <p className="mt-2 text-sm text-mute">{s.text}</p>
            </li>
          ))}
        </ol>
      </section>

      {/* Paketi */}
      <section id="paketi" className="mx-auto max-w-6xl scroll-mt-24 px-5 pb-24">
        <div className="mb-10 flex items-end justify-between gap-6">
          <div>
            <p className="eyebrow mb-3">Tri paketa</p>
            <h2 className="font-display text-3xl font-extrabold">Koliko dronova staje u vase vece</h2>
          </div>
        </div>

        <div className="grid gap-5 md:grid-cols-3">
          {packages.map((p, i) => (
            <Card
              key={p.id ?? i}
              className={`flex flex-col ${i === 1 ? 'border-beam/50 bg-dusk/70' : ''}`}
            >
              <div className="flex items-baseline justify-between gap-3">
                <h3 className="font-display text-xl font-bold">{p.name}</h3>
                {i === 1 && (
                  <span className="font-mono text-[10px] uppercase tracking-[0.14em] text-beam">
                    najcesce
                  </span>
                )}
              </div>

              <p className="mt-3 font-mono text-xs text-mute">
                {p.droneCount} dronova · {p.durationMinutes} min
              </p>
              <p className="mt-5 font-display text-2xl font-extrabold text-beam">
                {formatPrice(p.basePrice)}
              </p>

              {p.description && <p className="mt-4 text-sm text-mute">{p.description}</p>}

              {p.options?.length > 0 && (
                <ul className="mt-6 flex-1 space-y-2 text-sm">
                  <li className="eyebrow">Dodaci</li>
                  {p.options.map((o) => (
                    <li key={o.id} className="flex justify-between gap-3 text-mute">
                      <span>{o.name}</span>
                      <span className="font-mono text-xs">+{formatPrice(o.extraPrice)}</span>
                    </li>
                  ))}
                </ul>
              )}

              <Button
                as={Link}
                to={user ? '/zakazivanje' : '/prijava'}
                variant={i === 1 ? 'primary' : 'ghost'}
                className="mt-7 w-full"
              >
                Zakazi
              </Button>
            </Card>
          ))}
        </div>
      </section>

      {/* Galerija - kratak izbor */}
      {media.length > 0 && (
        <section className="mx-auto max-w-6xl px-5 pb-24">
          <div className="mb-8 flex flex-wrap items-end justify-between gap-4">
            <div>
              <p className="eyebrow mb-3">Sa odrzanih show-ova</p>
              <h2 className="font-display text-3xl font-extrabold">Kako to izgleda sa zemlje</h2>
            </div>
            <Link to="/galerija" className="text-sm text-beam hover:underline">
              Cela galerija →
            </Link>
          </div>

          <MediaGrid items={media} onOpen={setOpenIndex} />

          {openIndex !== null && (
            <Lightbox
              items={media}
              index={openIndex}
              onClose={() => setOpenIndex(null)}
              onNavigate={setOpenIndex}
            />
          )}
        </section>
      )}
    </>
  )
}
