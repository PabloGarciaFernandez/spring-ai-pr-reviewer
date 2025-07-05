package dev.pablogfe.workshop.controller;

import dev.pablogfe.workshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("review")
    public String reviewPullRequest(@RequestParam String pullrequest, @RequestParam String project) {
        return reviewService.review(pullrequest, project);
    }

    @GetMapping("anwer")
    public String answerPullRequest(@RequestParam String pullrequest, @RequestParam String project) {
        //return reviewService.answerPullRequest(pullrequest, project);
        return "";
    }
}
