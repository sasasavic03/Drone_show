import { PageHeader, Card, Alert } from '../components/ui.jsx'

/* Skeleton stranice. */
export default function Booking() {
  return (
    <div className="mx-auto max-w-5xl px-5 py-16">
      <PageHeader eyebrow="Zakazivanje" title="Izaberite termin i paket">
        Slobodni datumi se povlace sa servera. Nakon slanja zahteva admin proverava
        lokaciju i uslove, pa dobijate odgovor.
      </PageHeader>

      <div className="grid gap-5 lg:grid-cols-[1.4fr_1fr]">
        <Card>
          <p className="eyebrow mb-4">01 · Datum i vreme</p>
          <div className="flex h-64 items-center justify-center rounded-xl border border-dashed border-haze text-sm text-mute">
            Ovde ide kalendar
          </div>
        </Card>

        <Card>
          <p className="eyebrow mb-4">02 · Paket</p>
          <div className="flex h-64 items-center justify-center rounded-xl border border-dashed border-haze text-sm text-mute">
            Ovde ide izbor paketa
          </div>
        </Card>
      </div>

      <Card className="mt-5">
        <p className="eyebrow mb-4">03 · Detalji i slanje</p>
        <Alert tone="info">
          Sledeci korak: kalendar sa slobodnim terminima, izbor paketa i slanje zahteva
          na POST /bookings.
        </Alert>
      </Card>
    </div>
  )
}
