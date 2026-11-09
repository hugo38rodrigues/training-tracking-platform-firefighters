
import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'

import { AgentCard } from './components/Card'
import { AgentProfileDialog } from './components/AgentProfileDialog'
import { RegisterDialog } from './components/RegisterDialog'
import { getAgents, type DashboardAgentProfil, type Rank } from '../api/Request'

export type AgentStatusFilter = 'active' | 'archived' | 'all'

type Props = {
  rank: Rank | null
  search: string
  status: AgentStatusFilter
}

// "Léa" → "lea" : la recherche ignore les accents et les majuscules
function normalize(text: string) {
  return text.normalize('NFD').replace(/[̀-ͯ]/g, '').toLowerCase()
}

export function ViewAgent({ rank, search, status }: Props) {
  // TOUS les hooks avant le moindre return
  const { data: agents = [], isPending, error } = useQuery({ queryKey: ['agents'], queryFn: getAgents })
  const [profileAgent, setProfileAgent] = useState<DashboardAgentProfil | null>(null)
  const [registeringAgent, setRegisteringAgent] = useState<DashboardAgentProfil | null>(null)

  if (isPending) return <p className="text-ink-muted">Chargement…</p>

  if (error) {
    return (
      <p role="alert" className="rounded-[10px] bg-[#FBE4E2] px-4 py-3 font-semibold text-[#9B2219]">
        Impossible de charger les agents : {error.message}
      </p>
    )
  }

  const query = normalize(search.trim())

  const visibleAgents = agents.filter((agent) => {
    const matchesRank = rank === null || agent.rank === rank
    const matchesStatus =
      status === 'all' || (status === 'active' ? agent.isActive : !agent.isActive)
    const matchesSearch =
      query === '' ||
      normalize(agent.lastName).includes(query) ||
      normalize(agent.firstName).includes(query) ||
      String(agent.matricule).includes(query)

    return matchesRank && matchesStatus && matchesSearch
  })

  return (
    <>
      {visibleAgents.length === 0 ? (
        <p className="rounded-[10px] border border-dashed border-line-strong px-4 py-6 text-center text-ink-muted">
          Aucun agent ne correspond à ces filtres
        </p>
      ) : (
        <div className="grid grid-cols-[repeat(auto-fill,minmax(300px,1fr))] gap-5">
          {visibleAgents.map((agent) => (
            <AgentCard
              key={agent.matricule}
              agent={agent}
              onViewProfile={setProfileAgent}
              onRegister={setRegisteringAgent}
            />
          ))}
        </div>
      )}

      {/* Une seule boîte de chaque pour toute la page */}
      <AgentProfileDialog agent={profileAgent} onClose={() => setProfileAgent(null)} />
      <RegisterDialog agent={registeringAgent} onClose={() => setRegisteringAgent(null)} />
    </>
  )
}