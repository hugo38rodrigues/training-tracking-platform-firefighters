package usecase.data;

import domaine.entity.Agent;
import usecase.mock.FakeAgentRepository;

public class AgentDataUsecase {
    public static void insertAgentInRepo (Agent agent, FakeAgentRepository fakeAgentRepo){
        fakeAgentRepo.add(agent);
    }
}
