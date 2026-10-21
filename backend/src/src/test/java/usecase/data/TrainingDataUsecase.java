package usecase.data;

import domaine.entity.Training;
import usecase.mock.FakeTrainingRepository;

public class TrainingDataUsecase {
    public static void insertTrainingInRepo (Training training, int id, FakeTrainingRepository fakeTrainingRepo){
        training.setId(id);
        fakeTrainingRepo.addTraining(training);
    }
}
