// src/agent/components/AgentProfileDialog.tsx — le profil complet d'un agent
import type { DashboardAgentProfil } from '@/api/Request'
import { formatDate, licenseLabels, rankLabels, seniority } from '@/display'
import { cn } from '@/lib/utils'
import { Badge } from '@/components/ui/badge'
import {
  Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle,
} from '@/components/ui/dialog'

import { RegistrationRow, Section, chip, pill } from './Card'
import { RankInsignia } from './RankGenerator'

type Props = {
  agent: DashboardAgentProfil | null
  onClose: () => void
}

export function AgentProfileDialog({ agent, onClose }: Props) {
  const registrations = agent
    ? [...agent.registration].sort((a, b) => b.startDate.localeCompare(a.startDate))
    : []

  return (
    <Dialog open={agent !== null} onOpenChange={(open) => { if (!open) onClose() }}>
      <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg bg-background text-ink shadow-xl">
        {agent && (
          <>
            <DialogHeader>
              <DialogTitle className="text-xl">
                {agent.lastName.toUpperCase()} {agent.firstName}
              </DialogTitle>
              <DialogDescription>
                Matricule {agent.matricule} · {agent.isActive ? 'Actif' : 'Archivé'}
              </DialogDescription>
            </DialogHeader>

            <div className="flex flex-col gap-5">
              {/* Grade */}
              <div className="flex items-center gap-3 rounded-[10px] bg-page-soft px-3.5 py-3">
                <RankInsignia rank={agent.rank} />
                <div>
                  <p className="font-title text-[19px] font-bold uppercase tracking-wide">
                    {rankLabels[agent.rank]}
                  </p>
                  <p className="text-[13px] text-ink-muted">
                    Depuis le {formatDate(agent.rankStartDate)} · {seniority(agent.rankStartDate)}
                  </p>
                </div>
              </div>

              {agent.isDifferentiated && (
                <Badge variant="outline" className={cn(pill, 'self-start border-[#3D5A80] text-[#2F4A6E]')}>
                  Parcours différencié
                </Badge>
              )}

              <Section title="Permis">
                <div className="flex flex-wrap gap-1.5">
                  {agent.licenses.length === 0 && <p className="text-sm text-ink-muted">Aucun permis</p>}
                  {agent.licenses.map((license) => (
                    <Badge key={license} className={cn(chip, 'bg-navy font-semibold text-white')}>
                      {licenseLabels[license]}
                    </Badge>
                  ))}
                </div>
              </Section>

              {/* Toutes les compétences, sans limite */}
              <Section title={`Compétences · ${agent.skills.length}`}>
                <div className="flex flex-wrap gap-1.5">
                  {agent.skills.length === 0 && <p className="text-sm text-ink-muted">Aucune compétence</p>}
                  {agent.skills.map((skill) => (
                    <Badge key={skill} className={cn(chip, 'bg-chip text-[#2E3440]')}>
                      {skill}
                    </Badge>
                  ))}
                </div>
              </Section>

              {/* Tout l'historique */}
              <Section title={`Historique des inscriptions · ${registrations.length}`}>
                {registrations.length === 0 ? (
                  <p className="text-sm text-ink-muted">Aucune inscription</p>
                ) : (
                  <div className="flex flex-col gap-2">
                    {registrations.map((registration) => (
                      <RegistrationRow key={registration.id} registration={registration} />
                    ))}
                  </div>
                )}
              </Section>
            </div>
          </>
        )}
      </DialogContent>
    </Dialog>
  )
}