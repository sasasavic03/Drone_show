import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { PageHeader, Card, Alert, Button, Field, Input } from '../components/ui.jsx'
import { createBooking, getMonthAvailability, getPackages } from '../api/bookings.js'
import { EVENT_TYPES, formatPrice, toIsoDate } from '../lib/constants.js'

const WEEKDAYS = ['Pon', 'Uto', 'Sre', 'Cet', 'Pet', 'Sub', 'Ned']
const MONTHS = [
  'Januar', 'Februar', 'Mart', 'April', 'Maj', 'Jun',
  'Jul', 'Avgust', 'Septembar', 'Oktobar', 'Novembar', 'Decembar',
]

function startOfMonth(date) {
  return new Date(date.getFullYear(), date.getMonth(), 1)
}

function buildCalendarCells(viewDate) {
  const first = startOfMonth(viewDate)
  const daysInMonth = new Date(viewDate.getFullYear(), viewDate.getMonth() + 1, 0).getDate()
  const leadingBlanks = (first.getDay() + 6) % 7 // Ponedeljak = 0

  const cells = Array.from({ length: leadingBlanks }, () => null)
  for (let d = 1; d <= daysInMonth; d++) {
    cells.push(new Date(viewDate.getFullYear(), viewDate.getMonth(), d))
  }
  return cells
}

export default function Booking() {
  const navigate = useNavigate()

  // 01 · Datum i vreme
  const [viewDate, setViewDate] = useState(startOfMonth(new Date()))
  const [availability, setAvailability] = useState({})
  const [calLoading, setCalLoading] = useState(true)
  const [selectedDate, setSelectedDate] = useState(null)
  const [eventTime, setEventTime] = useState('')

  // 02 · Paket
  const [packages, setPackages] = useState([])
  const [packagesLoading, setPackagesLoading] = useState(true)
  const [packageId, setPackageId] = useState(null)

  // 03 · Detalji i slanje
  const [location, setLocation] = useState('')
  const [city, setCity] = useState('')
  const [guestCount, setGuestCount] = useState('')
  const [eventType, setEventType] = useState(EVENT_TYPES[0].value)
  const [submitting, setSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState('')
  const [submitted, setSubmitted] = useState(false)

  const today = useMemo(() => {
    const d = new Date()
    d.setHours(0, 0, 0, 0)
    return d
  }, [])

  const isCurrentViewMonth =
    viewDate.getFullYear() === today.getFullYear() && viewDate.getMonth() === today.getMonth()

  useEffect(() => {
    let alive = true
    setCalLoading(true)
    getMonthAvailability(viewDate.getFullYear(), viewDate.getMonth())
      .then((map) => alive && setAvailability(map))
      .finally(() => alive && setCalLoading(false))
    return () => {
      alive = false
    }
  }, [viewDate])

  useEffect(() => {
    let alive = true
    getPackages()
      .then((list) => alive && setPackages(list))
      .finally(() => alive && setPackagesLoading(false))
    return () => {
      alive = false
    }
  }, [])

  const selectedPackage = packages.find((p) => String(p.id) === String(packageId)) ?? null

  const canSubmit =
    selectedDate && eventTime && packageId && location.trim() && city.trim() && Number(guestCount) > 0

  function changeMonth(delta) {
    setViewDate((v) => new Date(v.getFullYear(), v.getMonth() + delta, 1))
  }

  function pickDate(day) {
    if (!day) return
    if (day < today) return
    const iso = toIsoDate(day)
    if (availability[iso] === false) return
    setSelectedDate(day)
  }

  async function submit(e) {
    e.preventDefault()
    if (!canSubmit) return
    setSubmitError('')
    setSubmitting(true)
    try {
      await createBooking({
        packageId,
        eventDate: toIsoDate(selectedDate),
        eventTime,
        location: location.trim(),
        city: city.trim(),
        guestCount: Number(guestCount),
        eventType,
      })
      setSubmitted(true)
    } catch (err) {
      setSubmitError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (submitted) {
    return (
      <div className="mx-auto max-w-3xl px-5 py-16">
        <PageHeader eyebrow="Zakazivanje" title="Zahtev je poslat" />
        <Alert tone="success">
          Zahtev je uspesno poslat. Admin proverava lokaciju i uslove, a status mozete pratiti
          na stranici "Moja zakazivanja".
        </Alert>
        <div className="mt-6 flex gap-3">
          <Button onClick={() => navigate('/moja-zakazivanja')}>Moja zakazivanja</Button>
          <Button
            variant="ghost"
            onClick={() => {
              setSubmitted(false)
              setSelectedDate(null)
              setEventTime('')
              setPackageId(null)
              setLocation('')
              setCity('')
              setGuestCount('')
              setEventType(EVENT_TYPES[0].value)
            }}
          >
            Novi zahtev
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-5xl px-5 py-16">
      <PageHeader eyebrow="Zakazivanje" title="Izaberite termin i paket">
        Slobodni datumi se povlace sa servera. Nakon slanja zahteva admin proverava
        lokaciju i uslove, pa dobijate odgovor.
      </PageHeader>

      <form onSubmit={submit}>
        <div className="grid gap-5 lg:grid-cols-[1.4fr_1fr]">
          <Card>
            <p className="eyebrow mb-4">01 · Datum i vreme</p>

            <div className="mb-4 flex items-center justify-between">
              <button
                type="button"
                onClick={() => changeMonth(-1)}
                disabled={isCurrentViewMonth}
                className="rounded-full border border-haze px-3 py-1 text-sm text-mute transition hover:text-paper disabled:cursor-not-allowed disabled:opacity-30"
              >
                ←
              </button>
              <p className="font-display font-semibold">
                {MONTHS[viewDate.getMonth()]} {viewDate.getFullYear()}
              </p>
              <button
                type="button"
                onClick={() => changeMonth(1)}
                className="rounded-full border border-haze px-3 py-1 text-sm text-mute transition hover:text-paper"
              >
                →
              </button>
            </div>

            <div className="grid grid-cols-7 gap-1 text-center text-xs text-mute">
              {WEEKDAYS.map((w) => (
                <span key={w} className="py-1">
                  {w}
                </span>
              ))}
            </div>

            <div className="grid grid-cols-7 gap-1">
              {buildCalendarCells(viewDate).map((day, i) => {
                if (!day) return <span key={`b-${i}`} />
                const iso = toIsoDate(day)
                const isPast = day < today
                const isUnavailable = availability[iso] === false
                const isSelected = selectedDate && toIsoDate(selectedDate) === iso
                const disabled = isPast || isUnavailable

                return (
                  <button
                    key={iso}
                    type="button"
                    onClick={() => pickDate(day)}
                    disabled={disabled}
                    title={isUnavailable ? 'Popunjeno' : undefined}
                    className={`aspect-square rounded-lg text-sm transition ${
                      isSelected
                        ? 'bg-beam font-bold text-night'
                        : disabled
                          ? 'cursor-not-allowed text-mute/30 line-through'
                          : 'text-paper hover:bg-dusk/80'
                    }`}
                  >
                    {day.getDate()}
                  </button>
                )
              })}
            </div>

            {calLoading && <p className="mt-3 text-xs text-mute">Proveravam dostupnost…</p>}

            <div className="mt-5">
              <Field label="Vreme dogadjaja">
                <Input
                  type="time"
                  value={eventTime}
                  onChange={(e) => setEventTime(e.target.value)}
                  disabled={!selectedDate}
                  required
                />
              </Field>
            </div>
          </Card>

          <Card>
            <p className="eyebrow mb-4">02 · Paket</p>

            {packagesLoading ? (
              <p className="text-sm text-mute">Ucitavanje paketa…</p>
            ) : (
              <div className="space-y-3">
                {packages.map((pkg) => {
                  const selected = String(pkg.id) === String(packageId)
                  return (
                    <button
                      key={pkg.id}
                      type="button"
                      onClick={() => setPackageId(pkg.id)}
                      className={`w-full rounded-xl border p-4 text-left transition ${
                        selected ? 'border-beam bg-beam/10' : 'border-haze/70 hover:border-pulse'
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <p className="font-display font-semibold">{pkg.name}</p>
                        <span className="font-mono text-sm text-beam">{formatPrice(pkg.basePrice)}</span>
                      </div>
                      <p className="mt-1 text-xs text-mute">
                        {pkg.droneCount} dronova · {pkg.durationMinutes} min
                      </p>
                      {pkg.description && (
                        <p className="mt-2 text-sm text-mute">{pkg.description}</p>
                      )}
                    </button>
                  )
                })}
              </div>
            )}
          </Card>
        </div>

        <Card className="mt-5">
          <p className="eyebrow mb-4">03 · Detalji i slanje</p>

          <div className="grid gap-5 sm:grid-cols-2">
            <Field label="Lokacija">
              <Input
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                placeholder="Naziv mesta / adresa"
                required
              />
            </Field>
            <Field label="Grad">
              <Input value={city} onChange={(e) => setCity(e.target.value)} required />
            </Field>
            <Field label="Broj gostiju">
              <Input
                type="number"
                min="1"
                value={guestCount}
                onChange={(e) => setGuestCount(e.target.value)}
                required
              />
            </Field>
            <Field label="Vrsta dogadjaja">
              <select
                value={eventType}
                onChange={(e) => setEventType(e.target.value)}
                className="w-full rounded-xl border border-haze bg-dusk/60 px-4 py-3 text-paper transition focus:border-pulse focus:outline-none"
              >
                {EVENT_TYPES.map((t) => (
                  <option key={t.value} value={t.value}>
                    {t.label}
                  </option>
                ))}
              </select>
            </Field>
          </div>

          {selectedDate && selectedPackage && (
            <p className="mt-5 text-sm text-mute">
              Rezime: <span className="text-paper">{selectedPackage.name}</span> ·{' '}
              {toIsoDate(selectedDate)} {eventTime && `u ${eventTime}`} ·{' '}
              <span className="text-beam">{formatPrice(selectedPackage.basePrice)}</span>
            </p>
          )}

          {submitError && (
            <div className="mt-4">
              <Alert>{submitError}</Alert>
            </div>
          )}

          <Button type="submit" disabled={!canSubmit || submitting} className="mt-5">
            {submitting ? 'Slanje…' : 'Posalji zahtev'}
          </Button>
        </Card>
      </form>
    </div>
  )
}
