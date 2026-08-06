import { Routes, Route } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import RequireAuth from './components/RequireAuth.jsx'
import { ROLES } from './lib/constants.js'

import Home from './pages/Home.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import Gallery from './pages/Gallery.jsx'
import Booking from './pages/Booking.jsx'
import MyBookings from './pages/MyBookings.jsx'
import AdminRequests from './pages/admin/Requests.jsx'
import NotFound from './pages/NotFound.jsx'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Javno */}
        <Route path="/" element={<Home />} />
        <Route path="/prijava" element={<Login />} />
        <Route path="/registracija" element={<Register />} />
        <Route path="/galerija" element={<Gallery />} />

        {/* Samo ulogovan korisnik */}
        <Route element={<RequireAuth roles={[ROLES.USER]} />}>
          <Route path="/zakazivanje" element={<Booking />} />
          <Route path="/moja-zakazivanja" element={<MyBookings />} />
        </Route>

        {/* Samo admin */}
        <Route element={<RequireAuth roles={[ROLES.ADMIN]} />}>
          <Route path="/admin/zahtevi" element={<AdminRequests />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  )
}
