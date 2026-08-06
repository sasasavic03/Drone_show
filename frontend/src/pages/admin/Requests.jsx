import { useEffect, useState } from 'react'
import { getAllBookings, updateBookingStatus } from '../../api/bookings.js'
import StatusBadge from '../../components/StatusBadge.jsx'
import { Alert, Button, EmptyState, PageHeader } from '../../components/ui.jsx'
import { BOOKING_STATUS, formatDate, formatPrice } from '../../lib/constants.js'

export default function AdminRequests() {
  const [items, setItems] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [busyId, setBusyId] = useState(null)

  useEffect(() => {
    getAllBookings()
      .then(setItems)
      .catch((err) =>
        setError(
          err.status === 404
            ? 'Backend jos nema GET /bookings za listu svih rezervacija. Panel ce raditi cim se endpoint doda.'
            : err.message,
        ),
      )
      .finally(() => setLoading(false))
  }, [])

  async function decide(id, status, adminNote) {
    setBusyId(id)
    setError('')
    try {
      await updateBookingStatus(id, status, adminNote)
      setItems((prev) => prev.map((b) => (b.id === id ? { ...b, status, adminNote } : b)))
    } catch (err) {
      setError(err.message)
    } finally {
      setBusyId(null)
    }
  }

  const pending = items.filter((b) => b.status === BOOKING_STATUS.PENDING).length

  return (
    <div className="mx-auto max-w-6xl px-5 py-16">
      <PageHeader eyebrow={`Zahteva na cekanju: ${pending}`} title="Zahtevi za zakazivanje">
        Prihvatanje zauzima termin u kalendaru i salje obavestenje korisniku.
      </PageHeader>

      {error && <Alert>{error}</Alert>}

      {loading ? (
        <p className="eyebrow">Ucitavanje…</p>
      ) : items.length === 0 && !error ? (
        <EmptyState title="Nema zahteva">Kada neko zakaze show, pojavice se ovde.</EmptyState>
      ) : items.length > 0 ? (
        <div className="mt-6 overflow-x-auto rounded-2xl border border-haze/70">
          <table className="w-full min-w-[860px] text-left text-sm">
            <thead className="bg-dusk/60">
              <tr className="eyebrow">
                <th className="px-5 py-4 font-normal">Termin</th>
                <th className="px-5 py-4 font-normal">Korisnik</th>
                <th className="px-5 py-4 font-normal">Paket</th>
                <th className="px-5 py-4 font-normal">Lokacija</th>
                <th className="px-5 py-4 font-normal">Cena</th>
                <th className="px-5 py-4 font-normal">Status</th>
                <th className="px-5 py-4 font-normal">Odluka</th>
              </tr>
            </thead>
            <tbody>
              {items.map((b) => (
                <tr key={b.id} className="border-t border-haze/40">
                  <td className="px-5 py-4 font-mono">
                    {formatDate(b.eventDate)} <span className="text-mute">{b.eventTime}</span>
                  </td>
                  <td className="px-5 py-4">
                    <div>{b.userName ?? b.user?.firstName ?? `#${b.userId}`}</div>
                    <div className="font-mono text-xs text-mute">{b.userEmail ?? b.user?.email}</div>
                  </td>
                  <td className="px-5 py-4">{b.packageName ?? `#${b.packageId}`}</td>
                  <td className="px-5 py-4 text-mute">
                    {b.location ?? '—'}
                    {b.city ? `, ${b.city}` : ''}
                  </td>
                  <td className="px-5 py-4 font-mono">{formatPrice(b.totalPrice)}</td>
                  <td className="px-5 py-4">
                    <StatusBadge status={b.status} />
                  </td>
                  <td className="px-5 py-4">
                    {b.status === BOOKING_STATUS.PENDING ? (
                      <div className="flex gap-2">
                        <Button
                          onClick={() => decide(b.id, BOOKING_STATUS.CONFIRMED, 'Odobreno')}
                          disabled={busyId === b.id}
                          className="px-4 py-2 text-xs"
                        >
                          Prihvati
                        </Button>
                        <Button
                          variant="danger"
                          onClick={() => decide(b.id, BOOKING_STATUS.CANCELLED, 'Odbijeno')}
                          disabled={busyId === b.id}
                          className="px-4 py-2 text-xs"
                        >
                          Odbij
                        </Button>
                      </div>
                    ) : (
                      <span className="font-mono text-xs text-mute">obradjeno</span>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </div>
  )
}
