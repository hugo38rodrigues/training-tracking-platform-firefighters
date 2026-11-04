package usecase.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import domaine.entity.Training;
import domaine.repository.TrainingRepository;

public class FakeTrainingRepository implements TrainingRepository {
    private HashMap<Integer, Training> repo = new HashMap<>();

    @Override
    public void addTraining(Training training) {
        repo.put(training.getTrainingNumber(), training);
    }

    @Override
    public boolean existsByTrainingNumber(int trainingNumber) {
        return repo.values().stream().anyMatch(training -> training.getTrainingNumber() == trainingNumber);
    }

    @Override 
    public Training findByTrainingNumber(int trainingFormation){
        return repo.get(trainingFormation);
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(repo.values());
    }

}
