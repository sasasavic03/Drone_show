import { BOOKING_STATUS, STATUS_LABEL } from '../lib/constants.js'

const styles = {
  [BOOKING_STATUS.PENDING]: 'border-pulse/50 text-pulse',
  [BOOKING_STATUS.CONFIRMED]: 'border-beam/60 text-beam',
  [BOOKING_STATUS.COMPLETED]: 'border-haze text-mute',
  [BOOKING_STATUS.CANCELLED]: 'border-red-400/50 text-red-300',
}

export default function StatusBadge({ status }) {
  const key = String(status || '').toUpperCase()
  return (
    <span
      className={`inline-flex items-center gap-2 rounded-full border px-3 py-1 font-mono text-[11px] uppercase tracking-[0.14em] ${
        styles[key] ?? 'border-haze text-mute'
      }`}
    >
      <span className="h-1.5 w-1.5 rounded-full bg-current" />
      {STATUS_LABEL[key] ?? status}
    </span>
  )
}
