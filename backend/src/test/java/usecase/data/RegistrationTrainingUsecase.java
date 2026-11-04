package usecase.data;
import domaine.entity.RegistrationAgentInTraining;
import domaine.repository.RegistrationAgentInTrainingRepository;

public class RegistrationTrainingUsecase {
    public static int insertRegistrationInRepo(RegistrationAgentInTraining reg, int id, RegistrationAgentInTrainingRepository registrationRepo){
        reg.setId(id);
        registrationRepo.save(reg);
        return reg.getId();
    }
}
