package data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domaine.entity.Skill;
import domaine.entity.Training;

public class TrainingData {

    public static Training trainingCE() {
        return new Training("Formation chef d'équipe", 1,
                LocalDate.now().plusDays(4), LocalDate.now().plusDays(8),
                SkillData.skillsforCE(), new ArrayList<>(List.of(new Skill("CE INC"))), false);
    }

    public static Training trainingWithStartDateInPast() {
        return new Training("Formation chef d'équipe", 1,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(5),
                SkillData.skillsforCE(), new ArrayList<>(List.of(new Skill("CE INC"))), false);
    }
}