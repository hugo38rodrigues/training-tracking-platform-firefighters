package domaine.entity;

import java.time.LocalDate;


public class RegistrationAgentInTraining {
    private int id;
    private LocalDate startRegistration ;
    private LocalDate endRegistration;
    private RegistrationStatus statut; 
    private int idAgent;
    private int idTraining;


    public RegistrationAgentInTraining(LocalDate startRegistration, LocalDate endRegistration, RegistrationStatus status,
            int idAgent, int idTraining) {
        this.startRegistration = startRegistration;
        this.endRegistration = endRegistration;
        this.statut = status;
        this.idAgent = idAgent;
        this.idTraining = idTraining;
    }

    public RegistrationStatus computeStatus(LocalDate today, Training training){

        if (this.getStatut() == RegistrationStatus.CANCEL || this.getStatut() == RegistrationStatus.FAILED){
            return this.getStatut();
        }

        if(today.isBefore(training.getStartDate())){
            return RegistrationStatus.PENDING;
        }

        if(today.isBefore(training.getEndDate())){
            return RegistrationStatus.IN_PROGRESS;
        }
        
        return RegistrationStatus.DONE;

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartRegistration() {
        return startRegistration;
    }

    public void setStartRegistration(LocalDate startRegistration) {
        this.startRegistration = startRegistration;
    }

    public LocalDate getEndRegistration() {
        return endRegistration;
    }

    public void setEndRegistration(LocalDate endRegistration) {
        this.endRegistration = endRegistration;
    }

    public RegistrationStatus getStatut() {
        return statut;
    }

    public void setStatut(RegistrationStatus statut) {
        this.statut = statut;
    }

    public int getIdAgent() {
        return idAgent;
    }

    public void setIdAgent(int idAgent) {
        this.idAgent = idAgent;
    }

    public int getIdTraining() {
        return idTraining;
    }

    public void setIdTraining(int idTraining) {
        this.idTraining = idTraining;
    }

    @Override
    public String toString() {
        return "RegistrationAgentInTraining [id=" + id + ", startInscription=" + startRegistration + ", endInscription="
                + endRegistration + ", statut=" + statut + ", idAgent=" + idAgent + ", idTraining=" + idTraining + "]";
    }

}
