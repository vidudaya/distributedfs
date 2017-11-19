package support;

/**
 * Created by vidudaya on 15/11/17.
 */
public class Rank {
    private String user;
    private Integer rank; // the rank given by the user
    private Integer rankTimestamp;

    public Rank(Integer rank, String user, Integer timestamp) {
        this.rank = rank;
        this.user = user;
        this.rankTimestamp = timestamp;
    }

    public Rank() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rank rank = (Rank) o;

        return user.equals(rank.user);

    }

    @Override
    public int hashCode() {
        return user.hashCode();
    }

    public Integer getRank() {
        return rank;
    }

    public String getUser() {
        return user;
    }

    public Integer getRankTimestamp() {
        return rankTimestamp;
    }

    public void setRankTimestamp(Integer rankTimestamp) {
        this.rankTimestamp = rankTimestamp;
    }
}
