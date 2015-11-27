import java.util.ArrayList;
import java.util.Scanner;


public class HotelManager {
	/**
	 * Constructor for HotelManager. Initialises a list of hotels.
	 */
	public HotelManager(){
		hotels = new ArrayList<Hotel>();
	}
	
	/**
	 * Adds the specified hotel to the list of hotels and delegates the task of 
	 * adding the room to that hotel. If hotel is unknown to the system, it is added. 
	 * @param parameters String detailing the hotel/room name and room type.
	 */
	public void addHotelAndRoom(String parameters){

		Scanner lineScan = new Scanner(parameters);
		String hotelName = lineScan.next();
		String roomName = lineScan.next();
		int roomType = lineScan.nextInt();
		
		if(!hotelExists(hotelName)) /*make new hotel*/{
			Hotel newHotel = new Hotel(hotelName);
			hotels.add(newHotel);
		}
		
		int hotelIndex = getHotelIndex(hotelName);
		hotels.get(hotelIndex).addRoom(roomName, roomType);
		
		if(lineScan != null){
			lineScan.close();
		}

	}
	
	/**
	 * Checks which hotel can accommodate the specified booking (if any)
	 * and delegates the task of adding the booking to that hotel.
	 * @param parameters String detailing the requested booking.
	 */
	public void addBooking(String parameters){
		int availableHotelIndex = firstAvailableHotel(parameters);
		if(availableHotelIndex == -1){
			System.out.println("Booking rejected");
		} else {
			hotels.get(availableHotelIndex).addBooking(parameters);
		}
	}
	
	/**
	 * Checks if the requested change is valid, and if so, delegates the responsibility
	 * of removing the old booking to the corresponding hotel and the responsibility of
	 * adding the new booking to the first hotel that can accommodate it. 
	 * @param parameters String detailing the previous booking and the desired new booking.
	 */
	public void changeBooking(String parameters){
		//Extract the user, hotel and change details from parameters
		Scanner lineScan = new Scanner(parameters);
		String user = lineScan.next();
		int oldHotelIndex = getHotelIndex(lineScan.next());
		String changeDetails = lineScan.nextLine();
		
		//Extract the old and new booking details from changeDetails
		Scanner bookingScan = new Scanner(changeDetails);
		String oldRoom = bookingScan.next();
		String oldMonth = bookingScan.next(); 
		int oldDate = bookingScan.nextInt();
		int oldNights = bookingScan.nextInt(); 
		
		String monthToAdd = bookingScan.next();
		int dateToAdd = bookingScan.nextInt();
		int nightsToAdd = bookingScan.nextInt();
		String typeToAdd = bookingScan.next();
		
		//close scanners
		if(bookingScan != null)bookingScan.close();
		if(lineScan != null)lineScan.close();
		
		//Create the appropriate cancel and add strings
		String bookingToCancel = oldRoom + " " + oldMonth + " " + oldDate + " " + oldNights;
		String bookingToAdd = user + " " + monthToAdd + " " + dateToAdd + " " + nightsToAdd + " " + typeToAdd + " 1";
		
		//Check if the hotel we are canceling from can cancel and/or book the respective bookings
			//returns 0 if can't cancel => can't book
			//returns 1 if can cancel but can't book
			//returns 2 if can cancel and can book
		int oldCanCancelAdd = hotels.get(oldHotelIndex).canChange(changeDetails, user);
		
		//index of the first hotel that can accommodate bookingToAdd
		int firstAvailableHotel = firstAvailableHotel(bookingToAdd);
		
		if(oldCanCancelAdd == 0)/*Can't cancel the old booking*/{
			System.out.println("Change rejected");	
		} else if(firstAvailableHotel == -1 && oldCanCancelAdd == 1)/*Can cancel, but no hotel can book*/{
			System.out.println("Change rejected");
		} else if(firstAvailableHotel != -1 && oldCanCancelAdd == 1 )/*Can cancel, will book in firstAVailableHotel*/{
			hotels.get(oldHotelIndex).cancelBooking(bookingToCancel, user);
			hotels.get(firstAvailableHotel).addBooking(bookingToAdd);
		} else /*Can cancel and ATLEAST ONE hotel can book*/{
			if(firstAvailableHotel == -1 && oldCanCancelAdd == 2) /*only original hotel can book*/{
				hotels.get(oldHotelIndex).cancelBooking(bookingToCancel, user);
				hotels.get(oldHotelIndex).addBooking(bookingToAdd);
			} else if(firstAvailableHotel >= oldHotelIndex)/*original hotel is the first hotel that can book*/{
				hotels.get(oldHotelIndex).cancelBooking(bookingToCancel, user);
				hotels.get(oldHotelIndex).addBooking(bookingToAdd);
			} else if(firstAvailableHotel < oldHotelIndex)/*firstAvailableHotel can accommodate the booking*/{
				hotels.get(oldHotelIndex).cancelBooking(bookingToCancel, user);
				hotels.get(firstAvailableHotel).addBooking(bookingToAdd);
			} else/*should be dead code but just in case*/{
				System.out.println("Change rejected");
			}
		}
	}
	
	/**
	 * Delegates the process of checking if a cancellation is valid, as well as the  
	 * cancellation itself (if it is valid) to the specified hotel. 
	 * @param parameters String detailing the cancellation.
	 */
	public void cancelBooking(String parameters){
		//Extract user, hotel and cancellation details
		Scanner lineScan = new Scanner(parameters);
		String user = lineScan.next();
		int hotelIndex = getHotelIndex(lineScan.next());
		String cancellationDetails = lineScan.nextLine();
		
		if(lineScan != null){
			lineScan.close();
		}
		
		//Sends cancellation details to the specified hotel
		if(!hotels.get(hotelIndex).cancelBooking(cancellationDetails, user)){
			//Notifies if cancellation was rejected
			System.out.println("Cancellation rejected");
		} else {
			System.out.println("Reservation cancelled");
		}
	}
	
	/**
	 * Prints out all of the specified hotel's bookings in the order of room name and then start date.
	 * @param hotelName String containing the name of the desired hotel.
	 */
	public void printHotelBookings(String hotelName){
		int hotelIndex = getHotelIndex(hotelName);
		ArrayList<String> unorderedBookings = hotels.get(hotelIndex).formatBookings();
		hotels.get(hotelIndex).printBookings(unorderedBookings);
	}

	/**
	 * Returns the index of the first hotel that can make the
	 * requested booking.
	 * @param parameters Describes the desired booking
	 * @return The index of the first valid hotel or -1 if there 
	 * is no valid hotel.
	 */
	private int firstAvailableHotel(String parameters){
		
		int returnIndex = -1;
		for(int i=0; i<hotels.size(); i++){
			if(hotels.get(i).canBook(parameters, hotels.get(i).getBookings())){
				returnIndex = i;
				break;
			}
		}
		return returnIndex;
	}
	
	/**
	 * Returns the index of the given hotel in the ArrayList.
	 * @param hotelName The name of the hotel to look for.
	 * @return The index of the hotel.
	 */
	private int getHotelIndex(String hotelName){
		int returnIndex = -1;
		for(int i=0; i<hotels.size(); i++){
			if(hotels.get(i).getHotelName().equals(hotelName)) returnIndex = i;	
		}
		return returnIndex;
	}
	
	/**
	 * Checks if the given hotel is in the list.
	 * @param hotelName The name of the hotel to look for.
	 * @return True if the give hotel is in the list of current hotels,
	 * 		   false otherwise.
	 */
	private boolean hotelExists(String hotelName){
		boolean hotelExists = false;
		for(int i=0; i<hotels.size(); i++){
			if(hotels.get(i).getHotelName().equals(hotelName)) hotelExists = true;	
		}
		return hotelExists;
	}
	
	//List of all the hotels added to the system
	private ArrayList<Hotel> hotels;
}
