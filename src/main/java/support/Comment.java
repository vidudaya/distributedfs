package support;

import java.util.*;

/**
 * Created by vidudaya on 15/11/17.
 */
public class Comment {
    private String id; // id to uniquely identify the comment
    private List<Comment> comments;
    private Set<Rank> ranks;
    private String comment;

    public Comment(String comment, Node node) {
        this.comment = comment;
        Random rand = new Random();
        String commentId = comment.hashCode() + rand.nextInt(200) + node.getNodeIdentifier(); // change this to be more unique with timestamp or lamport timestamp
        this.id = commentId;

        comments = new ArrayList<Comment>();
        ranks = new HashSet<Rank>();
    }

    public void addRank(Integer rank, String userName) {
        this.ranks.add(new Rank(rank, userName));
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public Set<Rank> getRanks() {
        return ranks;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
