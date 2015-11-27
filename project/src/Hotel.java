import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;


public class Hotel {
	/**
	 * Constructor for a hotel. Initialises three lists for each room type
	 * and one list for all of the hotels bookings.
	 * @param hotelName The name of the hotel to construct.
	 */
	public Hotel(String hotelName){
		name = hotelName;
		singleRooms = new ArrayList<Room>();
		doubleRooms = new ArrayList<Room>();
		tripleRooms = new ArrayList<Room>();
		bookings = new ArrayList<Booking>();
	}
	
	/**
	 * Adds the given room to the appropriate room list.
	 * @param roomName The name of room to add.
	 * @param roomType The type of room to add.
	 */
	public void addRoom(String roomName, int roomType){
		Room roomToAdd = new Room(roomName);
		if(roomType == 1){
			if(!roomExists(roomToAdd, roomType)){
				singleRooms.add(roomToAdd);
			}
		} else if(roomType == 2){
			if(!roomExists(roomToAdd, roomType)){
				doubleRooms.add(roomToAdd);
			}
		} else if(roomType == 3){
			if(!roomExists(roomToAdd, roomType)){
				tripleRooms.add(roomToAdd);
			}
		}
	}
	
	/**
	 * Adds the given booking to this hotel. It is assumed that this the 
	 * hotel can accommodate the booking.
	 * @param bookingLine String detailing the booking.
	 */
	public void addBooking(String bookingLine){
		System.out.print(name);
		//Extract user, date and nights requested
		Scanner lineScan = new Scanner(bookingLine);
		String user = lineScan.next();
		int month = monthNameToNumber(lineScan.next());
		int date = lineScan.nextInt();
		int nights = lineScan.nextInt();
		
		//read through the rest of the booking string
		while(lineScan.hasNext()){
			int roomType = typeNameToNumber(lineScan.next());
			int roomsReq = lineScan.nextInt();
			//make a list that points to the correct set of roomTypes
			ArrayList<Room> roomList = null;
			if(roomType == 1){
				roomList = singleRooms;
			} else if(roomType == 2){
				roomList = doubleRooms;
			} else if(roomType == 3){
				roomList = tripleRooms;
			}
			//loop for the desired number of rooms
			for(int i=0; i<roomsReq; i++){
				//loop for the number of rooms 
				for(int j=0; j<roomList.size(); j++){
					boolean availableForAllNights = true;
					//loop for the desired number of nights
					for(int n=0; n<nights; n++){
						//Check if each night is free
						Booking bookingToAdd = new Booking(month, date + n, user, roomList.get(j).getRoomName());
						if(bookingExists(bookingToAdd, bookings)) availableForAllNights = false;
					}
					if(availableForAllNights)/*All nights were free*/{
						for(int n=0; n<nights; n++){
							//Make an individual booking for each night
							Booking bookingToAdd = new Booking(month, date + n, user, roomList.get(j).getRoomName());
							bookings.add(bookingToAdd);
							if(n == 0 ) System.out.print(" " + roomList.get(j).getRoomName());
						}
						break;
					}
				}
			}
		}
		System.out.println();
		
		if(lineScan != null){
			lineScan.close();
		}
	}
	
	/**
	 * Given a booking and a list of bookings, determines in the booking is valid.
	 * @param bookingLine parameters for the booking
	 * @return true if booking is available, false otherwise
	 */
	public boolean canBook(String bookingLine, ArrayList<Booking> bookingsToUse){

		//Create a copy of the bookings that we can manipulate and test
		ArrayList<Booking> bookingCopy = new ArrayList<Booking>();
		for(int i=0; i<bookingsToUse.size(); i++){
			bookingCopy.add(bookingsToUse.get(i));
		}
		
		//Extract user, month, date and nights
		Scanner lineScan = new Scanner(bookingLine);
		String user = lineScan.next();
		int month = monthNameToNumber(lineScan.next());
		int date = lineScan.nextInt();
		int nights = lineScan.nextInt();
		
		//Assume that the booking is valid 
		boolean validBooking = true;
		
		ArrayList<Room> roomList = null;
		while(lineScan.hasNext() && validBooking){
			int roomType = typeNameToNumber(lineScan.next());
			int roomsReq = lineScan.nextInt();
			//make a list that points to the correct set of roomTypes
			if(roomType == 1){
				roomList = singleRooms;
			} else if(roomType == 2){
				roomList = doubleRooms;
			} else if(roomType == 3){
				roomList = tripleRooms;
			}
			
			int counter = 0;
			//loop for the requested number of rooms
			for(int i=0; i<roomsReq; i++){
				//loop for the number of rooms of requested type
				for(int j=0; j<roomList.size(); j++){
					boolean availableForAllNights = true;
					//loop for the number of nights
					for(int n=0; n<nights; n++){
						Booking bookingToAdd = new Booking(month, date + n, user, roomList.get(j).getRoomName());
						if(bookingExists(bookingToAdd, bookingCopy)) availableForAllNights = false;
					}
					if(availableForAllNights){
						for(int n=0; n<nights; n++){
							Booking bookingToAdd = new Booking(month, date + n, user, roomList.get(j).getRoomName());
							bookingCopy.add(bookingToAdd);
							counter ++;
						}
						break;
					}
				}
			}
			if(roomsReq * nights == counter)/*booking was successful on the copy*/{
				validBooking = true;
			} else /*the booking failed */{
				validBooking = false;
				//This will effectively break out of the while loop
			}
		}
		if(lineScan != null){
			lineScan.close();
		}
		return validBooking;
	}
	
	/**
	 * Checks that the specified booking exists under the user's name 
	 * and if so, removes bookings from the list.
	 * @param cancellationDetails Details on which reservations to cancel.
	 * @param user The user requesting the cancellation.
	 * @return True if cancellation went through, false otherwise.
	 */
	public boolean cancelBooking(String cancellationDetails, String user){
		Scanner lineScan = new Scanner(cancellationDetails);
		//Extract the room, month, date and number of nights
		String roomToCancel = lineScan.next();
		int month = monthNameToNumber(lineScan.next());
		int date = lineScan.nextInt();
		int nights = lineScan.nextInt();
		
		if(lineScan != null){
			lineScan.close();
		}
		
		//Check to see if user has actually has a booking for the specified room and dates
		int numberCancelled = 0;
		
		for(int i=0; i<nights; i++){
			Booking bookingToCheck = new Booking(month, date + i, user, roomToCancel);
			if(bookingMadeByUser(bookingToCheck, user)){
				numberCancelled ++;
			}
		}
		
		if(numberCancelled == nights)/*User does have the specified booking*/{
			//actually delete them
			for(int i=0; i<nights; i++){
				Booking bookingToCancel = new Booking(month, date + i, user, roomToCancel);
				for(int j=0; j<bookings.size(); j++){
					if(bookingToCancel.getMonth() == bookings.get(j).getMonth() &&
					   bookingToCancel.getDate() == bookings.get(j).getDate() &&
					   bookingToCancel.getRoom().equals(bookings.get(j).getRoom()) &&
					   bookingToCancel.getUser().equals(bookings.get(j).getUser())){
						bookings.remove(j);
						break;
					}
				}
			}
			return true;
		} else/*User does not have specified booking*/{
			return false;
		}

	}
	
	/**
	 * Checks if the requested change is valid.
	 * @param changeDetails String detailing the requested change.
	 * @param user Name of the user requesting the change
	 * @return 0 if invalid cancellation(and thus, invalid booking),
	 * 		   1 if valid cancellation but invalid booking for this hotel,
	 * 		   2 if valid cancellation and valid booking for this hotel.
	 */
	public int canChange(String changeDetails, String user){
		Scanner lineScan = new Scanner(changeDetails);
		String roomToChange = lineScan.next();
		String oldMonthString = lineScan.next();
		int oldMonth = monthNameToNumber(oldMonthString);
		int oldDate = lineScan.nextInt();
		int oldNights = lineScan.nextInt();
		
		String newMonthString = lineScan.next();
		int newDate = lineScan.nextInt();
		int newNights = lineScan.nextInt();
		String newType = lineScan.next();
		
		if(lineScan != null){
			lineScan.close();
		}
		
		//Check if user has made the specified reservation
		int numberCancelled = 0;
		
		for(int i=0; i<oldNights; i++){
			Booking bookingToCheck = new Booking(oldMonth, oldDate + i, user, roomToChange);
			if(bookingMadeByUser(bookingToCheck, user)){
				numberCancelled ++;
			}
		}
		
		//if the user has not made the specified reservation
		if(numberCancelled != oldNights){
			//then leave the bookings alone and notify the caller class
			return 0;
		} 
		
		//Create a copy of the bookings that we can manipulate and test
		ArrayList<Booking> bookingCopy = new ArrayList<Booking>();
		for(int i=0; i<bookings.size(); i++){
			bookingCopy.add(bookings.get(i));
		}
		
		//Cancel the old bookings in the copied list of all bookings 
		for(int i=0; i<oldNights; i++){
			Booking bookingToCancel = new Booking(oldMonth, oldDate + i, user, roomToChange);
			for(int j=0; j<bookingCopy.size(); j++){
				if(bookingToCancel.getMonth() == bookingCopy.get(j).getMonth() &&
				   bookingToCancel.getDate() == bookingCopy.get(j).getDate() &&
				   bookingToCancel.getRoom().equals(bookingCopy.get(j).getRoom()) &&
				   bookingToCancel.getUser().equals(bookingCopy.get(j).getUser())){
					bookingCopy.remove(j);
					break;
				}
			}
		}
		
		String newBookingLine = user + " " + newMonthString + " " + newDate + " " + newNights + " " + newType + " 1";
		//if the user's requested booking is not available (after cancellation)
		if(!canBook(newBookingLine, bookingCopy)){
			//then leave the bookings alone and notify the caller class (original bookings is left untouched)
			return 1;
		}
		return 2;
	}
	
	/**
	 * Converts each booking instance into a string for printing
	 * @return A list of booking strings in printable format
	 */
	public ArrayList<String> formatBookings(){
		ArrayList<String> unorderedBookings = new ArrayList<String>();
		//If there are no bookings, do nothing
		if(bookings.size() > 0){
			//Counter the number of consecutive nights
			int consecutiveNights = 0;
			//Store the details of the first booking in the list
			String currentUser = bookings.get(0).getUser();
			String currentRoom = bookings.get(0).getRoom();
			Booking startBook = bookings.get(0);
			int i = 0;
			int j = 0;
			//boolean to indicate if there was only one booking made
			boolean onlyOneBooking = true;
			while(i<bookings.size()){
				if(currentUser.equals(bookings.get(i).getUser()) && 
				   currentRoom.equals(bookings.get(i).getRoom()) &&
				   (startBook.getDateDisplacement(j) == bookings.get(i).getDate())){
					consecutiveNights++;
				} else/*new booking found*/ {
					String stringToAdd = (name + " " + bookings.get(i-1).getRoom() + " " + 
							   startBook.getMonth() + " " + 
							   startBook.getDate() + " " + 
							   consecutiveNights + " " + 
							   currentUser);
					unorderedBookings.add(stringToAdd);
					//Reset the counter and current booking
					consecutiveNights = 1;
					currentUser = bookings.get(i).getUser();
					currentRoom = bookings.get(i).getRoom();
					startBook = bookings.get(i);
					j = 0;
					onlyOneBooking = false;
				}
				i++;
				j++;
			}
			if(!onlyOneBooking) {
				startBook = bookings.get(i-consecutiveNights);
			}
			//Print out the last booking
			String stringToAdd = (name + " " + bookings.get(i-1).getRoom() + " " + 
					   startBook.getMonth() + " " + 
					   startBook.getDate() + " " + 
					   consecutiveNights + " " + 
					   currentUser);
			unorderedBookings.add(stringToAdd);
		}
		return unorderedBookings;
	}
	
	/**
	 * Sorts a list of booking strings in alphabetical order and outputs it into System.out.
	 * @param unorderedBookings List of unordered bookings strings 
	 */
	public void printBookings(ArrayList<String> unorderedBookings){
		Collections.sort(unorderedBookings);
		for(int i=0; i<unorderedBookings.size(); i++){
			Scanner bookingScan = new Scanner(unorderedBookings.get(i));
			String hotel = bookingScan.next();
			String room = bookingScan.next();
			String month = monthNumberToName(bookingScan.nextInt());
			String theRest = bookingScan.nextLine();
			if(bookingScan != null){
				bookingScan.close();
			}
			String toAdd = (hotel + " " + room + " " + month + theRest);
			System.out.println(toAdd);
		}
	}
	
	/**
	 * Gets hotel's name.
	 * @return String containing this hotel's name
	 */
	public String getHotelName(){
		return name;
	}
	
	/**
	 * Gets all the bookings for this hotel in the order that they were booked.
	 * @return List containing bookings for this hotel in order they were booked.
	 */
	public ArrayList<Booking> getBookings(){
		return bookings;
	}
	
	/**
	 * Converts a 3 letter representation of a month into 
	 * its relative index.
	 * @param month 3 letter string representing month.
	 * @return The index of the month (GregorianCalendar class standards)
	 */
	private int monthNameToNumber(String month){
		int returnVal = 0;
		if(month.equals("Jan")){
			returnVal = 0;
		} else if(month.equals("Feb")){
			returnVal = 1;
		} else if(month.equals("Mar")){
			returnVal = 2;
		} else if(month.equals("Apr")){
			returnVal = 3;
		} else if(month.equals("May")){
			returnVal = 4;
		} else if(month.equals("Jun")){
			returnVal = 5;
		} else if(month.equals("Jul")){
			returnVal = 6;
		} else if(month.equals("Aug")){
			returnVal = 7;
		} else if(month.equals("Sep")){
			returnVal = 8;
		} else if(month.equals("Oct")){
			returnVal = 9;
		} else if(month.equals("Nov")){
			returnVal = 10;
		} else if(month.equals("Dec")){
			returnVal = 11;
		}
		return returnVal;
	}
	
	/**
	 * Converts a month index into its relative 3 letter representation.
	 * @param month Index of the month (GregorianCalendar class standards)
	 * @return 3 letter representation of month.
	 */
	private String monthNumberToName(int month){
		String returnString = null;
		if(month == 0){
			returnString = "Jan";
		} else if(month == 1){
			returnString = "Feb";
		} else if(month == 2){
			returnString = "Mar";
		} else if(month == 3){
			returnString = "Apr";
		} else if(month == 4){
			returnString = "May";
		} else if(month == 5){
			returnString = "Jun";
		} else if(month == 6){
			returnString = "Jul";
		} else if(month == 7){
			returnString = "Aug";
		} else if(month == 8){
			returnString = "Sep";
		} else if(month == 9){
			returnString = "Oct";
		} else if(month == 10){
			returnString = "Nov";
		} else if(month == 11){
			returnString = "Dec";
		}
		return returnString;
	}

	/**
	 * Converts string representing roomType into its integer equivalent.
	 * @param type Room type to convert.
	 * @return 1 if Single Rooms, 2 if Double Rooms, 3 if Triple Rooms.
	 */
	private int typeNameToNumber(String type){
		int returnVal = 0;
		if(type.equals("single")){
			returnVal = 1;
		} else if(type.equals("double")){
			returnVal = 2;
		} else if(type.equals("triple")){
			returnVal = 3;
		}
		return returnVal;
	}
	
	/**
	 * Checks if this hotel has the given room.
	 * @param roomToCheck The room to look for.
	 * @param roomType The type of the room.
	 * @return True if hotel has the room, false otherwise.
	 */
	private boolean roomExists(Room roomToCheck, int roomType){
		ArrayList<Room> type = null;
		if(roomType == 1){
			type = singleRooms;
		} else if(roomType == 2){
			type = doubleRooms;
		} else if(roomType == 3){
			type = tripleRooms;
		}
		
		for(int i=0; i<type.size(); i++){
			if(roomToCheck.getRoomName() == type.get(i).getRoomName())
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if given booking exists inside of the given bookings.
	 * @param bookingToCheck The booking to look for.
	 * @param booking The set of booking to look inside of.
	 * @return True if the booking exists inside the given bookings,
	 * 		   false otherwise.
	 */
	private boolean bookingExists(Booking bookingToCheck, ArrayList<Booking> bookingsToUse){
		boolean returnVal = false;
		
		for(int i=0; i<bookingsToUse.size(); i++){
			if(bookingsToUse.get(i).getMonth() == bookingToCheck.getMonth() &&
				bookingsToUse.get(i).getDate() == bookingToCheck.getDate() &&
				bookingsToUse.get(i).getRoom().equals(bookingToCheck.getRoom())) returnVal = true;
		}
		
		return returnVal;
	}
	
	/**
	 * Checks if given booking exists and if it was made by given user.
	 * @param bookingToCheck The booking we are checking.
	 * @param user The user we are checking.
	 * @return True if user made the booking, false otherwise.
	 */
	private boolean bookingMadeByUser(Booking bookingToCheck, String user){
		boolean returnVal = false;
		
		for(int i=0; i<bookings.size(); i++){
			if(bookings.get(i).getMonth() == bookingToCheck.getMonth() &&
			   bookings.get(i).getDate() == bookingToCheck.getDate() &&
			   bookings.get(i).getRoom().equals(bookingToCheck.getRoom()) &&
			   bookings.get(i).getUser().equals(bookingToCheck.getUser())) returnVal = true;
		}	
		
		return returnVal;
	}	
	
	private String name;
	private ArrayList<Room> singleRooms;
	private ArrayList<Room> doubleRooms;
	private ArrayList<Room> tripleRooms;
	private ArrayList<Booking> bookings;
}
