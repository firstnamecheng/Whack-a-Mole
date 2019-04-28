package client;

/**
 * An interface representing any class whose objects get notified when
 * the objects they are observing update them.
 *
 * @param <Subject> the type of the object that is being observed
 * @author Cheng Ye
 * @author Albert Htun
 */
public interface Observer< Subject > {

    /**
     * The observed subject calls this method on each observer,
     * to tell the observer(s) to update
     */
    void update(Subject subject);

}
