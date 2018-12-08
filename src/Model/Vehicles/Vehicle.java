package Model.Vehicles;

import Exceptions.NotEnoughMoneyException;
import Exceptions.VehicleNotEnoughCapacityException;
import Interfaces.Movable;
import Interfaces.Tradable;
import Model.User;

import java.util.ArrayList;

public abstract class Vehicle implements Movable {
    protected User user;
    private ArrayList<Tradable> sellingList = new ArrayList<>();
    private int capacity;
    private int occupiedCapacity = 0;

    public Vehicle(User user, int capacity) {
        this.user = user;
        this.capacity = capacity;
    }

    public void addToList(Tradable object) throws VehicleNotEnoughCapacityException {
        if (occupiedCapacity < object.getVolume()) {
            throw new VehicleNotEnoughCapacityException();
        }
        sellingList.add(object);
        occupiedCapacity += object.getVolume();
    }

}