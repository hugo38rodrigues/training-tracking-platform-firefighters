package domaine.entity;

public enum Rank {
    SAPEUR_DEUXIEME_CLASSE(1),
    SAPEUR_PREMIERE_CLASSE(2),
    CAPORAL(3),
    CAPORAL_CHEF(4),
    SERGENT(5),
    SERGENT_CHEF(6),
    ADJUDANT(7),
    ADJUDANT_CHEF(8),
    LIEUTENANT(9),
    CAPITAINE(10),
    COMMANDANT(11),
    LIEUTENANT_COLONEL(12),
    COLONEL(13),
    CONTROLEUR_GENERAL(14);

    private final int valeur;

    Rank(int valeur) {              
        this.valeur = valeur;
    }

    public boolean isAtLeast(Rank otherRank) { 
        return valeur >= otherRank.valeur;       
    }
}