package usecase.DTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domaine.entity.RegistrationAgentInTraining;
import domaine.entity.RegistrationStatus;
import domaine.entity.Skill;
import domaine.entity.Training;

public record DashBoardRegistration (
    int registrationId,
    int trianingId,
    LocalDate startRegistration,
    LocalDate endRegistration,
    RegistrationStatus status, 
    int registrationAgentId,
    int registrationTrainingId,
    String name,
    int formationNumber,
    LocalDate startDate,
    LocalDate endDate,
    List<Skill> skillsNeeded,
    List<Skill> skillDelivered, 
    boolean hasDifferentiation

){
    public static DashBoardRegistration from(RegistrationAgentInTraining registration, Training training){
        return new DashBoardRegistration(
            registration.getId(),
            training.getId(),
            registration.getStartRegistration(),
            registration.getEndRegistration(),
            registration.getStatut(),
            registration.getIdAgent(),
            registration.getIdTraining(),
            training.getName(),
            training.getFormationNumber(),
            training.getStartDate(),
            training.getEndDate(),
            training.getSkillsNeeded(),
            training.getSkillDelivered(),
            training.hasDifferentiation()
        );
    }
}
