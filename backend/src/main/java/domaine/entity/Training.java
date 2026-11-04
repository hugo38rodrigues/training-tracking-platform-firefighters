package domaine.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Training {
    private Integer id;
    private String name;
    private int formationNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Skill> skillsNeeded = new ArrayList<Skill>();
    private List<Skill> skillDelivered = new ArrayList<Skill>();
    private boolean hasDifferentiation;


    public Training(String name, int formationNumber, LocalDate startDate, LocalDate endDate, List<Skill> skillsNeeded,
            List<Skill> skillDelivered, boolean hasDifferentiation) {
        this.name = name;
        this.formationNumber = formationNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.skillsNeeded = skillsNeeded;
        this.skillDelivered = skillDelivered;
        this.hasDifferentiation = hasDifferentiation;
    }

    public Integer getId() {
        return id;
    }

    public int getFormationNumber() {
        return formationNumber;
    }

    public void setFormationNumber(int formationNumber) {
        this.formationNumber = formationNumber;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    @Override
    public String toString() {
        return "Training [id=" + id + ", name=" + name + ", startDate=" + startDate + ", endDate=" + endDate
                + ", skillsNeeded=" + skillsNeeded + ", skillDelivered=" + skillDelivered + ", hasDifferentiation="
                + hasDifferentiation + "]";
    }

    public String getName() {
        return name;
    }
    
    public List<Skill> getSkillDelivered() {
        return skillDelivered;
    }
    public void setSkillDelivered(List<Skill> skillDelivered) {
        this.skillDelivered = skillDelivered;
    }
    
    public List<Skill> getSkillsNeeded() {
        return skillsNeeded;
    }
    public void setSkillsNeeded(List<Skill> skillsNeeded) {
        this.skillsNeeded = skillsNeeded;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public boolean hasDifferentiation () {
        return hasDifferentiation;
    }

}
