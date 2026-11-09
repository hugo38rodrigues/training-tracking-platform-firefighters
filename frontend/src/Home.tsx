import { Outlet } from 'react-router';
import { Navigation } from './Navigations';


export const Home = () => (
  <div className="flex max-h-full max-w-full">
    <div className='max-h-screen'> 
			<Navigation />
		</div>
    <div className="min-w-0 flex-1 px-10 py-9">
      <Outlet />
    </div>
  </div>
);