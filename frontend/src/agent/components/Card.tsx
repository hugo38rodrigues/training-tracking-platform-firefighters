import type { ReactNode } from 'react';
import type { DashboardAgentProfil, License } from '../../api/Request';
import { formatDate, licenseLabels, rankLabels, seniority, statusPriority, statusStyles } from '../../display';
import { Card, CardAction, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Button } from '../../components/ui/button';


const MAX_SKILLS = 5;
const MAX_REGISTRATIONS = 2;

function Section({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="flex flex-col gap-2">
      <span className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">{title}</span>
      {children}
    </div>
  );
}

export function AgentCard({ agent }: { agent: DashboardAgentProfil }) {
  const visibleSkills = agent.skills.slice(0, MAX_SKILLS);
  const hiddenSkills = agent.skills.length - visibleSkills.length;

  const registrations = [...agent.registration]
    .sort((a, b) => statusPriority[a.status] - statusPriority[b.status]);
  const visibleRegistrations = registrations.slice(0, MAX_REGISTRATIONS);
  const hiddenRegistrations = registrations.length - visibleRegistrations.length;

  return (
    <Card>
      {/* Identité + statut */}
      <CardHeader>
        <CardTitle className="text-lg">
          {agent.lastName.toUpperCase()} {agent.firstName}
        </CardTitle>
        <CardDescription>Matricule {agent.matricule}</CardDescription>
        <CardAction>
          <Badge className={agent.isActive ? 'bg-[#E3F1EA] text-[#1E6B45]' : 'bg-[#ECEBE8] text-[#4A4F57]'}>
            {agent.isActive ? 'Actif' : 'Archivé'}
          </Badge>
        </CardAction>
      </CardHeader>

      <CardContent className="flex flex-col gap-4">
        {/* Grade */}
        <div className="rounded-lg bg-[#F7F5F1] px-4 py-3">
          <p className="font-bold uppercase tracking-wide">{rankLabels[agent.rank]}</p>
          <p className="text-sm text-muted-foreground">
            Depuis le {formatDate(agent.rankStartDate)} · {seniority(agent.rankStartDate)}
          </p>
        </div>

        {agent.isDifferentiated && (
          <Badge variant="outline" className="self-start border-[#3D5A80] text-[#2F4A6E]">
            Parcours différencié
          </Badge>
        )}

        <Section title="Permis">
          <div className="flex flex-wrap gap-1.5">
            {agent.licenses.map((license: License) => (
              <Badge key={license} className="bg-secondary text-secondary-foreground">
                {licenseLabels[license]}
              </Badge>
            ))}
          </div>
        </Section>

        <Section title={`Compétences · ${agent.skills.length}`}>
          <div className="flex flex-wrap gap-1.5">
            {visibleSkills.map((skill: string) => (
              <Badge key={skill} variant="outline" className="bg-[#EEF0F3] border-transparent">
                {skill}
              </Badge>
            ))}
            {hiddenSkills > 0 && <Badge variant="outline">+{hiddenSkills}</Badge>}
          </div>
        </Section>

        <Section title="Inscriptions">
          {registrations.length === 0 ? (
            <p className="rounded-lg border border-dashed px-3 py-2 text-sm text-muted-foreground">
              Aucune inscription
            </p>
          ) : (
            <div className="flex flex-col gap-2">
              {visibleRegistrations.map((registration) => (
                <div key={registration.id}
                     className="flex items-center justify-between gap-3 rounded-lg border px-3 py-2">
                  <div>
                    <p className="text-sm font-semibold">{registration.name}</p>
                    <p className="text-xs text-muted-foreground">
                      {formatDate(registration.startDate)} → {formatDate(registration.endDate)}
                    </p>
                  </div>
                  <Badge className={statusStyles[registration.status].className}>
                    {statusStyles[registration.status].label}
                  </Badge>
                </div>
              ))}
              {hiddenRegistrations > 0 && (
                <Button variant="outline" className="border-dashed">
                  +{hiddenRegistrations} autre{hiddenRegistrations > 1 ? 's' : ''} inscription
                  {hiddenRegistrations > 1 ? 's' : ''}
                </Button>
              )}
            </div>
          )}
        </Section>
      </CardContent>

      <CardFooter className="mt-auto gap-2">
        <Button variant="outline" className="flex-1">Voir le profil</Button>
        <Button variant="secondary" className="flex-1" disabled={!agent.isActive}>Inscrire</Button>
      </CardFooter>
    </Card>
  );
}