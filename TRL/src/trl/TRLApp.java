package trl;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class TRLApp {

	private String title = "  _______        _   _                 _       _____            _        _    _      _ _                          \r\n"
			+ " |__   __|      | | | |               | |     |  __ \\          | |      | |  | |    (_) |                         \r\n"
			+ "    | | _____  _| |_| |__   ___   ___ | | __  | |__) |___ _ __ | |_ __ _| |  | |     _| |__  _ __ __ _ _ __ _   _ \r\n"
			+ "    | |/ _ \\ \\/ / __| '_ \\ / _ \\ / _ \\| |/ /  |  _  // _ \\ '_ \\| __/ _` | |  | |    | | '_ \\| '__/ _` | '__| | | |\r\n"
			+ "    | |  __/>  <| |_| |_) | (_) | (_) |   <   | | \\ \\  __/ | | | || (_| | |  | |____| | |_) | | | (_| | |  | |_| |\r\n"
			+ "    |_|\\___/_/\\_\\\\__|_.__/ \\___/ \\___/|_|\\_\\  |_|  \\_\\___|_| |_|\\__\\__,_|_|  |______|_|_.__/|_|  \\__,_|_|   \\__, |\r\n"
			+ "                                                                                                             __/ |\r\n"
			+ "                                                                                                            |___/ ";
	private Scanner scanner = new Scanner(System.in);
	private TPLController controller = new TPLController();
	private Worker worker;

	public static void main(String[] args) {
		TRLApp app = new TRLApp();
		app.displayAppTitle();
		app.launch();
	}

	private void launch() {
		while (true) {
			if (worker == null) {
				int i = displayMenu(new String[] { "Login", "Exit", "Help" });
				switch (i) {
				case 1:
					login();
					break;
				case 2:
					System.exit(0);
					break;
				case 3:
					displayHelp();
					break;
				}
			} else {
				int i = displayMenu(new String[] { "Check Out a Textbook", "Check In a Textbook", "Show Patron Details", "Logout", "Help" });
				switch (i) {
				case 1:
					startCheckOutSession();
					break;
				case 2:
					startCheckInSession();
					break;
				case 3: 
					showPatronDetails();
					break;
				case 4:
					logout();
					break;
				case 5:
					displayHelp();
					break;
				}
			}
		}
	}

	private void showPatronDetails() {
		Patron patron = validatePatron();
		if (patron != null) {
			System.out.println("Patron Details:");
			System.out.println(patron);
			List<PatronCopies> pc = controller.getPatronCopies(patron.getPatronId());
			if (!pc.isEmpty()) {
				System.out.println("Patron Copies:");
				for (PatronCopies patronCopy : pc) {
					System.out.println(patronCopy);
					Copy copy = controller.validateCopy(patronCopy.getCopyId());
					System.out.println("\t" + copy);
					System.out.println("\t\t" + controller.getTextbook(copy.getTextbookId()));
				}
			}
		}
	}

	private void logout() {
		System.out.println("\nLogged out.\n");
		worker = null;
	}

	private void login() {
		System.out.print("\nEnter login name:");
		String login = scanner.nextLine();
		System.out.print("Enter password:");
		String password = scanner.nextLine();
		worker = controller.login(login, password);
		if (worker != null) {
			System.out.println("\nWelcome to Textbook Rental Library.\n");
		} else {
			System.out.println("Invalid login name or password.");
		}
	}

	private void displayHelp() {
		System.out.println("\nHELP");
		System.out.println("For demo use login \"worker\" and password \"worker\".");
		System.out.println("Press Enter to continue...");
		scanner.nextLine();
	}

	public void displayAppTitle() {
		System.out.println(title);
	}

	public int displayMenu(String[] arr) {
		int selection = 0;
		boolean displayErrorMsg = false;
		while (true) {
			if (displayErrorMsg) {
				System.out.println("\nInvalid selection, please select option again.");
			}
			System.out.println("\nMENU OPTIONS");
			System.out.println("--------------------------------------------");
			for (int j = 0; j < arr.length; j++) {
				System.out.println("\t" + (j + 1) + ". " + arr[j]);
			}
			System.out.println("--------------------------------------------");
			System.out.print("Select option:");
			try {
				selection = scanner.nextInt();
				scanner.nextLine();
			} catch (InputMismatchException e) {
				scanner.nextLine();
				selection = 0;
			}
			if (0 < selection && selection <= arr.length) {
				break;
			} else {
				displayErrorMsg = true;
			}
		}
		return selection;
	}

	public void startCheckInSession() 
	{
		Patron patron = validatePatron();
		CheckInSession checkIn = new CheckInSession(patron, controller);
		if (patron != null) {
			System.out.println(patron);
			checkIn.start();
			checkIn.checkInCopies();
		}
	}

	public void startCheckOutSession() {
		Patron patron = validatePatron();
		if (patron != null) {
			System.out.println(patron);
			if (controller.canPatronCheckoutCopies(patron)) {
				CheckOutSession checkOut = new CheckOutSession(patron, controller);
				checkOut.start();
				checkOut.checkOutCopies();
			} else {
				System.out.println("You can not check out these copies. Please, return overdue copies.");
			}
		}
	}
	
	public Patron validatePatron() {
		System.out.println("Enter Patron Id:");
		String patronId = scanner.nextLine();
		Patron p = controller.validatePatron(patronId);
		if (p == null) {
			System.out.println("Invalid Patron Id.");
		}
		return p;
	}

}
