package support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;

/**
 * Created by vidudaya on 15/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilePost {
    private String id; // id to uniquely identify the FilePost
    private List<Comment> comments;
    private Set<Rank> ranks;
    private String fileName;

    public FilePost(String fileName) {
        this.fileName = fileName;
        String fileId = String.valueOf(Math.abs(fileName.hashCode())); // change this to be more unique with timestamp or lamport timestamp
        this.id = fileId;

        comments = new ArrayList<Comment>();
        ranks = new HashSet<Rank>();
    }

    public FilePost() {
    }

    public static String displayPost(FilePost post) {
        StringBuilder show = new StringBuilder();
        show.append("[" + post.getId() + "]" + post.getFileName()).append("[" + post.getRank() + "]").append("\n");
        addCommentsToShow(post.getComments(), show, 1);

        return show.toString();
    }

    public static void addCommentsToShow(List<Comment> coms, StringBuilder show, int indent) {
        // sort the coms list
        Collections.sort(coms, new Comparator<Comment>() {
            public int compare(Comment o1, Comment o2) {
                return o1.getCommentTimestamp() - o2.getCommentTimestamp();
            }
        });

        String gap = "";
        for (int i = 0; i < indent; i++) {
            gap += "\t";
        }
        for (Comment com : coms) {
            //show.append(gap).append("[" + com.getId() + "]" + com.getComment() + "[" + com.getRank() + "]" + "\n");
            show.append(gap).append("[" + com.getId() + "]" + com.getComment() + "[" + com.getRank() + "] " +com.getCommentTimestamp()+ "\n");
            if (!com.getComments().isEmpty()) {
                addCommentsToShow(com.getComments(), show, indent + 1);
            }
        }
    }

    public void addRank(Integer rank, String userName, Integer timestamp) {
        this.ranks.add(new Rank(rank, userName, timestamp));
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public Set<Rank> getRanks() {
        return ranks;
    }

    public void setRanks(Set<Rank> ranks) {
        this.ranks = ranks;
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
