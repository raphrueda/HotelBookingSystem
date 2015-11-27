import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class HotelBookingSystem {
	/**
	 * Reads input text file from args, filters it and feeds it to
	 * a HotelManager.
	 * @param args args[0] contains the text file for input.
	 */
	public static void main(String[] args){
		Scanner sc = null;
	    try
	    {
	    	sc = new Scanner(new FileReader(args[0]));
	    }
	    catch (FileNotFoundException e) {
	    	System.out.println("File not found.");
	    	System.exit(0);
	    }	
	    
	    HotelManager hotelManager = new HotelManager();
	    
	    String command;
	    String parameters;
	    while(sc.hasNextLine() && sc.hasNext()){
	    	command = sc.next();
	    	parameters = sc.nextLine();
	    	
	    	if(command.equals("Hotel")){
	    		hotelManager.addHotelAndRoom(parameters);
	    	} else if(command.equals("Book")){
	    		hotelManager.addBooking(parameters);
	    	} else if(command.equals("Change")){
	    		hotelManager.changeBooking(parameters);
	    	} else if(command.equals("Cancel")){
	    		hotelManager.cancelBooking(parameters);
	    	} else if(command.equals("Print")){
	    		Scanner getHotelName = new Scanner(parameters);
	    		String hotelName = getHotelName.next();
	    		getHotelName.close();
	    		hotelManager.printHotelBookings(hotelName);
	    	} 
	    }

	}
}
