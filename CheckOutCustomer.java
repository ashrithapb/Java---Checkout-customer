import java.util.Scanner;
import java.util.Random;

public class CheckOutCustomer {
	String[][] roomDetails;
	String[][] billDetails;
	String[][] customerDetails;
	int checkoutRoom;
	
	/* Constructor - assign initialize values*/
	public CheckOutCustomer() { 
		this.roomDetails = new String[][]{{"301", "Occupied"},{"302","Available"},{"303", "Occupied"}};
		this.billDetails = new String[][]{{"301", "John's Bill","100",""},{"303","Sam's Bill","200",""}};
		this.customerDetails = new String[][]{{"301","John","806‐333‐1234"},{"303","Sam","806‐333‐4567"}};
	}
	public void setRoom(int Room) {
		this.checkoutRoom = Room;
	}
	public int getRoom() {
		return this.checkoutRoom;
	}
	
	class DeskClerkInterface
	{
		public Bill checkOut(int RoomNum) {
			int room_count = roomDetails.length;
			Bill Billobj = null;
			for(int i=0;i<room_count;i++) {
				if(Integer.parseInt(roomDetails[i][0]) == RoomNum) {
					if(roomDetails[i][1]== "Occupied") {
						CheckOutManager cmanager = new CheckOutManager();
						Billobj = cmanager.requestBill(RoomNum);
						setRoom(RoomNum);
						return Billobj;	 
					}else {
						System.out.println("This room number is not occupied."); 
						System.out.println(" ---- program exit----");
						System.exit(0);
					}
				}
			}
			System.out.println("Invalid room number{valid:301,302,303}---- program exit---");
			System.exit(0);
			return Billobj;
		}
		public void displayMessage(String Message) {
			if(Message == "BillPrinted") {
				System.out.println("BillPrinted: Bill Successfully Printed.");
			}else {
				System.out.println("Credit Card denied.");
			}
		}
	}
	class BankInterface{
		public int chargeCreditCard(long CardNo, int Total) {
			/* Credit card number validation check - using Luhn Algorithm */
			String ccNumber = String.valueOf(CardNo);
			int sum = 0;
			int Result;
	        boolean alternate = false;
	        for (int i = ccNumber.length() - 1; i >= 0; i--)
	        {
	            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
	            if (alternate)
	            {
	                n *= 2;
	                if (n > 9)
	                {
	                    n = (n % 10) + 1;
	                }
	            }
	            sum += n;
	            alternate = !alternate;
	        }
	        boolean valid = (sum % 10 == 0);
	        if(valid) {
	        	System.out.println("Please enter 4 digit  authorization number");
	        	try{
	        		Scanner pin = new Scanner(System.in);
	        		int cardPin = pin.nextInt(); 
	        		if(cardPin <= 9999 && cardPin >=1000) {
	        			System.out.println("4 digit  authorization number is accepted.");
	        		pin.close();
	        		}else {
	        			System.out.println("Authorization number should be 4-digit number in the range(1000 to 9999)");
		        		System.out.println("--------Program exit----");
		        		System.exit(0);
	        		}
	        	}catch(Exception e) {
	        		System.out.println("Authorization number is not the valid one.");
	        		System.out.println("--------Program exit----");
	        		System.exit(0);
	        	}
	        	
				Random rand = new Random();
				Result = rand.nextInt(9999);
	        }else {
	        	Result = -1;
	        }
			return Result;
		}
	}
	class  ReceiptPringerInterface{
		public void printReceipt(long CardNo, int Total, int ReferenceNo) {
			String str = String.valueOf(CardNo);
			String last4digits = str.substring(str.length() - 4);
			System.out.println();
			System.out.println("----------------------Reciept generated------------------------");
			System.out.println("Your card number: xxxxx..."+last4digits+" is charged.");
			System.out.println("Total amount charged: "+Total);
			System.out.println("Authorization/reference number:"+ReferenceNo);
			System.out.println("------------------------------------------------------");
			System.out.println();
		} 
	}
	class CardReaderInterface{
		public void readCard(long CardNo) {
			CheckOutManager checkoutmanager = new CheckOutManager();
			checkoutmanager.payByCreditCard(CardNo);
		}
	}
	class CheckOutManager{
		public Bill requestBill(int RoomNo) {
			Bill bill = new Bill();
			Bill billobj = bill.readBill(RoomNo);
			return billobj;
		}
		public void payByCreditCard(long CardNo) {
			Bill bill = new Bill();
			int RoomNo = getRoom();
			int total = bill.readTotal(RoomNo);
			BankInterface bankinterface = new BankInterface();
			Room room = new Room();
			Customer customer = new Customer();
			BillPrinterInterface billprinterinterface = new BillPrinterInterface();
			DeskClerkInterface deskclerkinterface = new DeskClerkInterface();
			boolean b;
			String str;
			int result=0;
			int ReferenceNo = 0;
			try {
				result = bankinterface.chargeCreditCard(CardNo, total);
				if(result != -1) {
				ReferenceNo = result;
				b = true;
				}else {
					b = false;
				}
			}catch(Exception e) {
				b = false;
			}
			if(b) {
				ReceiptPringerInterface receiptpringerinterface = new ReceiptPringerInterface();
				receiptpringerinterface.printReceipt(CardNo, total, ReferenceNo);
				bill.updateReference(RoomNo, ReferenceNo);
				int RoomReleased = room.releaseRoom(RoomNo);
				System.out.println("RoomReleased: "+RoomReleased);
				customer.deleteCustomer(RoomReleased);
				Bill billobj = bill.readBill(RoomReleased);
				billprinterinterface.printBill(billobj);
				str = "BillPrinted";
				deskclerkinterface.displayMessage(str);
			}else {
				str = "CCDenied";
				deskclerkinterface.displayMessage(str);
			}
		}
	}
	class BillPrinterInterface{
		public void printBill(Bill bill) {
			System.out.println("-------------Printing Bill------------------");
			System.out.println("Room Number: "+bill.RoomNo);
			System.out.println("Bill: "+bill.BillN);
			System.out.println("Total amount charged: "+bill.Total);
			System.out.println("Reference number: "+bill.ReferenceNo);
			System.out.println("---------------------------------------------");
			System.out.println();
		}
	}
	class Room{
		int RoomNo;
		String Status;
		public Room() {
			//this.RoomNo = new int[]{301, 302, 303};
			//this.Status  = new String[] {"Occupied","Available"};
		}
		public int releaseRoom(int RoomNo) {
			this.Status = "Available";
			this.RoomNo = RoomNo;
			return RoomNo;
		} 
	}
	class Customer{
		int RoomNo;
		String Name;
		String PhoneNo;
		public Customer() {
			
		}
		public String deleteCustomer(int RoomNo) {
			int customer_count = customerDetails.length;
			String CustomerDeleted=null;
			//System.out.println("R---"+RoomNo);
			for(int i=0;i<customer_count;i++) {
				if(Integer.parseInt(customerDetails[i][0]) == RoomNo) {
					CustomerDeleted = customerDetails[i][1];
					this.RoomNo= 0;
					this.Name = null;
					this.PhoneNo = null;
					customerDetails[i][1]=null;
					customerDetails[i][2]=null;					
				}
			}
			System.out.println("deleted customer is : "+CustomerDeleted);
			return CustomerDeleted;
		}
	}
	class Bill{
		int RoomNo;
		String BillN;
		int Total;
		int ReferenceNo;
		public Bill() {
			
		}
		public Bill readBill(int RoomNo) {
			this.RoomNo = RoomNo;
			int bill_count = billDetails.length;
			for(int i=0;i<bill_count;i++) {
				if(Integer.parseInt(billDetails[i][0]) == RoomNo) {
					this.BillN = billDetails[i][1];
					this.Total = Integer.parseInt(billDetails[i][2]);
					return this;
				}
			}
			return this;
		}
		public int readTotal(int RoomNo) {
			int bill_count = billDetails.length;
			for(int i=0;i<bill_count;i++) {
				if(Integer.parseInt(billDetails[i][0]) == RoomNo) {
					this.Total = Integer.parseInt(billDetails[i][2]);
					return Total;
				}
			}
			return -1;
		}
		public void updateReference(int RoomNo, int ReferenceNo) {
			int bill_count = billDetails.length;
			for(int i=0;i<bill_count;i++) {
				if(Integer.parseInt(billDetails[i][0]) == RoomNo) {
					this.ReferenceNo = ReferenceNo;
					billDetails[i][3]= String.valueOf(ReferenceNo);
				}
			}
		}
		
	}

	public static void main(String[] args) {
		CheckOutCustomer coc = new CheckOutCustomer();
		CheckOutCustomer.DeskClerkInterface dci = coc.new DeskClerkInterface();
		System.out.println("Enter one of the Room number: {301,302,303}");
		try{
			Scanner in = new Scanner(System.in);
			int RoomNo = in.nextInt();
			Bill b = dci.checkOut(RoomNo);
			System.out.println("-------Checkout Bill details-------");
			System.out.println("Room number: "+b.RoomNo);
			System.out.println("Bill: "+b.BillN);
			System.out.println("Total: "+b.Total);
			System.out.println("--------------------------------");
			System.out.println();
		}catch(Exception e) {
			System.out.println("Entered Room number is in invalid format.------program exit-----");
			System.exit(0);
		}
		System.out.println("Please enter Card number:");
		try {
			CheckOutCustomer.CardReaderInterface cri = coc.new CardReaderInterface();
			Scanner input = new Scanner(System.in);
			long cardno = input.nextLong();
			cri.readCard(cardno);
			
		}catch(Exception e) {
			System.out.println("Invalid credit card number.");
		}
	}
}
