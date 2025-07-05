package dev.pablogfe.workshop.tool;

import dev.pablogfe.workshop.model.*;
import lombok.AllArgsConstructor;
import org.kohsuke.github.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class GitHubTools {

    private GitHub github;

    @Tool(description = "Recover GitHub repository")
    public RepositoryInfo getGitHubRepository(
            @ToolParam(description = "Repository name 'owner/name'") String repository) throws IOException {

        GHRepository repo = github.getRepository(repository);
        return new RepositoryInfo(repo.getName(), repo.getFullName(), repo.getDefaultBranch());
    }

    @Tool(description = "Recover GitHub pullrequest")
    public PullRequestInfo getPullRequest(
            @ToolParam(description = "Repository name 'owner/name'") String repository,
            @ToolParam(description = "PullRequest number") int number) throws IOException {

        GHPullRequest pr = github.getRepository(repository).getPullRequest(number);
        return new PullRequestInfo(pr.getNumber(), pr.getTitle(), pr.getUser().getLogin(), pr.getState().toString());
    }

    @Tool(description = "Post a comment on a GitHub pull request")
    public String postPullRequestComment(
            @ToolParam(description = "Repository name 'owner/name'") String repository,
            @ToolParam(description = "Pull request number") int prNumber,
            @ToolParam(description = "Comment body") String commentBody) throws IOException {

        GHPullRequest pr = github.getRepository(repository).getPullRequest(prNumber);
        pr.comment(commentBody);
        return "Comment posted successfully.";
    }

    @Tool(description = "Get the list of file diffs in a pull request")
    public List<FileDiffInfo> getPullRequestDiffFiles(
            @ToolParam(description = "Repository name 'owner/name'") String repository,
            @ToolParam(description = "Pull request number") int prNumber) throws IOException {

        return github.getRepository(repository).getPullRequest(prNumber).listFiles().toList().stream()
                .map(file -> new FileDiffInfo(file.getFilename(), file.getStatus(), file.getPatch()))
                .toList();
    }

    @Tool(description = "List all branches in a GitHub repository.")
    public List<BranchInfo> listRepositoryBranches(
            @ToolParam(description = "Repository name 'owner/name'") String repository) throws IOException {

        return github.getRepository(repository).getBranches().keySet().stream()
                .map(BranchInfo::new)
                .toList();
    }

    @Tool(description = "Compare two branches and get list of changed files.")
    public List<FileDiffInfo> compareBranches(
            @ToolParam(description = "Repository name 'owner/name'") String repository,
            @ToolParam(description = "Base branch name") String baseBranch,
            @ToolParam(description = "Head branch name") String headBranch) throws IOException {

        return Arrays.stream(github.getRepository(repository).getCompare(baseBranch, headBranch).getFiles())
                .map(file -> new FileDiffInfo(file.getFileName(), file.getStatus(), file.getPatch()))
                .toList();
    }

    @Tool(description = "List commits between two branches.")
    public List<CommitInfo> listCommitsBetweenBranches(
            @ToolParam(description = "Repository name 'owner/name'") String repository,
            @ToolParam(description = "Base branch name") String baseBranch,
            @ToolParam(description = "Head branch name") String headBranch) throws IOException {

        return Arrays.stream(github.getRepository(repository).getCompare(baseBranch, headBranch).getCommits())
                .map(commit -> {
                    try {
                        return new CommitInfo(commit.getSHA1(), commit.getCommitShortInfo().getMessage(),
                                commit.getAuthor() != null ? commit.getAuthor().getLogin() : "unknown");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }

    @Tool(description = "Read the content of a file from a specific branch.")
    public String getFileContentFromBranch(
            @ToolParam(description = "Repository name 'owner/name'") String repository,
            @ToolParam(description = "Branch name") String branch,
            @ToolParam(description = "File path relative to repo root") String filePath) throws IOException {

        GHContent content = github.getRepository(repository).getFileContent(filePath, branch);
        return new String(content.read().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Tool(description = "List all file paths in a specific branch.")
    public List<String> getAllFilePathsFromBranch(
            @ToolParam(description = "Repository name 'owner/name'") String repository,
            @ToolParam(description = "Branch name") String branch) throws IOException {

        List<String> filePaths = new ArrayList<>();
        collectFilePaths(github.getRepository(repository), "/", branch, filePaths);
        return filePaths;
    }

    private void collectFilePaths(GHRepository repository, String path, String branch, List<String> filePaths) throws IOException {
        for (GHContent content : repository.getDirectoryContent(path, branch)) {
            if (content.isFile()) {
                filePaths.add(content.getPath());
            } else if (content.isDirectory()) {
                collectFilePaths(repository, content.getPath(), branch, filePaths);
            }
        }
    }
}