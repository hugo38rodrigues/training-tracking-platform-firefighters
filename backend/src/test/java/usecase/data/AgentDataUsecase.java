package usecase.data;

import domaine.entity.Agent;
import usecase.mock.FakeAgentRepository;

public class AgentDataUsecase {
    public static int insertAgentInRepo (Agent agent, int id, FakeAgentRepository fakeAgentRepo){
        agent.setId(id);
        fakeAgentRepo.add(agent);
        return agent.getId();
    }
}
