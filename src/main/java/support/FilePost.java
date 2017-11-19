package support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vidudaya on 15/11/17.
 */
public class FilePost {
    private String id; // id to uniquely identify the FilePost
    private List<Comment> comments;
    private Set<Rank> ranks;
    private String fileName;

    public FilePost(String fileName, Node node) {
        this.fileName = fileName;
        String fileId = String.valueOf(fileName.hashCode()); // change this to be more unique with timestamp or lamport timestamp
        this.id = fileId;

        comments = new ArrayList<Comment>();
        ranks = new HashSet<Rank>();
    }

    public static String displayPost(FilePost post) {
        StringBuilder show = new StringBuilder();
        show.append("[" + post.getId() + "]" + post.getFileName()).append("[" + post.getRank() + "]").append("\n");
        addCommentsToShow(post.getComments(), show, 1);

        return show.toString();
    }

    public static void addCommentsToShow(List<Comment> coms, StringBuilder show, int indent) {
        String gap = "";
        for (int i = 0; i < indent; i++) {
            gap += "\t";
        }
        for (Comment com : coms) {
            show.append(gap).append("[" + com.getId() + "]" + com.getComment() + "[" + com.getRank() + "]" + "\n");
            if (!com.getComments().isEmpty()) {
                addCommentsToShow(com.getComments(), show, indent + 1);
            }
        }
    }

    public void addRank(Integer rank, String userName) {
        this.ranks.add(new Rank(rank, userName));
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public Set<Rank> getRanks() {
        return ranks;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return displayPost(this);
    }

    public double getRank() {
        double rankAvg = 0;
        for (Rank rank : ranks) {
            rankAvg += rank.getRank();
        }
        return rankAvg / ranks.size(); //  to first decimal point
    }
}
