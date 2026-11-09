import { Button } from '@/components/ui/button'
import { Plus } from 'lucide-react'
import type { FC } from 'react'

export const Header: FC = () => {

	const addAgent = (): void => {
		
	}	

	return (
		 <header className="mb-6 flex flex-wrap items-end justify-between gap-4">
      <div>
        <h1 className="font-title text-4xl font-bold text-ink">Agents</h1>
        <p className="text-ink-muted">Profil, grade, compétences et inscriptions de chaque agent</p>
      </div>
      <Button onClick={addAgent} className="min-h-11 gap-2 bg-brand-red px-5 text-white hover:bg-brand-red-dark">
        <Plus aria-hidden="true" />
        Ajouter un agent
      </Button>
    </header>
	)
}