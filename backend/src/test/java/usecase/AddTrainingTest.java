package usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import data.TrainingData;
import domaine.entity.Training;
import usecase.data.TrainingDataUsecase;
import usecase.exceptions.InvalidTrainingNumberExecption;
import usecase.mock.FakeSkillRepository;
import usecase.mock.FakeTrainingRepository;

public class AddTrainingTest {
    private FakeTrainingRepository fakeTrainingRepo;
    private FakeSkillRepository fakeSkillRepo;
    private AddTraining addTraining;

    @BeforeEach
    void setUp() {
        fakeTrainingRepo = new FakeTrainingRepository();
        fakeSkillRepo = new FakeSkillRepository();
        addTraining = new AddTraining(fakeTrainingRepo, fakeSkillRepo);
    }

    @Test
    @DisplayName("Lève une erreur si la formation existe déjà")
    void formation_existante() {
        TrainingDataUsecase.insertTrainingInRepo(TrainingData.trainingCE(), fakeTrainingRepo);
        InvalidTrainingNumberExecption e = assertThrows(InvalidTrainingNumberExecption.class,
                () -> addTraining.execute(TrainingData.trainingCE()));
        assertEquals("Cette formation existe déjà: " + TrainingData.trainingCE().getTrainingNumber(), e.getMessage());

    }

    @Test
    @DisplayName("Ajoute une formation qui n'existe pas encore")
    void formation_non_existante() {
        Training training = TrainingData.trainingCE();

        addTraining.execute(training);

        Training saved = fakeTrainingRepo.findByTrainingNumber(training.getTrainingNumber());
        assertNotNull(saved);
        assertEquals(training.getName(), saved.getName());
    }
}