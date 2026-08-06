export default function MediaGrid({ items, onOpen }) {
  return (
    <ul className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4">
      {items.map((item, i) => (
        <li key={item.id}>
          <button
            onClick={() => onOpen(i)}
            className="group relative block aspect-[4/3] w-full overflow-hidden rounded-xl border border-haze/70 bg-dusk/60"
          >
            {item.type === 'VIDEO' ? (
              <video
                src={item.url}
                muted
                playsInline
                preload="metadata"
                className="h-full w-full object-cover transition duration-500 group-hover:scale-105"
              />
            ) : (
              <img
                src={item.thumbnail}
                alt={item.title}
                loading="lazy"
                className="h-full w-full object-cover transition duration-500 group-hover:scale-105"
              />
            )}

            <span className="absolute inset-0 bg-gradient-to-t from-night/90 via-night/10 to-transparent opacity-80 transition group-hover:opacity-95" />

            {item.type === 'VIDEO' && (
              <span className="absolute left-3 top-3 rounded-full bg-night/80 px-2.5 py-1 font-mono text-[10px] uppercase tracking-[0.14em] text-beam">
                video
              </span>
            )}

            {item.title && (
              <span className="absolute inset-x-3 bottom-3 text-left text-sm font-medium">
                {item.title}
              </span>
            )}
          </button>
        </li>
      ))}
    </ul>
  )
}
