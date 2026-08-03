package helper.creeperbox.utils.mc;

public class StopWatch {
    private long millis;

    public void setMillis(long millis) {
        /* 12 */     this.millis = millis;
    }
    public StopWatch() {
        reset();
    }

    public boolean finished(long delay) {
       return (System.currentTimeMillis() - delay >= this.millis);
    }

    public void reset() {
        /* 25 */     this.millis = System.currentTimeMillis();
    }

    public long getMillis() {
        /* 29 */     return this.millis;
    }

    public long getElapsedTime() {
        /* 33 */     return System.currentTimeMillis() - this.millis;
    }
}