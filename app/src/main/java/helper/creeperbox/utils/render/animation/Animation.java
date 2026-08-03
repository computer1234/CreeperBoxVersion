package helper.creeperbox.utils.render.animation;

public class Animation {
    private Easing easing;
    private long duration;
    private long millis;
    private long startTime;

    private float startValue;
    private float destinationValue;
    private float value;
    private boolean finished;

    public Animation(final Easing easing, final long duration) {
        this.easing = easing;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
    }

    /**
     * Updates the animation by using the easing function and time
     *
     * @param destinationValue the value that the animation is going to reach
     */
    public void run(final float destinationValue) {
        this.millis = System.currentTimeMillis();
        if (this.destinationValue != destinationValue) {
            this.destinationValue = destinationValue;
            this.reset();
        } else {
            this.finished = this.millis - this.duration > this.startTime;
            if (this.finished) {
                this.value = destinationValue;
                return;
            }
        }

        final double result = this.easing.getFunction().apply(this.getProgress());
        if (this.value > destinationValue) {
            this.value = (float) (this.startValue - (this.startValue - destinationValue) * result);
        } else {
            this.value = (float) (this.startValue + (destinationValue - this.startValue) * result);
        }
    }

    public boolean hasTimeElapsed(float time) {
        return System.currentTimeMillis() - this.startTime > time;
    }


    public float getDestinationValue() {
        return destinationValue;
    }


    public float getValue() {
        return value;
    }


    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Returns the progress of the animation
     *
     * @return value between 0 and 1
     */
    public double getProgress() {
        return (double) (System.currentTimeMillis() - this.startTime) / (double) this.duration;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isFinished(){
        return finished;
    }

    /**
     * Resets the animation to the start value
     */
    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.startValue = value;
        this.finished = false;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setEasing(Easing easing) {
        this.easing = easing;
    }
}
