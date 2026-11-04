package data;

import java.util.ArrayList;
import java.util.List;

import domaine.entity.Skill;

public class SkillData {

    public static List<Skill> skillsforCE() {
        return new ArrayList<>(List.of(
                new Skill("EQ DIV HYM"), new Skill("EQ EA"), new Skill("EQ INC"),
                new Skill("EQ PMA"), new Skill("EQ PPBE"), new Skill("EQ SSUAP")));
    }

    public static List<Skill> skillsCE() {
        List<Skill> skills = skillsforCE();
        skills.add(new Skill("CE INC"));
        return skills;
    }

    public static List<Skill> skillsNotForCE() {
        return new ArrayList<>(List.of(
                new Skill("EQ DIV HYM"), new Skill("EQ EA"), new Skill("EQ INC"),
                new Skill("EQ PMA"), new Skill("EQ PPBE")));
    }
}