import { useEffect, useState } from "react"
import { getAgents, type DashboardAgentProfil } from "../api/Request"
import { AgentCard } from "./components/Card";


export const ViewAgent = () => {
	const [agents, setAgents] = useState<DashboardAgentProfil[]>([]);
	const [error, setError] = useState<string | null>(null);

	useEffect(() => {
		let ignore = false;

		const load = async () => {
			try {
				const data = await getAgents();
				console.log(data)
				if (!ignore) setAgents(data);
			} catch (e) {
				console.error(e);
				if (!ignore) setError('Impossible de charger les agents.');
			}
		}

		load();
		return () => { ignore = true; };
	}, []);
	
	return(
		<div id="containe-agent" className="flex flex-wrap max-w-4xl max-h-screen ml-96 bg-amber-600">
			<label>Coucou</label>
			{error && <p className="text-red-700">{error}</p>}
			{agents.map((agent) => 
				
				<AgentCard agent={agent} key={agent.matricule}/>
			)}
		</div>
	)
}