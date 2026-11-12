package api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import api.controllers.DTO.CreateAgentRequest;
import usecase.DTO.DashboardAgentProfil;
import usecase.AddAgent;
import usecase.GetAllAgent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final GetAllAgent getAllAgent;
    private final AddAgent addAgent;

    public AgentController(GetAllAgent getAllAgent, AddAgent addAgent) {
        this.getAllAgent = getAllAgent;
        this.addAgent = addAgent;
    }

    @GetMapping
    public List<DashboardAgentProfil> findAll() {
        return getAllAgent.execute();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody CreateAgentRequest request) {
        addAgent.execute(request.toAgent());
    }
    
    

    
}