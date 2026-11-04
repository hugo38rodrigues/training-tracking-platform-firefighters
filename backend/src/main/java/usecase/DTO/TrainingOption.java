package usecase.DTO;

import java.time.LocalDate;
import java.util.List;

import domaine.entity.Skill;
import domaine.entity.Training;

public record TrainingOption(int id, String name, LocalDate startDate, LocalDate endDate, int formationNumber, List<Skill> skillsNeeded, List<Skill> skillDelivered, boolean hasDifferentiation) {

    public static TrainingOption from(Training training) {
        return new TrainingOption(
            training.getId(),
            training.getName(),
            training.getStartDate(),
            training.getEndDate(),
            training.getFormationNumber(),
            training.getSkillsNeeded(),
            training.getSkillDelivered(),
            training.hasDifferentiation()
        );
    }
}