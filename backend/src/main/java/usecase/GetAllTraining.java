package usecase;

import java.util.List;

import domaine.entity.Training;
import domaine.repository.TrainingRepository;

public class GetAllTraining {
    private TrainingRepository trainingRepo;

    public GetAllTraining(TrainingRepository trainingRepo) {
        this.trainingRepo = trainingRepo;
    }

    public List<Training> execute(){
        return trainingRepo.findAll();
    }
    
}
