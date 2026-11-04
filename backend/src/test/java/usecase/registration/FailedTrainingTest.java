package usecase.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;
import usecase.exceptions.InvalidStatusException;
import usecase.mock.FakeRegistrationRepository;


class FailedTrainingTest {
    private FakeRegistrationRepository repo;
    private FailedTraining failedTraining;

    @BeforeEach
    void setUp() {
        repo = new FakeRegistrationRepository();
        failedTraining = new FailedTraining(repo);
    }

    private RegistrationAgentInTraining registrationWithStatus(RegistrationStatus status) {
        RegistrationAgentInTraining r = new RegistrationAgentInTraining(LocalDate.of(2026, 10, 14), LocalDate.of(2026, 10, 21), status, 2, 1);
        r.setId(2);
        repo.save(r);
        return r;
    }

    @ParameterizedTest 
    @EnumSource (value = RegistrationStatus.class, names = {"IN_PROGRESS"})
    void une_inscription_en_cours_peut_échouer(RegistrationStatus status){

        RegistrationAgentInTraining r = registrationWithStatus(status);
        failedTraining.execute(r);
        
        assertEquals(RegistrationStatus.FAILED, repo.findById(2).getStatut());
    }


    @ParameterizedTest
    @EnumSource(value = RegistrationStatus.class, names = { "PENDING" })
    void une_inscription_en_attente_ne_peut_pas_échouer(RegistrationStatus status) {
        RegistrationAgentInTraining r = registrationWithStatus(status);

        assertThrows(InvalidStatusException.class, () -> failedTraining.execute(r));
        assertEquals(status, repo.findById(2).getStatut());
    }
  
    @ParameterizedTest
    @EnumSource(value = RegistrationStatus.class, names = {"DONE", "CANCEL"})
    void une_inscription_terminee_ne_peut_pas_etre_échouer(RegistrationStatus status) {
        RegistrationAgentInTraining r = registrationWithStatus(status);

        assertThrows(InvalidStatusException.class, () -> failedTraining.execute(r));
        assertEquals(status, repo.findById(2).getStatut());
    }
}