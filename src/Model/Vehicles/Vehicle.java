package Model.Vehicles;

import Exceptions.MaxLevelExceededException;
import Exceptions.NotEnoughMoneyException;
import Exceptions.CapacityExceededException;
import Exceptions.NotValidCoordinatesException;
import Interfaces.*;
import Model.Direction;
import Model.Mission;
import Model.Position;

import java.util.ArrayList;

public abstract class Vehicle implements Movable, Upgradable, VisibleInMap, VisibleOutOfMap {
    protected Mission mission;
    protected ArrayList<Tradable> tradingObjects = new ArrayList<>();
    protected int capacity;
    protected int occupiedCapacity = 0;
    protected int[] VEHICLE_UPGRADE_COSTS = new int[4];
    protected static int VEHICLE_MAX_LEVEL = 3;
    protected int[] TRAVEL_DURATIONS = new int[4];
    protected  int travelDuration;

    protected int level = 0;
    protected Position position = new Position(1023,1023);//todo : yek makane khas rooye map ra behesh ekhtesas midim
    protected Direction direction = new Direction(0,0);



    public Vehicle(Mission mission, int capacity) throws NotValidCoordinatesException {
        this.mission = mission;
        this.capacity = capacity;
    }

    public void addToList(Tradable object) throws CapacityExceededException {
        if (occupiedCapacity < object.getVolume()) {
            throw new CapacityExceededException();
        }
        tradingObjects.add(object);
        occupiedCapacity += object.getVolume();
    }

    @Override
    public void upgrade() throws NotEnoughMoneyException, MaxLevelExceededException {
            if (this.level == VEHICLE_MAX_LEVEL){
                throw new MaxLevelExceededException();
            }
            if (this.mission.getMoney() <= VEHICLE_UPGRADE_COSTS[level+1]){
                throw new NotEnoughMoneyException();
            }
            this.level++;
            travelDuration = TRAVEL_DURATIONS[this.level];
            mission.spendMoney(VEHICLE_UPGRADE_COSTS[this.level]);
    }

}
