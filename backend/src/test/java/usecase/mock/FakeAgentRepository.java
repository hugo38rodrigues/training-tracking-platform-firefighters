package usecase.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domaine.entity.Agent;
import domaine.repository.AgentRepository;

public class FakeAgentRepository implements AgentRepository {
    private final Map<Integer, Agent> storage = new HashMap<>();

    @Override
    public void add(Agent agent) {
        storage.put(agent.getId(), agent);
    }

    @Override
    public boolean existsByMatricule(int matricule) {
        return storage.values().stream()
                .anyMatch(agent -> agent.getMatricule() == matricule);
    }

    @Override
    public Agent findAgent(Integer id) {
        return storage.get(id);
    }

    @Override
    public List<Agent> findAllAgent() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Agent agent) {
        storage.put(agent.getId(), agent);
    }
    

}
