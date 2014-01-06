package eu.anasta.bm.clearcalendar;

import java.util.Scanner;

import net.bluemind.core.api.AccessToken;
import net.bluemind.core.api.fault.AuthFault;
import net.bluemind.core.api.fault.ServerFault;
import net.bluemind.core.api.user.User;
import net.bluemind.core.client.calendar.CalendarClient;
import net.bluemind.core.client.locators.CalendarLocator;

public class Application {
	private CalendarClient calClient = null;
	private String host;
	private AccessToken token;
	private String password;
	AccessToken asUser;
	private String user;
	Scanner in = new Scanner(System.in);

	public static void main(String[] args) {
		Application app = new Application();
		try {
			app.readData();
			app.openWebservice();
			app.deleteEvent();
			app.logoff();
		} catch (AuthFault e) {
			System.out.println("Authentifiaction Fail");
			e.printStackTrace();
		} catch (ServerFault e) {
			System.out.println("Server fault");
			e.printStackTrace();
		}
	}

	private void readData() {
		System.out.println("hostname?");
		host = in.nextLine();

		System.out.println("pass for admin0 on " + host + "?");
		password = in.nextLine();
		System.out.println("full login user to clear?");
		user = in.nextLine();
		
	}
	private void logoff(){
		calClient.logout(asUser);
		calClient.logout(token);
		System.exit(0);
	}
	
	private void openWebservice() throws AuthFault, ServerFault {
		CalendarLocator cl = new CalendarLocator();
		// via l'url du serveur Blue Mind
		String url = "https://" + host + "/services";
		this.calClient = cl.locate(url);
		token = calClient.login("admin0@global.virt", password, "BM Notifier");
		asUser = calClient.sudo(token, user);
		// start session
	}


	private void deleteEvent() throws AuthFault, ServerFault {

		User user = calClient.findMe(asUser);
		System.out.println("reset calendar of " + user.getDisplayName() + "? (yes/no)");
		String response =in.nextLine();
		boolean yes = ("yes".equals(response) || "y".equals(response) || "Y".equals(response)  || "Yes".equals(response));
		if (yes){
		calClient.resetCalendar(asUser, user.getCalendarId());
		System.out.println("calendar of " + user.getDisplayName() + " is now empty");
		}
		else{
			System.out.println("goodbye");	
		}
	}

}
