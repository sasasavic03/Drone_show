/* Tailwind */

export function Button({ as: Tag = 'button', variant = 'primary', className = '', ...props }) {
  const base =
    'inline-flex items-center justify-center gap-2 rounded-full px-6 py-3 text-sm font-bold transition disabled:cursor-not-allowed disabled:opacity-50'
  const variants = {
    primary: 'bg-beam text-night hover:bg-beam/85',
    ghost: 'border border-haze text-paper hover:border-pulse hover:text-pulse',
    danger: 'border border-red-400/50 text-red-300 hover:bg-red-400/10',
    quiet: 'text-mute hover:text-paper',
  }
  return <Tag className={`${base} ${variants[variant]} ${className}`} {...props} />
}

export function Field({ label, hint, error, children }) {
  return (
    <label className="block">
      <span className="eyebrow mb-2 block">{label}</span>
      {children}
      {error ? (
        <span className="mt-1.5 block text-xs text-red-300">{error}</span>
      ) : hint ? (
        <span className="mt-1.5 block text-xs text-mute">{hint}</span>
      ) : null}
    </label>
  )
}

export function Input({ className = '', ...props }) {
  return (
    <input
      className={`w-full rounded-xl border border-haze bg-dusk/60 px-4 py-3 text-paper placeholder:text-mute/60 transition focus:border-pulse focus:outline-none ${className}`}
      {...props}
    />
  )
}

export function Card({ className = '', ...props }) {
  return (
    <div
      className={`rounded-2xl border border-haze/70 bg-dusk/40 p-6 ${className}`}
      {...props}
    />
  )
}

export function Alert({ children, tone = 'error' }) {
  if (!children) return null
  const tones = {
    error: 'border-red-400/40 bg-red-400/10 text-red-200',
    success: 'border-beam/40 bg-beam/10 text-beam',
    info: 'border-haze bg-dusk/60 text-mute',
  }
  return (
    <div className={`rounded-xl border px-4 py-3 text-sm ${tones[tone]}`} role="alert">
      {children}
    </div>
  )
}

export function PageHeader({ eyebrow, title, children }) {
  return (
    <header className="mb-10">
      {eyebrow && <p className="eyebrow mb-3">{eyebrow}</p>}
      <h1 className="font-display text-3xl font-extrabold sm:text-4xl">{title}</h1>
      {children && <p className="mt-3 max-w-2xl text-mute">{children}</p>}
    </header>
  )
}

export function EmptyState({ title, children }) {
  return (
    <div className="rounded-2xl border border-dashed border-haze px-6 py-14 text-center">
      <p className="font-display text-lg font-semibold">{title}</p>
      {children && <div className="mt-3 text-sm text-mute">{children}</div>}
    </div>
  )
}
