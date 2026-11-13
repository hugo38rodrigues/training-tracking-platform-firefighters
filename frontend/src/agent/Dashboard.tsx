import { useQuery } from '@tanstack/react-query'
import { cn } from 'cn'
import { Search, X } from 'lucide-react'
import { useState } from 'react'
import { getAgents, getRanks, type Rank } from '../api/Request'
import { Button } from '../components/ui/button'
import { Card } from '../components/ui/card'
import {
	Combobox,
	ComboboxContent,
	ComboboxEmpty,
	ComboboxInput,
	ComboboxItem,
	ComboboxList,
} from '../components/ui/combobox'
import { Input } from '../components/ui/input'
import { Label } from '../components/ui/label'
import { rankLabels } from '../display'
import { Header } from './components/Header'
import { ViewAgent, type AgentStatusFilter } from './ViewAgent'

const statusOptions: { value: AgentStatusFilter; label: string }[] = [
	{ value: 'active', label: 'Actifs' },
	{ value: 'archived', label: 'Archivés' },
	{ value: 'all', label: 'Tous' },
]

function StatCard({ value, label }: { value: number; label: string }) {
	return (
		<Card className="gap-0.5 px-4.5 py-4">
			<span className="font-title text-[34px] font-bold leading-tight">{value}</span>
			<span className="text-sm text-ink-muted">{label}</span>
		</Card>
	)
}

export const Dashboard = () => {
	const [search, setSearch] = useState('')
	const [selectedRank, setSelectedRank] = useState<Rank | null>(null)
	const [status, setStatus] = useState<AgentStatusFilter>('active')

	const { data: ranks = [], error: ranksError } = useQuery({
		queryKey: ['ranks'],
		queryFn: getRanks,
	})
	const { data: agents = [] } = useQuery({ queryKey: ['agents'], queryFn: getAgents })

	const hasFilters = search !== '' || selectedRank !== null || status !== 'active'
	const resetFilters = () => {
		setSearch('')
		setSelectedRank(null)
		setStatus('active')
	}

	const registrations = agents.flatMap((agent) => agent.registration)

	return (
		<div className="flex flex-col gap-6">
			<Header />

			<div role="search" className="flex flex-wrap items-end gap-4">
				{/* Recherche */}
				<div className="flex min-w-0 flex-[1_1_280px] flex-col gap-1.5">
					<Label htmlFor="agent-search">Rechercher</Label>
					<div className="relative">
						<Search
							aria-hidden="true"
							className="pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2 text-ink-muted"
						/>
						<Input
							id="agent-search"
							type="search"
							value={search}
							onChange={(e) => setSearch(e.target.value)}
							placeholder="Nom, prénom ou matricule"
							className="h-11 bg-white pl-9"
						/>
					</div>
				</div>

				{/* Grade */}
				<div className="flex w-full flex-col gap-1.5 sm:w-56">
					<Label htmlFor="rank-filter">Grade</Label>
					<Combobox
						items={ranks}
						value={selectedRank}
						onValueChange={setSelectedRank}
						itemToStringLabel={(rank: Rank) => rankLabels[rank]}
					>
						<ComboboxInput
							id="rank-filter"
							placeholder="Tous les grades"
							className="h-11 bg-white"
						/>
						<ComboboxContent>
							<ComboboxEmpty>Aucun grade trouvé.</ComboboxEmpty>
							<ComboboxList>
								{(rank: Rank) => (
									<ComboboxItem key={rank} value={rank}>
										{rankLabels[rank]}
									</ComboboxItem>
								)}
							</ComboboxList>
						</ComboboxContent>
					</Combobox>
				</div>

				{/* Statut */}
				<fieldset className="flex flex-col">
					<legend className="mb-1.5 text-sm font-medium">Statut</legend>
					<div className="flex gap-1 rounded-[10px] bg-[#E7E3DB] p-1">
						{statusOptions.map((option) => {
							const selected = status === option.value
							return (
								<button
									key={option.value}
									type="button"
									aria-pressed={selected}
									onClick={() => setStatus(option.value)}
									className={cn(
										'min-h-9 cursor-pointer rounded-[7px] px-4 text-sm transition-colors',
										selected
											? 'bg-white font-semibold text-ink shadow-sm'
											: 'text-ink-soft hover:text-ink',
									)}
								>
									{option.label}
								</button>
							)
						})}
					</div>
				</fieldset>

				{hasFilters && (
					<Button variant="ghost" onClick={resetFilters} className="h-11 gap-1.5 text-ink-soft">
						<X aria-hidden="true" className="size-4" />
						Effacer les filtres
					</Button>
				)}
			</div>

			{ranksError && (
				<p role="alert" className="text-sm text-[#9B2219]">
					Impossible de charger les grades.
				</p>
			)}

			{/* Compteurs */}
			<div className="grid grid-cols-[repeat(auto-fill,minmax(200px,1fr))] gap-4">
				<StatCard value={agents.length} label="agents" />
				<StatCard value={agents.filter((agent) => agent.isActive).length} label="actifs" />
				<StatCard
					value={registrations.filter((r) => r.status === 'IN_PROGRESS').length}
					label="formations en cours"
				/>
				<StatCard
					value={registrations.filter((r) => r.status === 'PENDING').length}
					label="inscriptions en attente"
				/>
			</div>

			<ViewAgent rank={selectedRank} search={search} status={status} />
		</div>
	)
}
