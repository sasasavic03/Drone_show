import { useMemo } from 'react'

export default function DroneField({ count = 120 }) {
  const dots = useMemo(() => {
    const out = []
    for (let i = 0; i < count; i++) {
      const t = i / count
      const arc = i % 3
      let x, y, r
      if (arc === 0) {
        x = 60 + t * 680
        y = 190 - Math.sin(t * Math.PI) * 90
        r = 2.2
      } else if (arc === 1) {
        x = 120 + t * 560
        y = 235 - Math.sin(t * Math.PI) * 45
        r = 1.8
      } else {
        x = 40 + ((i * 97) % 720)
        y = 40 + ((i * 53) % 150)
        r = 1.2
      }
      out.push({
        x,
        y,
        r,
        delay: ((i * 137) % 400) / 100,
        beam: arc !== 2,
      })
    }
    return out
  }, [count])

  return (
    <svg
      viewBox="0 0 800 300"
      className="h-full w-full"
      aria-hidden="true"
      preserveAspectRatio="xMidYMid slice"
    >
      {dots.map((d, i) => (
        <circle
          key={i}
          className="drone-dot"
          cx={d.x}
          cy={d.y}
          r={d.r}
          fill={d.beam ? 'var(--color-beam)' : 'var(--color-pulse)'}
          style={{ animationDelay: `${d.delay}s` }}
        />
      ))}
    </svg>
  )
}
