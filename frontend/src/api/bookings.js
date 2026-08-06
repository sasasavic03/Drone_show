import { request, ENDPOINTS } from './client.js'
import { FALLBACK_PACKAGES, toIsoDate } from '../lib/constants.js'

/* paketi */

export async function getPackages() {
  try {
    const data = await request(ENDPOINTS.packages, { auth: false })
    const list = Array.isArray(data) ? data : (data?.content ?? [])
    return list.length ? list : FALLBACK_PACKAGES
  } catch {
    return FALLBACK_PACKAGES
  }
}

export function getPackage(id) {
  return request(ENDPOINTS.packageById(id), { auth: false })
}

export function calculatePrice({ packageId, optionIds = [] }) {
  return request(ENDPOINTS.calculatePrice, {
    method: 'POST',
    body: { packageId, optionIds },
    auth: false,
  })
}

/*  dostupnost
   backend proverava jedan datum: GET /bookings/availability?date=YYYY-MM-DD
   kalendaru treba ceo mesec pa dok ne dobijemo opseg saljemo dan po dan */

export function getAvailability(date) {
  const iso = typeof date === 'string' ? date : toIsoDate(date)
  return request(`${ENDPOINTS.availability}?date=${iso}`, { auth: false })
}

export async function getMonthAvailability(year, month) {
  const days = new Date(year, month + 1, 0).getDate()
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  const dates = []
  for (let d = 1; d <= days; d++) {
    const date = new Date(year, month, d)
    if (date >= today) dates.push(toIsoDate(date))
  }

  const results = await Promise.all(
    dates.map((iso) =>
      getAvailability(iso)
        .then((r) => [iso, r?.isAvailable !== false])
        .catch(() => [iso, false]),
    ),
  )
  return Object.fromEntries(results)
}

/* rezervacije*/

export function createBooking({
  packageId,
  eventDate,
  eventTime,
  location,
  city,
  guestCount,
  eventType,
  optionIds = [],
}) {
  return request(ENDPOINTS.bookings, {
    method: 'POST',
    body: { packageId, eventDate, eventTime, location, city, guestCount, eventType, optionIds },
  })
}

export function getBooking(id) {
  return request(ENDPOINTS.bookingById(id))
}

export function cancelBooking(id) {
  return request(ENDPOINTS.bookingById(id), { method: 'DELETE' })
}

/* ova dva endpointa nisu u backend dokumentaciji.
   frontend bez njih nemoze da prikaze listu:
   GET /bookings/me  (korisnikove rezervacije)
   GET /bookings     (sve, samo ADMIN)
   Ako dobiju druga imena, menja se samo ovde. */

function asList(data) {
  return Array.isArray(data) ? data : (data?.content ?? [])
}

export async function getMyBookings() {
  const data = await request(`${ENDPOINTS.bookings}/me`)
  return asList(data)
}

export async function getAllBookings() {
  const data = await request(ENDPOINTS.bookings)
  return asList(data)
}

export function updateBookingStatus(id, status, adminNote) {
  return request(ENDPOINTS.bookingStatus(id), {
    method: 'PATCH',
    body: { status, adminNote },
  })
}
