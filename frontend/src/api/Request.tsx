
export type Rank =
  | 'SAPEUR_DEUXIEME_CLASSE' | 'SAPEUR_PREMIERE_CLASSE'
  | 'CAPORAL' | 'CAPORAL_CHEF'
  | 'SERGENT' | 'SERGENT_CHEF'
  | 'ADJUDANT' | 'ADJUDANT_CHEF'
  | 'LIEUTENANT' | 'CAPITAINE' | 'COMMANDANT' | 'LIEUTENANT_COLONEL' | 'COLONEL'
  | 'CONTROLEUR_GENERAL';

export type License = 'PERMIS_B' | 'PERMIS_C';

export type RegistrationStatus = 'PENDING' | 'IN_PROGRESS' | 'DONE' | 'FAILED' | 'CANCEL';

export interface DashboardRegistration {
  id: number;                 
  name: string;
  startDate: string;          
  endDate: string;
  status: RegistrationStatus;
}

export interface DashboardAgentProfil {
  matricule: number;
  lastName: string;
  firstName: string;
  rank: Rank;
  rankStartDate: string;
  licenses: License[];
  skills: string[];
  isActive: boolean;
  isDifferentiated: boolean;
  registration: DashboardRegistration[];
}

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, options);
  if (!response.ok) {
    throw new Error(`Erreur ${response.status}`);
  }
  if (response.status === 204) {
    return null as T;
  }
  const data = await response.json();   // une seule lecture
  console.log(data);                    // tu peux afficher la variable autant de fois que tu veux
  return data as T;
}

export const getAgents = () => request<DashboardAgentProfil[]>('/api/agents/');