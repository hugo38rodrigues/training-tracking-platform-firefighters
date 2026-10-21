package domaine.entity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Agent {
    private Integer id;
    private String lastName;
    private String firstName;
    private int matricule;
    private Rank rank;
    private LocalDate rankStartDate;
    private List<License> licenses = new ArrayList<License>();
    private List<Skill> skills = new ArrayList<Skill>();
    private boolean isActive = true;
    private boolean isDifferentiated = false;

    public Agent(String firstName, String lastName,int matricule, Rank rank, LocalDate rankStarDate,List<License> licenses, List<Skill> skills){
        this.firstName = firstName;
        this.lastName = lastName;
        this.matricule = matricule;
        this.rank = rank;
        this.rankStartDate = rankStarDate;
        this.licenses = licenses;
        this.skills = skills;

    }

    public boolean canRegistrationTraining(Training training){
        boolean hasSkill = this.getSkills().equals(training.getSkillsNeeded());
        return hasSkill;
    }

    public void archive(){
        this.isActive = !this.isActive; 
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }
    
    public List<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }
    
    public int getMatricule() {
        return matricule;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public void setRank(Rank rank) {
        this.rank = rank;
    }
    public LocalDate getRankStartDate() {
        return rankStartDate;
    }
    
    public void setRankStartDate(LocalDate rankStartDate) {
        this.rankStartDate = rankStartDate;
    }
    
    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public boolean isDifferentiated() {
        return isDifferentiated;
    }
    
    public void setDifferentiated(boolean isDifferentiated) {
        this.isDifferentiated = isDifferentiated;
    }
  
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Agent [id=" + id + ", lastName=" + lastName + ", firstName=" + firstName + ", matricule=" + matricule
                + ", rank=" + rank + ", rankStartDate=" + rankStartDate + ", licenses=" + licenses + ", skills="
                + skills + ", isActive=" + isActive + ", isDifferentiated=" + isDifferentiated + "]";
    }

}
