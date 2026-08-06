import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMyBookings } from '../api/bookings.js'
import StatusBadge from '../components/StatusBadge.jsx'
import { Alert, Button, EmptyState, PageHeader } from '../components/ui.jsx'
import { formatDate, formatPrice } from '../lib/constants.js'

export default function MyBookings() {
  const [items, setItems] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getMyBookings()
      .then(setItems)
      .catch((err) =>
        setError(
          err.status === 404
            ? 'Backend jos nema GET /bookings/me. Lista ce raditi cim se endpoint doda.'
            : err.message,
        ),
      )
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="mx-auto max-w-5xl px-5 py-16">
      <PageHeader eyebrow="Moja zakazivanja" title="Status vasih zahteva">
        Kada admin odgovori, status se ovde menja.
      </PageHeader>

      {error && <Alert>{error}</Alert>}

      {loading ? (
        <p className="eyebrow">Ucitavanje…</p>
      ) : items.length === 0 && !error ? (
        <EmptyState title="Jos nemate zakazan show">
          <Button as={Link} to="/zakazivanje" className="mt-5">
            Zakazi termin
          </Button>
        </EmptyState>
      ) : items.length > 0 ? (
        <div className="mt-6 space-y-3">
          {items.map((b) => (
            <div
              key={b.id}
              className="flex flex-col gap-4 rounded-2xl border border-haze/70 bg-dusk/40 p-5 sm:flex-row sm:items-center sm:justify-between"
            >
              <div>
                <p className="font-mono text-sm">
                  {formatDate(b.eventDate)} <span className="text-mute">· {b.eventTime ?? '—'}</span>
                </p>
                <p className="mt-1 font-display font-semibold">{b.packageName ?? `Paket #${b.packageId}`}</p>
                <p className="mt-1 text-sm text-mute">
                  {b.location ?? '—'}
                  {b.city ? `, ${b.city}` : ''}
                </p>
                {b.adminNote && <p className="mt-2 text-sm text-mute">Napomena: {b.adminNote}</p>}
              </div>
              <div className="flex items-center gap-5">
                <span className="font-mono text-sm">{formatPrice(b.totalPrice)}</span>
                <StatusBadge status={b.status} />
              </div>
            </div>
          ))}
        </div>
      ) : null}
    </div>
  )
}
