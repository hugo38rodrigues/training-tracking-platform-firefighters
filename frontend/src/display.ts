import type { License, Rank, RegistrationStatus } from './api/Request';

export const rankLabels: Record<Rank, string> = {
  SAPEUR_DEUXIEME_CLASSE: 'Sapeur 2e classe',
  SAPEUR_PREMIERE_CLASSE: 'Sapeur 1re classe',
  CAPORAL: 'Caporal',
  CAPORAL_CHEF: 'Caporal-chef',
  SERGENT: 'Sergent',
  SERGENT_CHEF: 'Sergent-chef',
  ADJUDANT: 'Adjudant',
  ADJUDANT_CHEF: 'Adjudant-chef',
  LIEUTENANT: 'Lieutenant',
  CAPITAINE: 'Capitaine',
  COMMANDANT: 'Commandant',
  LIEUTENANT_COLONEL: 'Lieutenant-colonel',
  COLONEL: 'Colonel',
  CONTROLEUR_GENERAL: 'Contrôleur général',
};

export const licenseLabels: Record<License, string> = {
  PERMIS_B: 'Permis B',
  PERMIS_C: 'Permis C',
};

export const statusStyles: Record<RegistrationStatus, { label: string; className: string }> = {
  PENDING:     { label: 'En attente', className: 'bg-[#FDF0DC] text-[#8A4B08]' },
  IN_PROGRESS: { label: 'En cours',   className: 'bg-[#E3ECFB] text-[#1D4E9E]' },
  DONE:        { label: 'Terminée',   className: 'bg-[#E3F1EA] text-[#1E6B45]' },
  CANCEL:      { label: 'Annulée',    className: 'bg-[#ECEBE8] text-[#4A4F57]' },
  FAILED:      { label: 'Échouée',    className: 'bg-[#FBE4E2] text-[#9B2219]' },
};

// En cours d'abord, puis en attente : ce sont les plus utiles à voir
export const statusPriority: Record<RegistrationStatus, number> = {
  IN_PROGRESS: 0, PENDING: 1, DONE: 2, FAILED: 3, CANCEL: 4,
};

export function formatDate(iso: string): string {
  const [year, month, day] = iso.split('-');
  return `${day}/${month}/${year}`;
}

/** "13 jours", "7 mois", "4 ans 7 mois"… */
export function seniority(iso: string): string {
  const [y, m, d] = iso.split('-').map(Number);
  const start = new Date(y, m - 1, d);
  const now = new Date();

  let months = (now.getFullYear() - start.getFullYear()) * 12 + (now.getMonth() - start.getMonth());
  if (now.getDate() < start.getDate()) months--;

  if (months <= 0) {
    const days = Math.floor((now.getTime() - start.getTime()) / 86_400_000);
    return `${days} jour${days > 1 ? 's' : ''}`;
  }
  const years = Math.floor(months / 12);
  const rest = months % 12;
  if (years === 0) return `${rest} mois`;
  return `${years} an${years > 1 ? 's' : ''}${rest ? ` ${rest} mois` : ''}`;
}