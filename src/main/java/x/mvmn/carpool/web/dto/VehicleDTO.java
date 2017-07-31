package x.mvmn.carpool.web.dto;

import x.mvmn.carpool.model.Vehicle;

public class VehicleDTO {

	protected int id;
	protected String name;
	protected String plateNumber;
	protected int passengerSeats;
	protected String description;
	protected UserDTO owner;

	public VehicleDTO() {
	}

	public VehicleDTO(int id, String name, String plateNumber, int passengerSeats, String description) {
		this.id = id;
		this.name = name;
		this.plateNumber = plateNumber;
		this.passengerSeats = passengerSeats;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public UserDTO getOwner() {
		return owner;
	}

	public void setOwner(UserDTO owner) {
		this.owner = owner;
	}

	public int getPassengerSeats() {
		return passengerSeats;
	}

	public void setPassengerSeats(int passengerSeats) {
		this.passengerSeats = passengerSeats;
	}

	public static VehicleDTO fromVehicleIgnoreUser(Vehicle vehicle) {
		return new VehicleDTO(vehicle.getId(), vehicle.getName(), vehicle.getPlateNumber(), vehicle.getPassengerSeats(), vehicle.getDescription());
	}
}
