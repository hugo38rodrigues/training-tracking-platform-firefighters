package api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import api.controllers.DTO.SetRegistationRequest;
import usecase.registration.SetRegistrationAgentTraining;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final SetRegistrationAgentTraining setRegistration;

    public RegistrationController(SetRegistrationAgentTraining setRegistration) {
        this.setRegistration = setRegistration;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void registrationAgent(@RequestBody SetRegistationRequest params) {
        setRegistration.execute(params.matricule(), params.trainingNumber());
    }
    

    
}
