package ui;

import java.time.LocalDate;
import java.time.Period;

import domaine.entity.License;
import domaine.entity.Rank;

public final class DisplayNames {

    private DisplayNames() {} // classe utilitaire : on ne crée jamais d'objet DisplayNames

    public static String rank(Rank rank) {
        return switch (rank) {
            case SAPEUR_DEUXIEME_CLASSE -> "Sapeur 2eme classe";
            case SAPEUR_PREMIERE_CLASSE -> "Sapeur 1re classe";
            case CAPORAL -> "Caporal";
            case CAPORAL_CHEF -> "Caporal-chef";
            case SERGENT -> "Sergent";
            case SERGENT_CHEF -> "Sergent-chef";
            case ADJUDANT -> "Adjudant";
            case ADJUDANT_CHEF -> "Adjudant-chef";
            case LIEUTENANT -> "Lieutenant";
            case CAPITAINE -> "Capitaine";
            case COMMANDANT -> "Commandant";
            case LIEUTENANT_COLONEL -> "Lieutenant-colonel";
            case COLONEL -> "Colonel";
            default -> throw new IllegalArgumentException("Unexpected value: " + rank);
        };
    }

    public static String license(License license) {
        return switch (license) {
            case PERMIS_B -> "Permis B";
            case PERMIS_C -> "Permis C";
        };
    }

    /** "13 jours", "7 mois", "4 ans 7 mois"... */
    public static String seniority(LocalDate since) {
        Period period = Period.between(since, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();
        if (years > 0) {
            String result = years + (years > 1 ? " ans" : " an");
            return months > 0 ? result + " " + months + " mois" : result;
        }
        if (months > 0) {
            return months + " mois";
        }
        int days = period.getDays();
        return days + (days > 1 ? " jours" : " jour");
    }
} 
    
