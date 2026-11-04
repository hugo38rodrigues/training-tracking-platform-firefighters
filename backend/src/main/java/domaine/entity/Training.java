package domaine.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Training {
    private String name;
    private int trainingNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Skill> skillsNeeded = new ArrayList<Skill>();
    private List<Skill> skillDelivered = new ArrayList<Skill>();
    private boolean hasDifferentiation;

    public Training(String name, int trainingNumber, LocalDate startDate, LocalDate endDate, List<Skill> skillsNeeded,
            List<Skill> skillDelivered, boolean hasDifferentiation) {
        this.name = name;
        this.trainingNumber = trainingNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.skillsNeeded = skillsNeeded;
        this.skillDelivered = skillDelivered;
        this.hasDifferentiation = hasDifferentiation;
    }

    public int getTrainingNumber() {
        return trainingNumber;
    }

    public void setTrainingNumber(int trainingNumber) {
        this.trainingNumber = trainingNumber;
    }

    public LocalDate getEndDate() {
        return endDate;
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

    public boolean hasDifferentiation() {
        return hasDifferentiation;
    }

    @Override
    public String toString() {
        return "Training [name=" + name + ", formationNumber=" + trainingNumber + ", startDate=" + startDate
                + ", endDate=" + endDate + ", skillsNeeded=" + skillsNeeded + ", skillDelivered=" + skillDelivered
                + ", hasDifferentiation=" + hasDifferentiation + "]";
    }

}
