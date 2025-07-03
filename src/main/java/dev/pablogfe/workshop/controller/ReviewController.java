package dev.pablogfe.workshop.controller;

import dev.pablogfe.workshop.service.ReviewService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ReviewController {

    private ReviewService reviewService;

    @GetMapping("review")
    public String reviewPullRequest(@RequestParam String pullrequest, @RequestParam String project) {
        return reviewService.review(pullrequest, project);
    }
}
