package support;

/**
 * Created by vidudaya on 15/11/17.
 */
public class Rank {
    private String user;
    private Integer rank; // the rank given by the user

    public Rank(Integer rank, String user) {
        this.rank = rank;
        this.user = user;
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
}
