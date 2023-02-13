// they only have methods
// no attributes
// methods are contracts
// every class implementing the interface must implement all the methods that are in the interface
// you can have a list of friendships, it has a method of make new friends, call make new friends on every object on that friendship list
// every class that implements friendship is a friendship, polymorphism
// polygon is an interface that implements compute area, triangle and rectangle  would be real classes that implement Polygon which contains compute area
// public class Triangle implements Polygon


// option + enter with the cursor on the Interface -> "Implement interface"
public interface Friendship {
    // write methods

    // this will be called from the agents class
    public void makeNewFriends();
}
