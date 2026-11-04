package usecase.mock;

import java.util.HashMap;

import domaine.entity.Training;
import domaine.repository.TrainingRepository;

public class FakeTrainingRepository implements TrainingRepository{
    private HashMap<Integer, Training> repo = new HashMap<>();

    @Override
    public void addTraining(Training training) {
        repo.put(training.getId(),training);
    }

    @Override
    public boolean findTrainingByFormationNumber(int formationNumber) {
        return repo.values().stream().anyMatch(training -> training.getFormationNumber() == formationNumber);
    }

    @Override
    public Training findTrainingById(int id) {
        return repo.values().stream().filter(training -> training.getId().equals(id)).findFirst()
                .orElse(null);
    }

    

}
