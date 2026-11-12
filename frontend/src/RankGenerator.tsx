// src/components/app/RankInsignia.tsx — l'insigne de grade (mêmes tracés que la version JavaFX)
import type { Rank } from './api/Request'

// Le fond : un carré aux coins arrondis
const PATCH = 'M0 3 Q0 0 3 0 H21 Q24 0 24 3 V21 Q24 24 21 24 H3 Q0 24 0 21 Z'

// Les galons
const SLANT_1 = 'M7.5 18 L10.5 18 L16.5 6 L13.5 6 Z'
const SLANT_2 = 'M5.25 18 L8.25 18 L14.25 6 L11.25 6 Z M9.75 18 L12.75 18 L18.75 6 L15.75 6 Z'
const SLANT_3 = 'M3 18 L6 18 L12 6 L9 6 Z M7.5 18 L10.5 18 L16.5 6 L13.5 6 Z M12 18 L15 18 L21 6 L18 6 Z'
const SLANT_3_GAPS = 'M6.35 18 L7.15 18 L13.15 6 L12.35 6 Z M10.85 18 L11.65 18 L17.65 6 L16.85 6 Z'
const SERGENT_EDGE = 'M7.5 18 L8.3 18 L14.3 6 L13.5 6 Z'
const SERGENT_STRIPE = 'M8.3 18 L10.5 18 L16.5 6 L14.3 6 Z'
const BAR = 'M4 10 H20 V14 H4 Z'
const BAR_LINE = 'M4 11.7 H20 V12.3 H4 Z'
const BARS_2 = 'M4 8 H20 V11 H4 Z M4 13 H20 V16 H4 Z'
const BARS_3 = 'M4 6.75 H20 V9.25 H4 Z M4 10.75 H20 V13.25 H4 Z M4 14.75 H20 V17.25 H4 Z'
const BARS_4 = 'M4 5.75 H20 V7.75 H4 Z M4 9.25 H20 V11.25 H4 Z M4 12.75 H20 V14.75 H4 Z M4 16.25 H20 V18.25 H4 Z'
const BARS_5_ODD = 'M4 5.1 H20 V6.9 H4 Z M4 11.1 H20 V12.9 H4 Z M4 17.1 H20 V18.9 H4 Z'
const BARS_5_EVEN = 'M4 8.1 H20 V9.9 H4 Z M4 14.1 H20 V15.9 H4 Z'
const STARS_2 =
  'M7.5 8.2 L8.35 10.83 L11.11 10.83 L8.88 12.45 L9.73 15.07 L7.5 13.45 L5.27 15.07 L6.12 12.45 L3.89 10.83 L6.65 10.83 Z ' +
  'M16.5 8.2 L17.35 10.83 L20.11 10.83 L17.88 12.45 L18.73 15.07 L16.5 13.45 L14.27 15.07 L15.12 12.45 L12.89 10.83 L15.65 10.83 Z'

const RED = '#E53935'
const SILVER = '#DCE0E6'
const GOLD = '#E3A92B'

interface Layer { d: string; fill: string }

// Record<Rank, …> oblige à traiter chaque grade : en oublier un = erreur de compilation
const insignia: Record<Rank, Layer[]> = {
  SAPEUR_DEUXIEME_CLASSE: [],
  SAPEUR_PREMIERE_CLASSE: [{ d: SLANT_1, fill: RED }],
  CAPORAL: [{ d: SLANT_2, fill: RED }],
  CAPORAL_CHEF: [{ d: SLANT_3, fill: RED }],
  SERGENT: [{ d: SERGENT_EDGE, fill: RED }, { d: SERGENT_STRIPE, fill: SILVER }],
  SERGENT_CHEF: [{ d: SLANT_3, fill: SILVER }, { d: SLANT_3_GAPS, fill: RED }],
  ADJUDANT: [{ d: BAR, fill: GOLD }, { d: BAR_LINE, fill: RED }],
  ADJUDANT_CHEF: [{ d: BAR, fill: SILVER }, { d: BAR_LINE, fill: RED }],
  LIEUTENANT: [{ d: BARS_2, fill: SILVER }],
  CAPITAINE: [{ d: BARS_3, fill: SILVER }],
  COMMANDANT: [{ d: BARS_4, fill: SILVER }],
  LIEUTENANT_COLONEL: [{ d: BARS_5_ODD, fill: SILVER }, { d: BARS_5_EVEN, fill: GOLD }],
  COLONEL: [{ d: BARS_5_ODD, fill: SILVER }, { d: BARS_5_EVEN, fill: SILVER }],
  CONTROLEUR_GENERAL: [{ d: STARS_2, fill: SILVER }],
}

export function RankInsignia({ rank, className }: { rank: Rank; className?: string }) {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true" className={className ?? 'size-7 shrink-0'}>
      <path d={PATCH} fill="#1B2433" />
      {insignia[rank].map((layer, index) => (
        <path key={index} d={layer.d} fill={layer.fill} />
      ))}
    </svg>
  )
}