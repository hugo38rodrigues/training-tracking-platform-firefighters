package usecase.registration;
import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;
import domaine.repository.RegistrationAgentInTrainingRepository;
import usecase.exceptions.InvalidStatusException;

public class FailedTraining {
    private RegistrationAgentInTrainingRepository registrationRepo;

    public FailedTraining(RegistrationAgentInTrainingRepository registrationRepo) {
        this.registrationRepo = registrationRepo;
    }

    public void execute(RegistrationAgentInTraining registration){
        RegistrationAgentInTraining regi = registrationRepo.findById(registration.getId());
        boolean isValid = regi.getStatut().equals(RegistrationStatus.DONE)|| 
        regi.getStatut().equals(RegistrationStatus.CANCEL) || 
        regi.getStatut().equals(RegistrationStatus.PENDING);

        if (isValid){
            throw new InvalidStatusException("Cette inscription ne peut pas être échouer");
        }
        
        regi.setStatut(RegistrationStatus.FAILED);
        registrationRepo.update(regi);
       
        
    }
}
