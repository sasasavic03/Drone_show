# Drone show - frontend

React (JavaScript) + vite + Tailwind CSS v4 + react router.
Povezan sa mikroservisima.

## Pokretanje

```bash
# 1. Backend
docker-compose up

# 2. Frontend
npm install
cp .env.example .env
npm run dev
```


| Prefiks     | Servis          | Port |
| `/auth`     | auth_service    | 3001 |
| `/users`    | user_service    | 3002 |
| `/packages` | package_service | 3003 |
| `/bookings` | booking_service | 3004 |
| `/media`    | media_service   | 3005 |

Kad gateway proradi: obrisi proxy unose, ostavi `'/api': 8080` i stavi
`VITE_API_URL=/api` u `.env`. Putanje u kodu se ne menjaju.

Sve putanje su u objektu `ENDPOINTS` u `src/api/client.js`.

## Tokeni

Backend vraca `accessToken` (15 min) i `refreshToken` (7 dana).
`client.js` na 401 automatski poziva `POST /auth/refresh`, obnovi token i
ponovi originalni zahtev — jednom po zahtevu, i samo jedna obnova ako vise
zahteva istovremeno padne. Ako obnova ne uspe, korisnik se odjavljuje.

## Statusi rezervacije

`PENDING → CONFIRMED → COMPLETED`, uz `CANCELLED` iz bilo kog stanja.
Backend nema poseban `REJECTED`, pa admin "Odbij" salje
`PATCH /bookings/{id}/status` sa `{ status: "CANCELLED", adminNote: "Odbijeno" }`.

## Sta jos fali na backendu

Frontend je napisan tako da radi cim se ovo doda — menjaju se samo
`src/api/bookings.js`, ne komponente.

1. **`GET /bookings/me`** — lista rezervacija prijavljenog korisnika.
   Bez ovoga stranica "Moja zakazivanja" nema sta da prikaze.
2. **`GET /bookings`** (ADMIN) — lista svih rezervacija.
   Bez ovoga admin panel ne moze da prikaze zahteve za odobravanje.
3. **`GET /bookings/availability?from=&to=`** — dostupnost za opseg datuma.
   Trenutno postoji samo `?date=`, pa kalendar salje ~30 zahteva po mesecu.
4. **`GET /packages`** — lista paketa (dokumentovan je samo `/packages/{id}`).
   Dok ne postoji, pocetna prikazuje fallback iz `lib/constants.js`.

## Struktura

```
src/
├─ api/
│  ├─ client.js     ENDPOINTS, tokeni, refresh, greske
│  ├─ auth.js       login / register / me / logout
│  └─ bookings.js   paketi, dostupnost, rezervacije, status
│  └─ media.js      galerija (MinIO URL se prepisuje na javnu adresu)
├─ context/AuthContext.jsx
├─ components/      Layout, Navbar, RequireAuth, StatusBadge, DroneField,
│                   MediaGrid, Lightbox, ui
├─ pages/           Home, Galerija, Login, Register, Booking, MyBookings,
│                   admin/Requests
└─ lib/constants.js role, statusi, tipovi dogadjaja, formatiranje
```

## Rute

| Ruta | Pristup |
| --- | --- |
| `/` | svi |
| `/galerija` | svi |
| `/prijava`, `/registracija` | svi |
| `/zakazivanje`, `/moja-zakazivanja` | USER |
| `/admin/zahtevi` | ADMIN |

## Galerija i MinIO

Media servis vraca `fileUrl` sa internom docker adresom
(`http://minio:9000/...`), koju pregledac ne moze da otvori.
`src/api/media.js` je prepisuje na `VITE_MEDIA_URL` (podrazumevano
`http://localhost:9000`).

Dve stvari da bi slike bile vidljive u razvoju:

1. U `.env` postavi `VITE_MEDIA_URL=http://localhost:9000`
2. U MinIO konzoli (`localhost:9001`) daj bucket-u `drone-show-media`
   anonimno `readonly` pravilo, inace MinIO vraca 403.

Za produkciju: backend treba da vraca presigned URL ili da servira fajlove
kroz gateway — tada se prepisivanje u `media.js` brise.
