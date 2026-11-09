import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import Dasboard from './Dasboard.tsx'
import "./index.css"

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Dasboard />
  </StrictMode>,
)
