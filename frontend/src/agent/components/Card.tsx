// src/agent/components/Card.tsx — la carte d'un agent (tableau de bord)
import { useState, type ReactNode } from 'react'
import { Archive } from 'lucide-react'
import type { DashboardAgentProfil, DashboardRegistration } from '../../api/Request'
import { formatDate, licenseLabels, rankLabels, seniority, statusPriority, statusStyles } from '../../display'
import { cn } from 'cn'
import { Badge } from '../../components/ui/badge'
import { Card, CardAction, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../../components/ui/card'
import { RankInsignia } from '../../RankGenerator'
import { Button } from '../../components/ui/button'



const MAX_SKILLS = 5
const MAX_REGISTRATIONS = 2

/* ---------- Éléments partagés avec les boîtes de dialogue ---------- */

// Pastille rectangulaire (permis, compétences)
export const chip = 'h-auto rounded-md px-2.5 py-1 text-[13px] font-medium'
// Pastille arrondie (statuts)
export const pill = 'h-auto rounded-full px-2.5 py-1 text-xs font-semibold'

export function Section({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className="flex flex-col gap-2">
      <h3 className="text-xs font-semibold uppercase tracking-[0.06em] text-ink-muted">{title}</h3>
      {children}
    </section>
  )
}

// Une ligne d'inscription : utilisée dans la carte et dans le profil
export function RegistrationRow({ registration }: { registration: DashboardRegistration }) {
  return (
    <div className="flex items-center justify-between gap-3 rounded-[10px] border border-line px-3 py-2.5">
      <div className="min-w-0">
        <p className="text-sm font-semibold">{registration.name}</p>
        <p className="text-[13px] text-ink-muted">
          {formatDate(registration.startDate)} → {formatDate(registration.endDate)}
        </p>
      </div>
      <Badge className={cn(pill, 'shrink-0', statusStyles[registration.status].className)}>
        {statusStyles[registration.status].label}
      </Badge>
    </div>
  )
}

/* ---------- La carte ---------- */

interface AgentCardProps {
  agent: DashboardAgentProfil
  onViewProfile?: (agent: DashboardAgentProfil) => void
  onRegister?: (agent: DashboardAgentProfil) => void
  onArchive?: (agent: DashboardAgentProfil) => void
}

export function AgentCard({ agent, onViewProfile, onRegister, onArchive }: AgentCardProps) {
  const [showAllSkills, setShowAllSkills] = useState(false)
  const [showAllRegistrations, setShowAllRegistrations] = useState(false)

  const hiddenSkills = agent.skills.length - MAX_SKILLS
  const shownSkills = showAllSkills ? agent.skills : agent.skills.slice(0, MAX_SKILLS)

  // Les inscriptions en cours et en attente d'abord
  const registrations = [...agent.registration].sort(
    (a, b) => statusPriority[a.status] - statusPriority[b.status],
  )
  const hiddenRegistrations = registrations.length - MAX_REGISTRATIONS
  const shownRegistrations = showAllRegistrations
    ? registrations
    : registrations.slice(0, MAX_REGISTRATIONS)

  return (
    <Card className="gap-4 rounded-[14px] border border-line py-5">
      {/* Identité + statut */}
      <CardHeader>
        <CardTitle className="text-lg font-semibold leading-tight">
          {agent.lastName.toUpperCase()} {agent.firstName}
        </CardTitle>
        <CardDescription className="text-[13px] text-ink-muted">Matricule {agent.matricule}</CardDescription>
        <CardAction>
          <Badge
            className={cn(
              pill,
              agent.isActive ? 'bg-[#E3F1EA] text-[#1E6B45]' : 'bg-[#ECEBE8] text-[#4A4F57]',
            )}
          >
            {agent.isActive ? 'Actif' : 'Archivé'}
          </Badge>
        </CardAction>
      </CardHeader>

      <CardContent className="flex flex-col gap-4">
        {/* Grade */}
        <div className="flex items-center gap-3 rounded-[10px] bg-page-soft px-3.5 py-3">
          <RankInsignia rank={agent.rank} />
          <div>
            <p className="font-title text-[19px] font-bold uppercase tracking-wide">{rankLabels[agent.rank]}</p>
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

        <Section title={`Compétences · ${agent.skills.length}`}>
          <div className="flex flex-wrap gap-1.5">
            {shownSkills.map((skill) => (
              <Badge key={skill} className={cn(chip, 'bg-chip text-[#2E3440]')}>
                {skill}
              </Badge>
            ))}
            {hiddenSkills > 0 && (
              <button
                type="button"
                aria-expanded={showAllSkills}
                onClick={() => setShowAllSkills((shown) => !shown)}
                className={cn(
                  chip,
                  'cursor-pointer border border-line-strong font-semibold text-ink-soft hover:border-ink-soft',
                )}
              >
                {showAllSkills ? 'Réduire' : `+${hiddenSkills}`}
              </button>
            )}
          </div>
        </Section>

        <Section title="Inscriptions">
          {registrations.length === 0 ? (
            <p className="rounded-[10px] border border-dashed border-line-strong px-3 py-2.5 text-sm text-ink-muted">
              Aucune inscription
            </p>
          ) : (
            <div className="flex flex-col gap-2">
              {shownRegistrations.map((registration) => (
                <RegistrationRow key={registration.id} registration={registration} />
              ))}
              {hiddenRegistrations > 0 && (
                <Button
                  variant="outline"
                  aria-expanded={showAllRegistrations}
                  onClick={() => setShowAllRegistrations((shown) => !shown)}
                  className="h-11 border-dashed border-line-strong font-semibold text-ink-soft"
                >
                  {showAllRegistrations
                    ? 'Réduire'
                    : `+${hiddenRegistrations} autre${hiddenRegistrations > 1 ? 's' : ''} inscription${hiddenRegistrations > 1 ? 's' : ''}`}
                </Button>
              )}
            </div>
          )}
        </Section>
      </CardContent>

      {/* Actions, toujours en bas de la carte */}
      <CardFooter className="mt-auto gap-2 border-t-0 bg-transparent pt-1">
        <Button
          variant="outline"
          className="h-11 flex-1 border-navy text-navy hover:bg-chip"
          onClick={() => onViewProfile?.(agent)}
        >
          Voir le profil
        </Button>
        <Button
          className="h-11 flex-1 bg-navy text-white hover:bg-navy-light"
          disabled={!agent.isActive}
          onClick={() => onRegister?.(agent)}
        >
          Inscrire
        </Button>
        {agent.isActive && (
          <Button
            variant="outline"
            size="icon"
            aria-label="Archiver l'agent"
            title="Archiver l'agent"
            className="size-11 shrink-0 border-line-strong text-ink-soft hover:text-brand-red"
            onClick={() => onArchive?.(agent)}
          >
            <Archive aria-hidden="true" />
          </Button>
        )}
      </CardFooter>
    </Card>
  )
}