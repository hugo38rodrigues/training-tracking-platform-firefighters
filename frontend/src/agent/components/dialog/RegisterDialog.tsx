import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { getAvailableTrainings, registerAgentToTraining, type DashboardAgentProfil } from "../../../api/Request"
import { useState } from "react"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "../../../components/ui/dialog"
import { formatDate } from "../../../display"
import { Button } from "../../../components/ui/button"


interface Props  {
  agent: DashboardAgentProfil | null
  onClose: () => void
}

export function RegisterDialog({ agent, onClose }: Props) {
  const queryClient = useQueryClient()
  const [formationNumber, setFormationNumber] = useState<number | null>(null)

  // Les formations accessibles : demandées seulement quand la boîte est ouverte
  const { data: trainings = [], isPending, error } = useQuery({
    queryKey: ['available-trainings', agent?.matricule],
    queryFn: () => getAvailableTrainings(agent!.matricule),
    enabled: agent !== null,
  })


  const registration = useMutation({
    mutationFn: registerAgentToTraining,
    onSuccess: async() => {
      await queryClient.invalidateQueries({ queryKey: ['agents'] })
      await queryClient.invalidateQueries({ queryKey: ['available-trainings'] })
      handleClose()
    },
  })

  function handleClose() {
    setFormationNumber(null)
    registration.reset() 
    onClose()
  }

  return (
    <Dialog open={agent !== null} onOpenChange={(open) => { if (!open) handleClose() }}>
      <DialogContent className="sm:max-w-lg bg-white text-ink shadow-xl">
        {agent && (
          <>
            <DialogHeader>
              <DialogTitle>
                Inscrire {agent.lastName.toUpperCase()} {agent.firstName}
              </DialogTitle>
              <DialogDescription>Formations accessibles avec les compétences de l'agent</DialogDescription>
            </DialogHeader>

            {isPending ? (
              <p className="text-sm text-ink-muted">Chargement des formations…</p>
            ) : error ? (
              <p role="alert" className="text-sm text-[#9B2219]">
                Impossible de charger les formations : {error.message}
              </p>
            ) : trainings.length === 0 ? (
              <p className="rounded-[10px] border border-dashed border-line-strong px-3 py-4 text-center text-sm text-ink-muted">
                Aucune formation accessible pour cet agent.
              </p>
            ) : (
              <fieldset className="flex max-h-72 flex-col gap-2 overflow-y-auto">
                <legend className="sr-only">Formation</legend>
                {trainings.map((training) => (
                  <label
                    key={training.formationNumber}
                    className="flex cursor-pointer items-center gap-3 rounded-[10px] border border-line px-3 py-2.5
                               hover:bg-page-soft has-[:checked]:border-navy has-[:checked]:bg-[#E3ECFB]"
                  >
                    <input
                      type="radio"
                      name="training"
                      checked={formationNumber === training.formationNumber}
                      onChange={() => setFormationNumber(training.formationNumber)}
                      className="size-4 shrink-0 accent-navy"
                    />
                    <span className="min-w-0">
                      <span className="block text-sm font-semibold">{training.name}</span>
                      <span className="block text-[13px] text-ink-muted">
                        N° {training.formationNumber} · du {formatDate(training.startDate)} au{' '}
                        {formatDate(training.endDate)}
                      </span>
                    </span>
                  </label>
                ))}
              </fieldset>
            )}

            {registration.error && (
              <p role="alert" className="rounded-[10px] bg-[#FBE4E2] px-3 py-2 text-sm font-semibold text-[#9B2219]">
                {registration.error.message}
              </p>
            )}

            <DialogFooter>
              <Button variant="outline" className="h-11" onClick={handleClose}>
                Annuler
              </Button>
              <Button
                className="h-11 bg-navy bg-background hover:bg-navy-light"
                disabled={formationNumber === null || registration.isPending}
                onClick={() => {
                  if (formationNumber === null) return
                  registration.mutate({ agentMatricule: agent.matricule, formationNumber })
                }}
              >
                {registration.isPending ? 'Inscription…' : 'Inscrire'}
              </Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  )
}