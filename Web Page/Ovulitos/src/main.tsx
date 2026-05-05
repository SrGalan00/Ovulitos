import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import { AuthProvider } from './context/authContext/index.jsx'

import './index.css'
import dayjs from 'dayjs'
import 'dayjs/locale/es'

dayjs.locale('es')

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </React.StrictMode>,
)

