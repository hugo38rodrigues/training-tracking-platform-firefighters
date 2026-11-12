import type { FC, KeyboardEvent } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { createAgent, getRanks, getSkills, type License, type Rank } from '../../../api/Request'
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
} from '../../../components/ui/dialog'
import { Field, FieldGroup } from '../../../components/ui/field'
import { Label } from '../../../components/ui/label'
import { Input } from '../../../components/ui/input'
import { licenseLabels, rankLabels } from '../../../display'
import { Button } from '../../../components/ui/button'

// Les listes de valeurs viennent des labels : une seule source de vérité
const RANKS = Object.keys(rankLabels) as [Rank, ...Rank[]]
const LICENSES = Object.keys(licenseLabels) as [License, ...License[]]

// Le schéma joue le rôle d'un DTO validé (comme @NotNull / @Min en Java)
const agentSchema = z.object({
	matricule: z.number({ message: 'Matricule obligatoire' }).int().min(1, 'Matricule invalide'),
	lastName: z.string().trim().min(1, 'Nom obligatoire'),
	firstName: z.string().trim().min(1, 'Prénom obligatoire'),
	rank: z.enum(RANKS, { message: 'Grade obligatoire' }),
	rankStartDate: z.string().min(1, 'Date obligatoire'), // "2026-11-09", le format de LocalDate
	licenses: z.array(z.enum(LICENSES)),
	skills: z.array(z.string()),
	isDifferentiated: z.boolean(),
})

// Le type TypeScript est déduit du schéma
type AgentForm = z.infer<typeof agentSchema>

const defaultValues: Partial<AgentForm> = {
	lastName: '',
	firstName: '',
	rankStartDate: '',
	licenses: [],
	skills: [],
	isDifferentiated: false,
}

const selectClass =
	'h-9 w-full rounded-md border border-input bg-white px-2.5 text-sm ' +
	'focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-navy'

// Une case à cocher déguisée en pastille : la case est cachée, le label change de couleur quand elle est cochée
const toggleChipClass =
	'cursor-pointer select-none rounded-md border border-line-strong px-2.5 py-1 text-[13px] font-medium text-ink-soft ' +
	'has-[:checked]:border-navy has-[:checked]:bg-navy has-[:checked]:text-white ' +
	'has-[:focus-visible]:outline-2 has-[:focus-visible]:outline-offset-2 has-[:focus-visible]:outline-navy'

const errorClass = 'text-sm text-[#9B2219]'

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
		enabled: isOpen,
	})

	const {
		register,
		handleSubmit,
		reset,
		formState: { errors },
	} = useForm<AgentForm>({
		resolver: zodResolver(agentSchema),
		defaultValues,
	})

	const creation = useMutation({
		mutationFn: createAgent,
		onSuccess: async () => {
			await queryClient.invalidateQueries({ queryKey: ['agents'] })
		},
	})

	const handleClose = () => {
		creation.reset() // efface l'éventuel message d'erreur serveur
		reset() // vide le formulaire pour la prochaine ouverture
		onClose()
	}

	// Appelée uniquement si le schéma est valide : agent est déjà typé et nettoyé
	const onSubmit = (agent: AgentForm) => {
		creation.mutate(agent, { onSuccess: handleClose })
	}

	// Entrée dans un champ ne valide pas le formulaire : seul le bouton le fait
	const blockEnter = (event: KeyboardEvent<HTMLFormElement>) => {
		if (event.key === 'Enter' && event.target instanceof HTMLInputElement) {
			event.preventDefault()
		}
	}

	return (
		<Dialog
			open={isOpen}
			onOpenChange={(open) => {
				if (!open) handleClose()
			}}
		>
			<DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-lg">
				<form
					// handleSubmit renvoie une Promise : void évite l'erreur no-floating-promises
					onSubmit={(event) => void handleSubmit(onSubmit)(event)}
					onKeyDown={blockEnter}
					noValidate // les messages viennent de Zod, pas du navigateur
					className="flex flex-col gap-5"
				>
					<DialogHeader>
						<DialogTitle>Ajouter un agent</DialogTitle>
						<DialogDescription>
							Identité, grade, permis et compétences de l'agent.
						</DialogDescription>
					</DialogHeader>

					<FieldGroup>
						<div className="grid grid-cols-2 gap-3">
							<Field>
								<Label htmlFor="lastName">Nom</Label>
								<Input
									id="lastName"
									autoComplete="off"
									aria-invalid={!!errors.lastName}
									{...register('lastName')}
								/>
								{errors.lastName && <p className={errorClass}>{errors.lastName.message}</p>}
							</Field>
							<Field>
								<Label htmlFor="firstName">Prénom</Label>
								<Input
									id="firstName"
									autoComplete="off"
									aria-invalid={!!errors.firstName}
									{...register('firstName')}
								/>
								{errors.firstName && <p className={errorClass}>{errors.firstName.message}</p>}
							</Field>
						</div>

						<Field>
							<Label htmlFor="matricule">Matricule</Label>
							<Input
								id="matricule"
								type="number"
								min={1}
								inputMode="numeric"
								aria-invalid={!!errors.matricule}
								{...register('matricule', { valueAsNumber: true })}
							/>
							{errors.matricule && <p className={errorClass}>{errors.matricule.message}</p>}
						</Field>

						<div className="grid grid-cols-2 gap-3">
							<Field>
								<Label htmlFor="rank">Grade</Label>
								<select
									id="rank"
									defaultValue=""
									className={selectClass}
									aria-invalid={!!errors.rank}
									{...register('rank')}
								>
									<option value="" disabled>
										Choisir un grade
									</option>
									{ranks.map((rank) => (
										<option key={rank} value={rank}>
											{rankLabels[rank]}
										</option>
									))}
								</select>
								{errors.rank && <p className={errorClass}>{errors.rank.message}</p>}
							</Field>
							<Field>
								<Label htmlFor="rankStartDate">Grade depuis le</Label>
								<Input
									id="rankStartDate"
									type="date"
									aria-invalid={!!errors.rankStartDate}
									{...register('rankStartDate')}
								/>
								{errors.rankStartDate && (
									<p className={errorClass}>{errors.rankStartDate.message}</p>
								)}
							</Field>
						</div>

						<fieldset className="flex flex-col gap-2">
							<legend className="mb-2 text-sm font-medium">Permis</legend>
							<div className="flex flex-wrap gap-1.5">
								{LICENSES.map((license) => (
									<label key={license} className={toggleChipClass}>
										<input
											type="checkbox"
											value={license}
											className="sr-only"
											{...register('licenses')}
										/>
										{licenseLabels[license]}
									</label>
								))}
							</div>
						</fieldset>

						<fieldset className="flex flex-col gap-2">
							<legend className="mb-2 text-sm font-medium">Compétences</legend>
							{skillsError ? (
								<p role="alert" className={errorClass}>
									Impossible de charger les compétences.
								</p>
							) : (
								<div className="flex max-h-40 flex-wrap gap-1.5 overflow-y-auto">
									{skills.map((skill) => (
										<label key={skill.name} className={toggleChipClass}>
											<input
												type="checkbox"
												value={skill.name}
												className="sr-only"
												{...register('skills')}
											/>
											{skill.name}
										</label>
									))}
								</div>
							)}
						</fieldset>

						<label className="flex cursor-pointer items-center gap-2 text-sm">
							<input
								type="checkbox"
								className="size-4 accent-navy"
								{...register('isDifferentiated')}
							/>
							Parcours différencié
						</label>
					</FieldGroup>

					{creation.error && (
						<p
							role="alert"
							className="rounded-[10px] bg-[#FBE4E2] px-3 py-2 text-sm font-semibold text-[#9B2219]"
						>
							{creation.error.message}
						</p>
					)}

					<DialogFooter>
						<Button type="button" variant="outline" className="h-11" onClick={handleClose}>
							Annuler
						</Button>
						<Button
							type="submit"
							className="h-11 bg-brand-red text-white hover:bg-brand-red-dark"
							disabled={creation.isPending}
						>
							{creation.isPending ? 'Enregistrement…' : 'Ajouter'}
						</Button>
					</DialogFooter>
				</form>
			</DialogContent>
		</Dialog>
	)
}
