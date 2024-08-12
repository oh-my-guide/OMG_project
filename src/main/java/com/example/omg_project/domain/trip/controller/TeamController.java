package com.example.omg_project.domain.trip.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/team")
public class TeamController {
    // 팀에 가입하는 폼
    @GetMapping("/join")
    public String showJoinTeamForm() {
        return "team/join";
    }

    @GetMapping("/myteam")
    public String showTeamsPage() {
        return "team/myteam";
    }
}

