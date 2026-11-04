package usecase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.TrainingData;
import usecase.data.TrainingDataUsecase;
import usecase.exceptions.InvalidTrainingNumberExecption;
import usecase.mock.FakeSkillRepository;
import usecase.mock.FakeTrainingRepository;

public class AddTrainingTest {
    private FakeTrainingRepository fakeTrainingRepo;
    private FakeSkillRepository fakeSkillRepo;
    private AddTraining addTraining;

    @BeforeEach 
    void setUp(){
        fakeTrainingRepo = new  FakeTrainingRepository();
        fakeSkillRepo = new  FakeSkillRepository();
        addTraining = new AddTraining(fakeTrainingRepo, fakeSkillRepo);
    }

    @Test 
    @DisplayName ("Lève une erreur si la formation existe déjà")
    void formation_existante(){
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(), 1, fakeTrainingRepo);
        InvalidTrainingNumberExecption e = assertThrows(InvalidTrainingNumberExecption.class,
            () -> addTraining.execute(TrainingData.trainingCE()));
        assertEquals("Cette formation existe déjà: " + TrainingData.trainingCE().getFormationNumber(), e.getMessage());

    }

    @Test 
    @DisplayName ("Insert une formation dans la base de donnée")
    void formation_non_existante(){

        addTraining.execute(TrainingData.trainingCE());
        assertTrue(fakeTrainingRepo.findTrainingByFormationNumber(TrainingData.trainingCE().getFormationNumber()));
    }
}