package orm.query.examples.ehcache.dao;

/**
 *
 */
public enum BoStage {
    INITIAL(0), APPROVAL(1);

    private int stage;

    BoStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }
}
