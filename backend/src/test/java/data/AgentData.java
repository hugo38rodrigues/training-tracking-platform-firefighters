package data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domaine.entity.Agent;
import domaine.entity.License;
import domaine.entity.Rank;

public class AgentData {

    public static Agent exitantAgent() {
        return new Agent("Test", "Toto", 23659, Rank.CAPORAL, LocalDate.now(),
                licenses(License.PERMIS_B), SkillData.skillsCE());
    }

    public static Agent newAgent() {
        return new Agent("Test", "Toto", 23660, Rank.SAPEUR_PREMIERE_CLASSE, LocalDate.parse("2022-03-26"),
                licenses(License.PERMIS_C), SkillData.skillsCE());
    }

    public static Agent agentWithoutSkillForCE() {
        return new Agent("Test", "Toto", 23660, Rank.SAPEUR_PREMIERE_CLASSE, LocalDate.parse("2022-03-26"),
                licenses(License.PERMIS_C), SkillData.skillsNotForCE());
    }

    public static Agent agentCE() {
        return new Agent("Test", "Toto", 23660, Rank.SAPEUR_PREMIERE_CLASSE, LocalDate.parse("2022-03-26"),
                licenses(License.PERMIS_C), SkillData.skillsCE());
    }

    public static Agent agentWithSkillForCE() {
        return new Agent("Test", "Toto", 23660, Rank.SAPEUR_PREMIERE_CLASSE, LocalDate.parse("2022-03-26"),
                licenses(License.PERMIS_C), SkillData.skillsforCE());
    }

    public static Agent agentWithMatriculeExistant() {
        return new Agent("Test", "Toto", 23659, Rank.CAPORAL, LocalDate.now(),
                licenses(License.PERMIS_B), SkillData.skillsCE());
    }

    public static Agent agentWithMatriculeToBig() {
        return new Agent("Test", "Toto", 2365900, Rank.CAPORAL, LocalDate.now(),
                licenses(License.PERMIS_B), SkillData.skillsCE());
    }

    private static List<License> licenses(License... licenses) {
        return new ArrayList<>(List.of(licenses));
    }
}