import { type FC, type FormEvent, type KeyboardEvent } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createAgent, getRanks, getSkills, type License, type Rank } from '../../../api/Request'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../../../components/ui/dialog'
import { Field, FieldGroup } from '../../../components/ui/field'
import { Label } from '../../../components/ui/label'
import { Input } from '../../../components/ui/input'
import { licenseLabels, rankLabels } from '../../../display'
import { Button } from '../../../components/ui/button'


const LICENSES: License[] = ['PERMIS_B', 'PERMIS_C']

const selectClass =
  'h-9 w-full rounded-md border border-input bg-white px-2.5 text-sm ' +
  'focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-navy'

// Une case à cocher déguisée en pastille : la case est cachée, le label change de couleur quand elle est cochée
const toggleChipClass =
  'cursor-pointer select-none rounded-md border border-line-strong px-2.5 py-1 text-[13px] font-medium text-ink-soft ' +
  'has-[:checked]:border-navy has-[:checked]:bg-navy has-[:checked]:text-white ' +
  'has-[:focus-visible]:outline-2 has-[:focus-visible]:outline-offset-2 has-[:focus-visible]:outline-navy'

interface Props {
  isOpen: boolean
  onClose: () => void
}

export const AddAgentDialog: FC<Props> = ({ isOpen, onClose }) => {
  const queryClient = useQueryClient()

  const { data: ranks = [] } = useQuery({ queryKey: ['ranks'], queryFn: getRanks })
  const { data: skills = [], error: skillsError } = useQuery({
    queryKey: ['skills'],
    queryFn: getSkills,
    enabled: isOpen, // chargées seulement à l'ouverture
  })

  const creation = useMutation({
    mutationFn: createAgent,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['agents'] }) // la nouvelle carte apparaît
      handleClose()
    },
  })

  function handleClose() {
    creation.reset() // efface l'éventuel message d'erreur
    onClose()
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault() // sinon le navigateur recharge la page

    // FormData lit tous les champs du formulaire grâce à leur attribut name
    const data = new FormData(event.currentTarget)

    creation.mutate({
      matricule: Number(data.get('matricule')),
      lastName: String(data.get('lastName')).trim(),
      firstName: String(data.get('firstName')).trim(),
      rank: data.get('rank') as Rank,
      rankStartDate: String(data.get('rankStartDate')), // "2026-11-09", le format de LocalDate
      licenses: data.getAll('licenses') as License[], // toutes les cases cochées de ce nom
      skills: data.getAll('skills').map(String),
      isDifferentiated: data.get('isDifferentiated') === 'on',
    })
  }

  // Entrée dans un champ ne valide pas le formulaire : seul le bouton le fait
  function blockEnter(event: KeyboardEvent<HTMLFormElement>) {
    if (event.key === 'Enter' && event.target instanceof HTMLInputElement) {
      event.preventDefault()
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={(open) => { if (!open) handleClose() }}>
      <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-lg">
        <form onSubmit={handleSubmit} onKeyDown={blockEnter} className="flex flex-col gap-5">
          <DialogHeader>
            <DialogTitle>Ajouter un agent</DialogTitle>
            <DialogDescription>Identité, grade, permis et compétences de l'agent.</DialogDescription>
          </DialogHeader>

          <FieldGroup>
            <div className="grid grid-cols-2 gap-3">
              <Field>
                <Label htmlFor="lastName">Nom</Label>
                <Input id="lastName" name="lastName" required autoComplete="off" />
              </Field>
              <Field>
                <Label htmlFor="firstName">Prénom</Label>
                <Input id="firstName" name="firstName" required autoComplete="off" />
              </Field>
            </div>

            <Field>
              <Label htmlFor="matricule">Matricule</Label>
              <Input id="matricule" name="matricule" type="number" min={1} required inputMode="numeric" />
            </Field>

            <div className="grid grid-cols-2 gap-3">
              <Field>
                <Label htmlFor="rank">Grade</Label>
                <select id="rank" name="rank" required defaultValue="" className={selectClass}>
                  <option value="" disabled>Choisir un grade</option>
                  {ranks.map((rank) => (
                    <option key={rank} value={rank}>{rankLabels[rank]}</option>
                  ))}
                </select>
              </Field>
              <Field>
                <Label htmlFor="rankStartDate">Grade depuis le</Label>
                <Input id="rankStartDate" name="rankStartDate" type="date" required />
              </Field>
            </div>

            <fieldset className="flex flex-col gap-2">
              <legend className="mb-2 text-sm font-medium">Permis</legend>
              <div className="flex flex-wrap gap-1.5">
                {LICENSES.map((license) => (
                  <label key={license} className={toggleChipClass}>
                    <input type="checkbox" name="licenses" value={license} className="sr-only" />
                    {licenseLabels[license]}
                  </label>
                ))}
              </div>
            </fieldset>

            <fieldset className="flex flex-col gap-2">
              <legend className="mb-2 text-sm font-medium">Compétences</legend>
              {skillsError ? (
                <p role="alert" className="text-sm text-[#9B2219]">Impossible de charger les compétences.</p>
              ) : (
                <div className="flex max-h-40 flex-wrap gap-1.5 overflow-y-auto">
                  {skills.map((skill) => (
                    <label key={skill.name} className={toggleChipClass}>
                      <input type="checkbox" name="skills" value={skill.name} className="sr-only" />
                      {skill.name}
                    </label>
                  ))}
                </div>
              )}
            </fieldset>

            <label className="flex cursor-pointer items-center gap-2 text-sm">
              <input type="checkbox" name="isDifferentiated" className="size-4 accent-navy" />
              Parcours différencié
            </label>
          </FieldGroup>

          {creation.error && (
            <p role="alert" className="rounded-[10px] bg-[#FBE4E2] px-3 py-2 text-sm font-semibold text-[#9B2219]">
              {creation.error.message}
            </p>
          )}

          <DialogFooter>
            <Button type="button" variant="outline" className="h-11" onClick={handleClose}>
              Annuler
            </Button>
            <Button type="submit" className="h-11 bg-brand-red text-white hover:bg-brand-red-dark"
                    disabled={creation.isPending}>
              {creation.isPending ? 'Enregistrement…' : 'Ajouter'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}