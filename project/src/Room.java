
public class Room {
	/**
	 * Constructor for Room.
	 * @param roomName Name of the room.
	 */
	public Room(String roomName){
		name = roomName;
	}
	
	/**
	 * Gets the name of room.
	 * @return Name of room.
	 */
	public String getRoomName(){
		return name;
	}
	
	private String name;
}
