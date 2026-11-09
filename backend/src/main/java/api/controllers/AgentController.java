package api.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import usecase.DTO.DashboardAgentProfil;
import usecase.GetAllAgent;


@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final GetAllAgent getAllAgent;

    public AgentController(GetAllAgent getAllAgent) {
        this.getAllAgent = getAllAgent;
    }

    @GetMapping("/")
    public List<DashboardAgentProfil> findAll() {
        return getAllAgent.execute();
    } 
    
}