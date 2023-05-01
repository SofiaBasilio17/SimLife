public class Preconditions {
    // TODO: change the way resources work, consume will always require a resource
    // TODO: Precondition location may be a list of locations
    // E.G. you can buy cigarettes at different stores
    // time periods and resources may also be a list,
    // e.g. if you are hungry you may want to consume a resource of type food
    // e.g. a store may be open in two different periods, lunch break being the one that breaks the period
    private String name;
    private String location;

    private MoveTo locationAction;
    private String resource;
    private Acquire resourceAction;
    private TimePeriod time;

    public Preconditions(String name){
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public void setResource(String resource){
        this.resource = resource;
    }
    public void setTime(TimePeriod time) {
        this.time = time;
    }

    public void setMoveTo(MoveTo movementAction){
        this.locationAction = movementAction;
    }

    public void setAcquire(Acquire acquireAction) { this.resourceAction = acquireAction; }
    public boolean isPossible(int currentTime){
        boolean possibilityTime = true;
        boolean possibilityLocation = true;
        boolean possibilityResource = true;
        if (this.time != null){
            possibilityTime = this.time.isWithinTimePeriod(currentTime);
        }
        if (this.location != null){
            possibilityLocation = this.locationAction.isPossible(currentTime);
        }
//        if (this.resource != null){
//            possibilityResource = this.resourceAction.isPossible(currentTime);
//        }
        return possibilityTime & possibilityLocation;
    }
    public boolean isPossibleToAcquire(int currentTime){
        return this.resourceAction.isPossible(currentTime);
    }
    public MoveTo getLocationAction(){
        return this.locationAction;
    }

    public Acquire getResourceAction(){
        return this.resourceAction;
    }
    public Double preconditionsValue(String location, int t){
        // TODO: we do not need a time check here because we are already doing a time possibility check
        Double totalConditions = 0.0;
        Double satisfiedConditions = 0.0;
        if (this.location != null){
            totalConditions += 1.0;
            if(this.matchesLocation(location)){

                satisfiedConditions += 1.0;
            }
        }
        if(this.time != null){
            totalConditions += 1.0;
            if (this.time.isWithinTimePeriod(t)){
                satisfiedConditions += 1.0;
            }
        }
        if (totalConditions > 0.0){
            // if there are conditions
            if(satisfiedConditions == 0.0){
                // and none of them are fulfilled we return 0 otherwise we are dividing by 0
                return 0.0;
            }else {
                // if there is a number > 0 for both total and satisfied, we return their division
                return satisfiedConditions/totalConditions;
            }
        }
        return 0.0;
    }

    public boolean matchesLocation(String location){
        if (this.location.equalsIgnoreCase(location)){
            return true;
        }
        return false;
    }

    public boolean hasLocation(){
        if (this.location != null){
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        String str = "Preconditions:\n";
        if (time != null){
            str += "* time " + this.time.toString() + "\n";
        }
        if(location != null){
            str += "* location " + this.location + "\n";
            str += "* moveTo "+ this.locationAction.getName()+ "\n";
        }
        if (resource != null){
            str += "* resource " + this.resource + "\n";
        }
        return str;
    }
}
