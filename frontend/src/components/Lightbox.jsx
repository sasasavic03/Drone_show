import { useEffect, useRef } from 'react'

export default function Lightbox({ items, index, onClose, onNavigate }) {
  const closeRef = useRef(null)
  const item = items[index]

  useEffect(() => {
    closeRef.current?.focus()
  }, [])

  useEffect(() => {
    function onKey(e) {
      if (e.key === 'Escape') onClose()
      if (e.key === 'ArrowRight') onNavigate((index + 1) % items.length)
      if (e.key === 'ArrowLeft') onNavigate((index - 1 + items.length) % items.length)
    }
    document.addEventListener('keydown', onKey)
    // Pozadina ne sme da skroluje dok je otvoren prikaz
    const prev = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    return () => {
      document.removeEventListener('keydown', onKey)
      document.body.style.overflow = prev
    }
  }, [index, items.length, onClose, onNavigate])

  if (!item) return null

  return (
    <div
      className="fixed inset-0 z-[100] flex items-center justify-center bg-night/95 p-4 backdrop-blur"
      role="dialog"
      aria-modal="true"
      aria-label={item.title || 'Prikaz snimka'}
      onClick={onClose}
    >
      <button
        ref={closeRef}
        onClick={onClose}
        className="absolute right-5 top-5 rounded-full border border-haze px-4 py-2 font-mono text-xs uppercase tracking-[0.14em] text-mute transition hover:text-paper"
      >
        Zatvori
      </button>

      {items.length > 1 && (
        <>
          <button
            onClick={(e) => {
              e.stopPropagation()
              onNavigate((index - 1 + items.length) % items.length)
            }}
            className="absolute left-3 rounded-full border border-haze px-4 py-3 text-lg text-mute transition hover:text-paper sm:left-8"
            aria-label="Prethodno"
          >
            ‹
          </button>
          <button
            onClick={(e) => {
              e.stopPropagation()
              onNavigate((index + 1) % items.length)
            }}
            className="absolute right-3 rounded-full border border-haze px-4 py-3 text-lg text-mute transition hover:text-paper sm:right-8"
            aria-label="Sledece"
          >
            ›
          </button>
        </>
      )}

      <figure className="max-h-full w-full max-w-5xl" onClick={(e) => e.stopPropagation()}>
        {item.type === 'VIDEO' ? (
          <video src={item.url} controls autoPlay className="max-h-[80vh] w-full rounded-2xl" />
        ) : (
          <img
            src={item.url}
            alt={item.title}
            className="max-h-[80vh] w-full rounded-2xl object-contain"
          />
        )}
        <figcaption className="mt-4 flex items-center justify-between gap-4 text-sm">
          <span>{item.title}</span>
          <span className="font-mono text-xs text-mute">
            {index + 1} / {items.length}
          </span>
        </figcaption>
      </figure>
    </div>
  )
}
