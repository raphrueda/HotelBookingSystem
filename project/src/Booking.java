import java.util.Calendar;
import java.util.GregorianCalendar;


public class Booking {
	/**
	 * Constructor for Booking. Each booking represents one night.
	 * @param bookMonth Month of booking
	 * @param bookDate Date of booking
	 * @param bookUser Name of user making the booking
	 * @param bookRoom Room that the booking is for
	 */
	public Booking(int bookMonth, int bookDate, String bookUser, String bookRoom){
		//All bookings are for 2014
		booking = new GregorianCalendar(2014, bookMonth, bookDate);
		user = bookUser;
		room = bookRoom;
	}
	
	/**
	 * Gets the month of this booking.
	 * @return Integer representing the month of this booking.
	 */
	public int getMonth(){
		return booking.get(Calendar.MONTH);
	}
	
	/**
	 * Gets the date of this booking.
	 * @return Integer representing the date of this booking.
	 */
	public int getDate(){
		return booking.get(Calendar.DATE);
	}
	
	/**
	 * Gets the month with displacement of this booking.
	 * @param displace Number of days to displace.
	 * @return Month after displacement.
	 */
	public int getMonthDisplacement(int displace){
		Calendar displacement = new GregorianCalendar(2014, getMonth(), getDate()+displace);
		return displacement.get(Calendar.MONTH);
	}
	
	/**
	 * Gets the date with displacement of this booking.
	 * @param displace Number of days to displace.
	 * @return Date after displacement.
	 */
	public int getDateDisplacement(int displace){
		Calendar displacement = new GregorianCalendar(2014, getMonth(), getDate()+displace);
		return displacement.get(Calendar.DATE);
	}
	
	/**
	 * Gets the user that made booking.
	 * @return Name of this booking's user.
	 */
	public String getUser(){
		return user;
	}
	
	/**
	 * Gets the name of the booking room.
	 * @return Name of this booking's room.
	 */
	public String getRoom(){
		return room;
	}
	
	private Calendar booking;
	private String user;
	private String room;
}
