package pt.ulisboa.tecnico.softeng.broker.domain;


import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ulisboa.tecnico.softeng.broker.exception.BrokerException;
import pt.ulisboa.tecnico.softeng.broker.interfaces.ActivityInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.BankInterface;
import pt.ulisboa.tecnico.softeng.broker.interfaces.HotelInterface;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;

public class Adventure {
	private static Logger logger = LoggerFactory.getLogger(Adventure.class);

	private static int counter = 0;

	private final String ID;
	private final Broker broker;
	private final LocalDate begin;
	private final LocalDate end;
	private final int age;
	private final String IBAN;
	private final int amount;
	private String bankPayment;
	private String roomBooking;
	private String activityBooking;

	public Adventure(Broker broker, LocalDate begin, LocalDate end, int age, String IBAN, int amount) {
		fullCheck(broker, begin, end, age, IBAN, amount);
		this.ID = broker.getCode() + Integer.toString(++counter);
		this.broker = broker;
		this.begin = begin;
		this.end = end;
		this.age = age;
		this.IBAN = IBAN;
		this.amount = amount;
		
		if(broker.hasAdventure(this)) throw new BrokerException("Duplicate Adventure");
		broker.addAdventure(this);
	}
	
	public void fullCheck(Broker broker, LocalDate begin, LocalDate end, int age, String IBAN, int amount) {
		if(broker==null) throw new BrokerException("Null broker");
		if(begin==null) throw new BrokerException("Null begin date");
		if(end==null) throw new BrokerException("Null end date");
		if(IBAN==null) throw new BrokerException("Null IBAN");
		
		if(end.isBefore(begin)) throw new BrokerException("Begin date after end date");
		if(begin.isBefore(LocalDate.now())) throw new BrokerException("Begin date before actual date(today)");
		
		if(age<18) throw new BrokerException("Min age >17");
		if(age>99) throw new BrokerException("Max age <100");
		if(amount<1) throw new BrokerException("Amount <0 should not exist");
		
		if(IBAN.length()<5) throw new BrokerException("IBAN can't be shorter than 5 digits");
		if(IBAN.trim().length()==0) throw new BrokerException("IBAN composed only of whitespaces");
	}

	public String getID() {
		return this.ID;
	}

	public Broker getBroker() {
		return this.broker;
	}

	public LocalDate getBegin() {
		return this.begin;
	}

	public LocalDate getEnd() {
		return this.end;
	}

	public int getAge() {
		return this.age;
	}

	public String getIBAN() {
		return this.IBAN;
	}

	public int getAmount() {
		return this.amount;
	}

	public String getBankPayment() {
		return this.bankPayment;
	}

	public String getRoomBooking() {
		return this.roomBooking;
	}

	public String getActivityBooking() {
		return this.activityBooking;
	}

	public void process() {
		logger.debug("process ID:{} ", this.ID);
		this.bankPayment = BankInterface.processPayment(this.IBAN, this.amount);
		this.roomBooking = HotelInterface.reserveHotel(Room.Type.SINGLE, this.begin, this.end);
		this.activityBooking = ActivityInterface.reserveActivity(this.begin, this.end, this.age);
	}
	
	/* the 2 following methods are Overridden for hasAdventure
	  method in Broker, to compare 2 Adventures. 
	  HashSet.contains() first checks if an object has the same hashCode as the other,
	  then compares with equals.
	  this hashCode method uses the unique IBAN string, and so does equals
	  verify with duplicateCode test
	 */
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if (!Adventure.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final Adventure adv = (Adventure) obj;
	    if(this.IBAN.trim().equals(adv.getIBAN().trim())) {
	    	return true;
	    }
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.IBAN.trim().hashCode();
	}
}
