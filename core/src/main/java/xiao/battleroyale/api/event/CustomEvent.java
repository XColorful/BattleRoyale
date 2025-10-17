package xiao.battleroyale.api.event;

public abstract class CustomEvent implements ICustomEvent {

    private boolean isCanceled = false;

    public CustomEvent() {
    }

    @Override
    public boolean isCanceled() {
        return this.isCanceled;
    }

    @Override
    public void setCanceled(boolean cancel) {
        if (isCancelable()) {
            this.isCanceled = cancel;
        }
    }

    @Override
    public Object getEvent() {
        return this;
    }
}
