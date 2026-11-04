package usecase.registration;

import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;
import domaine.repository.RegistrationAgentInTrainingRepository;
import usecase.exceptions.InvalidStatusException;

public class CancelTraining {
    private RegistrationAgentInTrainingRepository registrationRepo;

    public CancelTraining(RegistrationAgentInTrainingRepository registrationRepo) {
        this.registrationRepo = registrationRepo;
    }

    public void execute(RegistrationAgentInTraining registration){
        RegistrationAgentInTraining regi = registrationRepo.findById(registration.getId());
        boolean registrationFinish = regi.getStatut().equals(RegistrationStatus.DONE)|| regi.getStatut().equals(RegistrationStatus.CANCEL) || regi.getStatut().equals(RegistrationStatus.FAILED);
        boolean registrationIsInProgresse = regi.getStatut().equals(RegistrationStatus.IN_PROGRESS);

        if (registrationFinish){
            throw new InvalidStatusException("Cette inscription est déjà terminé");
        }
        
        if(registrationIsInProgresse){
            throw new InvalidStatusException("Cette inscription est déjà en cour et ne peux pas être annulé");
        }
        
        regi.setStatut(RegistrationStatus.CANCEL);
        registrationRepo.update(regi);
       
        
    }
}
