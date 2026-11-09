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

export type DashboardRegistration = {
  id: number
  name: string
  startDate: string
  endDate: string
  status: RegistrationStatus
}

export type DashboardAgentProfil = {
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
export type TrainingOption = {
  formationNumber: number
  name: string
  startDate: string
  endDate: string
}

/* ---------- Fonction commune à tous les appels ---------- */

function parseJson(text: string) {
  try {
    return JSON.parse(text)
  } catch {
    return null // réponse vide ou pas du JSON (page d'erreur HTML…)
  }
}

async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await fetch(url, init)

  const text = await response.text()
  const body = text ? parseJson(text) : null
	console.log(response)
  if (!response.ok) {
    // Message envoyé par le @RestControllerAdvice : { "message": "…" }
    throw new Error(body?.message ?? `Erreur ${response.status}`)
  }
  return body as T
}

/* ---------- Appels ---------- */

export const getAgents = () => request<DashboardAgentProfil[]>('/api/agents/')

export const getRanks = () => request<Rank[]>('/api/ranks/')

export const getAvailableTrainings = (matricule: number) =>
  request<TrainingOption[]>(`/api/agents/${matricule}/available-trainings`)

export const registerAgentToTraining = (body: { agentMatricule: number; formationNumber: number }) =>
  request<void>('/api/registrations', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
	export type Skill = { name: string }

// Ce qu'on envoie pour créer un agent : le profil, sans ce que le serveur calcule lui-même
export type NewAgent = Omit<DashboardAgentProfil, 'isActive' | 'registration'>

export const getSkills = () => request<Skill[]>('/api/skills')

export const createAgent = (agent: NewAgent) =>
  request<void>('/api/agents', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(agent),
  })