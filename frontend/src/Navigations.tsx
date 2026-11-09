import { NavLink } from 'react-router';
import { cn } from './lib/utils';

const links = [
  { to: '/', label: 'Agents', icon: 'M9 11.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z M2.5 20c.6-3.6 3.3-5.5 6.5-5.5s5.9 1.9 6.5 5.5 M16 4.5a3.5 3.5 0 0 1 0 7 M18.5 14.8c1.7.7 2.8 2.4 3 5.2' },
  { to: '/formations', label: 'Formations', icon: 'M2 8l10-5 10 5-10 5z M6 10.5V16c0 1.5 2.7 3 6 3s6-1.5 6-3v-5.5' },
  { to: '/inscriptions', label: 'Inscriptions', icon: 'M6 4h12a2 2 0 0 1 2 2v13a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z M8 2.5v3 M16 2.5v3 M4 9h16 M8.5 14.5l2 2 4-4' },
];

export const Navigation = () => (
  <nav aria-label="Navigation principale"
       className="flex w-60 shrink-0 flex-col gap-8 bg-[#1B2433] px-4 py-7 text-[#E8EBF0]">
    {/* Logo + nom */}
    <div className="flex items-center gap-3 px-2">
      <div aria-hidden="true" className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-primary">
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2"
             strokeLinecap="round" strokeLinejoin="round">
          <path d="M12 3c1 3 4 4.5 4 8.5a4 4 0 0 1-8 0c0-1.6.7-2.8 1.6-3.8.2 1.4 1 2.3 2 2.6C11 8 11.2 5.3 12 3z" />
          <path d="M6 19.5h12" />
        </svg>
      </div>
      <div>
        <p className="text-lg font-bold leading-tight text-white">Suivi des formations</p>
        <p className="text-sm text-[#B6BDC9]">Sapeurs-pompiers</p>
      </div>
    </div>

    {/* Liens */}
    <ul className="flex flex-col gap-1">
      {links.map((link) => (
        <li key={link.to}>
          <NavLink
            to={link.to}
            end={link.to === '/'}
            className={({ isActive }) =>
              cn(
                'flex min-h-11 items-center gap-3 rounded-lg px-3 text-[15px]',
                isActive ? 'bg-[#2A3548] font-semibold text-white' : 'text-[#C9CFD9] hover:bg-[#232E40]'
              )
            }
          >
            {({ isActive }) => (
              <>
                <svg aria-hidden="true" width="20" height="20" viewBox="0 0 24 24" fill="none"
                     stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d={link.icon} />
                </svg>
                <span className="flex-1">{link.label}</span>
                {isActive && <span aria-hidden="true" className="size-2 rounded-full bg-primary" />}
              </>
            )}
          </NavLink>
        </li>
      ))}
    </ul>
  </nav>
);