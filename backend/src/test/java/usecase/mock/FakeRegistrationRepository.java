package usecase.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import domaine.entity.RegistrationAgentInTraining;
import domaine.repository.RegistrationAgentInTrainingRepository;

public class FakeRegistrationRepository implements RegistrationAgentInTrainingRepository {
    private final Map<Integer, RegistrationAgentInTraining> storage = new HashMap<>();

    @Override
    public RegistrationAgentInTraining findById(int id) {
        return storage.get(id);
    }

    @Override
    public void update(RegistrationAgentInTraining registration) {
        storage.put(registration.getId(), registration);
    }

    @Override
    public void save(RegistrationAgentInTraining registration) {
        storage.put(registration.getId(), registration);
    }

    @Override
    public List<RegistrationAgentInTraining> findByAgent(int matricule) {
        return storage.values().stream()
                .filter(r -> r.getAgentMatricule() == matricule)
                .toList();
    }

    @Override
    public List<RegistrationAgentInTraining> findByTraining(int trainingId) {
        return storage.values().stream()
                .filter(r -> r.getIdTraining() == trainingId)
                .toList();
    }
}