package support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

/**
 * Created by vidudaya on 15/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    private String id; // id to uniquely identify the comment
    private List<Comment> comments;
    private Set<Rank> ranks;
    private String comment;

    public Comment() {
    }

    public Comment(String comment, Node node) {
        this.comment = comment;
        Random rand = new Random();
        String commentId = comment.hashCode() + rand.nextInt(200) + node.getNodeIdentifier(); // change this to be more unique with timestamp or lamport timestamp
        this.id = commentId;

        comments = new ArrayList<Comment>();
        ranks = new HashSet<Rank>();
    }

    public Comment getCommentWithId(String id) {
        if (this.id.equals(id)) {
            return this;
        }
        for (Comment comment : comments) {
            return comment.getCommentWithId(id);
        }
        return null;
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

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public double getRank() {
        double rankAvg = 0;
        for (Rank rank : ranks) {
            rankAvg += rank.getRank();
        }
        return rankAvg / ranks.size(); //  to first decimal point
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        return id.equals(comment.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
