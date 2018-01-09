package fi.raah.android.curious_catalog_gatherer.model;

public class DomainAndToken {

    private final String domainName;
    private final String token;

    public DomainAndToken(String domainName, String token) {
        this.domainName = domainName;
        this.token = token;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getToken() {
        return token;
    }
}
