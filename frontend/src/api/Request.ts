export type Rank =
	| 'SAPEUR_DEUXIEME_CLASSE'
	| 'SAPEUR_PREMIERE_CLASSE'
	| 'CAPORAL'
	| 'CAPORAL_CHEF'
	| 'SERGENT'
	| 'SERGENT_CHEF'
	| 'ADJUDANT'
	| 'ADJUDANT_CHEF'
	| 'LIEUTENANT'
	| 'CAPITAINE'
	| 'COMMANDANT'
	| 'LIEUTENANT_COLONEL'
	| 'COLONEL'
	| 'CONTROLEUR_GENERAL'

export type License = 'PERMIS_B' | 'PERMIS_C'

export type RegistrationStatus = 'PENDING' | 'IN_PROGRESS' | 'DONE' | 'CANCEL' | 'FAILED'

export interface DashboardRegistration {
	id: number
	name: string
	startDate: string
	endDate: string
	status: RegistrationStatus
}

export interface DashboardAgentProfil {
	matricule: number
	lastName: string
	firstName: string
	rank: Rank
	rankStartDate: string
	licenses: License[]
	skills: string[]
	isActive: boolean
	isDifferentiated: boolean
	registration: DashboardRegistration[]
}

// Une formation à laquelle l'agent peut s'inscrire
export interface TrainingOption {
	trainingNumber: number
	name: string
	startDate: string
	endDate: string
}

export interface Skill {
	name: string
}

// Ce qu'on envoie pour créer un agent : le profil, sans ce que le serveur calcule lui-même
export type NewAgent = Omit<DashboardAgentProfil, 'isActive' | 'registration'>

/* ---------- Fonctions communes à tous les appels ---------- */

// JSON.parse peut renvoyer n'importe quoi : unknown oblige à vérifier avant d'utiliser
const parseJson = (text: string): unknown => {
	try {
		return JSON.parse(text)
	} catch {
		return null
	}
}

const getErrorMessage = (body: unknown): string | null =>
	typeof body === 'object' && body !== null && 'message' in body && typeof body.message === 'string'
		? body.message
		: null

const request = async <T>(url: string, init?: RequestInit): Promise<T> => {
	const response = await fetch(url, init)

	const text = await response.text()
	const body = text ? parseJson(text) : null

	if (!response.ok) {
		throw new Error(getErrorMessage(body) ?? `Erreur ${response.status}`)
	}
	return body as T
}

const postJson = <T>(url: string, body: unknown) =>
	request<T>(url, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(body),
	})

/* ---------- Appels ---------- */

export const getAgents = () => request<DashboardAgentProfil[]>('/api/agents')

export const getRanks = () => request<Rank[]>('/api/ranks')

export const getSkills = () => request<Skill[]>('/api/skills')

export const getAvailableTrainings = (matricule: number) =>
	request<TrainingOption[]>(`/api/trainings/available-trainings?matricule=${matricule}`)

export const createAgent = (agent: NewAgent) => postJson<void>('/api/agents', agent)

export const registerAgentToTraining = (body: {
	agentMatricule: number
	formationNumber: number
}) => postJson<void>('/api/registrations', body)
