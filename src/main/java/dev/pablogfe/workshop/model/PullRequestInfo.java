package dev.pablogfe.workshop.model;

public record PullRequestInfo(int number, String title, String authorLogin, String state) { }
