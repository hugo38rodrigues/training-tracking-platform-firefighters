package domaine.entity;

import java.util.ArrayList;
import java.util.List;

public class RankCondition {
    private int id;
    private Rank rank;
    private Rank previousRank;
    private int requiredYears;
    private List<Skill> requireSkills = new ArrayList<>();;

    public RankCondition(Rank rank, Rank previousRank, int requireYears, List<Skill> requireSkills){
        this.rank = rank;
        this.previousRank = previousRank;
        this.requiredYears = requireYears;
        this.requireSkills = requireSkills;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Rank getRank() {
        return rank;
    }

    public List<Skill> getRequireSkills() {
        return requireSkills;
    }
    
    public void setRequireSkills(List<Skill> requireSkills) {
        this.requireSkills = requireSkills;
    }
    
    public int getRequiredYears() {
        return requiredYears;
    }

    public Rank getPreviousRank() {
        return previousRank;
    }
}
