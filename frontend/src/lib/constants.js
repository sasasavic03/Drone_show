export const ROLES = {
  USER: 'USER',
  ADMIN: 'ADMIN',
}

/* Status flow sa backenda:
   PENDING -> CONFIRMED -> COMPLETED, uz CANCELLED iz bilo kog stanja.
   Nema posebnog REJECTED — odbijanje admina je CANCELLED + adminNote. */
export const BOOKING_STATUS = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
}

export const STATUS_LABEL = {
  PENDING: 'Ceka odgovor',
  CONFIRMED: 'Prihvaceno',
  COMPLETED: 'Odrzano',
  CANCELLED: 'Otkazano',
}

export const EVENT_TYPES = [
  { value: 'WEDDING', label: 'Vencanje' },
  { value: 'CORPORATE', label: 'Korporativni dogadjaj' },
  { value: 'BIRTHDAY', label: 'Rodjendan' },
  { value: 'CITY_EVENT', label: 'Gradska manifestacija' },
  { value: 'OTHER', label: 'Drugo' },
]

export const MAX_SHOWS_PER_DAY = 2

// Koristi se samo dok GET /packages ne vrati listu.
export const FALLBACK_PACKAGES = [
  {
    id: 'silver',
    name: 'Silver 80 Drona',
    droneCount: 80,
    durationMinutes: 15,
    basePrice: 1500,
    description: 'Kratka koreografija za privatne proslave i otvaranja.',
  },
  {
    id: 'gold',
    name: 'Gold 150 Drona',
    droneCount: 150,
    durationMinutes: 20,
    basePrice: 2800,
    description: 'Najcesci izbor za vencanja i gradske manifestacije.',
  },
  {
    id: 'platinum',
    name: 'Platinum 300 Drona',
    droneCount: 300,
    durationMinutes: 25,
    basePrice: 4900,
    description: 'Kompletna produkcija sa scenarijem i probom pred dogadjaj.',
  },
]

export function formatDate(value) {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return String(value)
  return d.toLocaleDateString('sr-RS', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

export function formatPrice(value) {
  if (value == null) return '—'
  return `${Number(value).toLocaleString('sr-RS')} €`
}

// YYYY-MM-DD bez pomeranja zbog vremenske zone (toISOString bi znao da odseva dan)
export function toIsoDate(date) {
  const p = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${p(date.getMonth() + 1)}-${p(date.getDate())}`
}
