import { useEffect, useState } from 'react'
import { getMedia } from '../api/media.js'
import MediaGrid from '../components/MediaGrid.jsx'
import Lightbox from '../components/Lightbox.jsx'
import { Alert, EmptyState, PageHeader } from '../components/ui.jsx'
import { EVENT_TYPES } from '../lib/constants.js'

const FILTERS = [{ value: '', label: 'Sve' }, ...EVENT_TYPES]

export default function Gallery() {
  const [items, setItems] = useState([])
  const [eventType, setEventType] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [openIndex, setOpenIndex] = useState(null)

  useEffect(() => {
    let alive = true
    setLoading(true)
    setError('')

    getMedia({ eventType: eventType || undefined })
      .then((data) => alive && setItems(data))
      .catch((err) => alive && setError(err.message))
      .finally(() => alive && setLoading(false))

    return () => {
      alive = false
    }
  }, [eventType])

  return (
    <div className="mx-auto max-w-6xl px-5 py-16">
      <PageHeader eyebrow="Galerija" title="Kako to izgleda sa zemlje">
        Snimci i fotografije sa odrzanih show-ova. Filtrirajte po tipu dogadjaja
        da vidite sta radimo za priliku slicnu vasoj.
      </PageHeader>

      <div className="mb-8 flex flex-wrap gap-2">
        {FILTERS.map((f) => (
          <button
            key={f.value}
            onClick={() => setEventType(f.value)}
            className={`rounded-full border px-4 py-2 font-mono text-[11px] uppercase tracking-[0.14em] transition ${
              eventType === f.value
                ? 'border-beam bg-beam/10 text-beam'
                : 'border-haze text-mute hover:text-paper'
            }`}
          >
            {f.label}
          </button>
        ))}
      </div>

      {error && <Alert>{error}</Alert>}

      {loading ? (
        <p className="eyebrow">Ucitavanje…</p>
      ) : items.length === 0 && !error ? (
        <EmptyState title="Ovde jos nema snimaka">
          {eventType
            ? 'Za ovaj tip dogadjaja jos nismo objavili materijal. Pogledajte ostale kategorije.'
            : 'Materijal sa show-ova se pojavljuje ovde cim ga objavimo.'}
        </EmptyState>
      ) : (
        <MediaGrid items={items} onOpen={setOpenIndex} />
      )}

      {openIndex !== null && (
        <Lightbox
          items={items}
          index={openIndex}
          onClose={() => setOpenIndex(null)}
          onNavigate={setOpenIndex}
        />
      )}
    </div>
  )
}
