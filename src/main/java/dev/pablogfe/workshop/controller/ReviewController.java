package dev.pablogfe.workshop.controller;

import dev.pablogfe.workshop.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ReviewController {

    private ReviewService reviewService;

    @PostMapping("review")
    public String reviewPullRequest(@RequestParam String pullrequest, @RequestParam String project) {
        return reviewService.review(pullrequest, project);
    }
}
