package usecase.DTO;
import java.time.LocalDate;
import java.util.List;

import domaine.entity.Agent;
import domaine.entity.License;
import domaine.entity.Rank;
import domaine.entity.Skill;


public record DashboardAgentProfil (
    Integer id,
    String lastName,
    String firstName,
    int matricule,
    Rank rank,
    LocalDate rankStartDate,
    List<License> licenses,
    List<String> skills,
    boolean isActive,
    boolean isDifferentiated,
    List<DashBoardRegistration> registration
){

        public static DashboardAgentProfil from (Agent agent,List<DashBoardRegistration> registration ){
            List<String> skillsName = agent.getSkills().stream().map(Skill::getName).toList();
            return new DashboardAgentProfil(
                agent.getId(), 
                agent.getLastName(), 
                agent.getFirstName(), 
                agent.getMatricule(), 
                agent.getRank(),
                agent.getRankStartDate(),
                agent.getLicenses(), 
                skillsName,
                agent.isActive(),
                agent.isDifferentiated(),
                registration


            );
        }
    }

   
