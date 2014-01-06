package eu.anasta.bm;

import java.util.List;
import java.util.Scanner;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import net.bluemind.core.api.AccessToken;
import net.bluemind.core.api.ResultList;
import net.bluemind.core.api.contact.FolderQuery;
import net.bluemind.core.api.fault.AuthFault;
import net.bluemind.core.api.fault.ServerFault;
import net.bluemind.core.api.system.Domain;
import net.bluemind.core.api.system.DomainQuery;
import net.bluemind.core.api.system.Host;
import net.bluemind.core.api.system.HostQuery;
import net.bluemind.core.api.user.User;
import net.bluemind.core.api.user.UserQuery;
import net.bluemind.core.client.book.BookClient;
import net.bluemind.core.client.calendar.CalendarClient;
import net.bluemind.core.client.group.GroupClient;
import net.bluemind.core.client.locators.AddressBookLocator;
import net.bluemind.core.client.locators.CalendarLocator;
import net.bluemind.core.client.locators.GroupLocator;
import net.bluemind.core.client.locators.MailLocator;
import net.bluemind.core.client.locators.MailshareLocator;
import net.bluemind.core.client.locators.SystemLocator;
import net.bluemind.core.client.locators.UserLocator;
import net.bluemind.core.client.mail.MailClient;
import net.bluemind.core.client.mailshare.MailshareClient;
import net.bluemind.core.client.system.SystemClient;
import net.bluemind.core.client.user.UserClient;


public class OneShot {

	String sInbox = "INBOX";
	String dInbox = "INBOX";
	String sDraft = "Brouillons";
	String sJunks = "Pourriel";
	String sJunksno = "non-pourriel-confirme";
	String sJunks2 = "pourriel-confirme";
	String sSent = "Objets envoy�s";
	String sSent2 = "Envoy�s";
	String sTrash = "Corbeille";
	String dDraft = "Drafts";
	String dJunks = "Junk";
	String dSent = "Sent";
	String dTrash = "Trash";

	private CalendarClient sCalClient = null;
	private MailClient sMailClient = null;
	private BookClient sBookClient = null;
	private UserClient sUserClient = null;
	private GroupClient sGroupClient = null;
	private MailshareClient sMailShareClient = null;

	private CalendarClient dCalClient = null;
	private MailClient dMailClient = null;
	private BookClient dBookClient = null;
	private UserClient dUserClient = null;
	private GroupClient dGroupClient = null;
	private MailshareClient dMailShareClient = null;

	int start = 1;
	int end = -1;

	Session session = null;
	Store sourcestore = null;
	Store deststore = null;
	Folder sourcefolder = null;
	Folder destfolder = null;
	String sHost = null;
	String dHost = null;

	String sHostName = null;
	String dHostName = null;

	String sadmin0Pass = null;
	String dadmin0Pass = null;
	String sadmindPass = null;
	String dadmindPass = null;

	private String domaine;
	private SystemClient ssystemClient = null;
	private SystemClient dsystemClient = null;

	public static void main(String[] args) throws Exception {
		OneShot app = new OneShot();
//		app.clearFolder();
		
		// System.out.println("�a marche");
	}

	public void clearFolder(){
		Scanner in = new Scanner(System.in);
		System.out.println("Domaine?");
		domaine = in.nextLine();


		System.out.println("source hostname?");
		sHostName = in.nextLine();
		sHost = sHostName + "." + domaine;
		System.out.println("pass for admin0 on " + sHost + "?");
		sadmin0Pass = in.nextLine();
		System.out.println("pass for admin@" + domaine + " on " + sHost + "?");
		sadmindPass = in.nextLine();

		System.out.println("user ?");
		String user = in.nextLine();

		System.out.println("folder to clear?");
		String folder= in.nextLine();
		clearFolder(user, folder);
	}


	private String exportCalendar(String user, CalendarClient calClient,
			String password) {
		AccessToken token = calClient.login("admin0@global.virt", password,
				"Migration");
		try {
			AccessToken asUser = calClient.sudo(token, user);
			return calClient.exportICS(asUser, null);
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			calClient.logout(token);
		}
		return null;

	}

	private void importCalendar(String user, CalendarClient calClient,
			String password, String ics) {
		AccessToken token = calClient.login("admin0@global.virt", password,
				"Migration");
		try {
			AccessToken asUser = calClient.sudo(token, user);
			calClient.importICS(asUser, ics);
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			calClient.logout(token);
		}

	}

	private String exportFolder(String user, BookClient bookClient,
			String password, net.bluemind.core.api.contact.Folder f) {
		AccessToken token = bookClient.login("admin0@global.virt", password,
				"Migration");
		try {
			AccessToken asUser = bookClient.sudo(token, user);
			return bookClient.exportFolderVcards(asUser, f.getId());
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bookClient.logout(token);
		}
		return null;
	}

	private void importFolder(String user, BookClient bookClient,
			String password, net.bluemind.core.api.contact.Folder f,
			String vcard) {
		AccessToken token = bookClient.login("admin0@global.virt", password,
				"Migration");
		try {
			AccessToken asUser = bookClient.sudo(token, user);
			if (vcard != null) { // clear null value
				vcard = vcard.replaceAll(",null", "");
				vcard = vcard.replaceAll("null,", "");
				vcard = vcard.replaceAll("null ", "");
				vcard = vcard.replaceAll(" null", "");
				vcard = vcard.replaceAll(";null", ";");
				vcard = vcard.replaceAll("null;", ";");
			}
			bookClient.importAllVCards(asUser, f, vcard);
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bookClient.logout(token);
		}
		return;
	}

	private net.bluemind.core.api.contact.Folder findFolder(String user,
			BookClient bookClient, String password, String folderName) {
		AccessToken token = bookClient.login("admin0@global.virt", password,
				"Migration");
		try {
			AccessToken asUser = bookClient.sudo(token, user);
			FolderQuery q = new FolderQuery();
			q.setName(folderName);
			q.setOwner(asUser.getUserId());
			List<net.bluemind.core.api.contact.Folder> list = bookClient
					.findFolders(asUser, q);
			if (list.size() == 1) {
				return list.get(0);
			}
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bookClient.logout(token);
		}
		return null;
	}

	private List<net.bluemind.core.api.contact.Folder> getAllFolder(
			String user, BookClient bookClient, String password) {
		AccessToken token = bookClient.login("admin0@global.virt", password,
				"Migration");
		try {
			AccessToken asUser = bookClient.sudo(token, user);
			return bookClient.findFolders(asUser);
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bookClient.logout(token);
		}
		return null;

	}

	private void updatepass(User dUser, UserClient userClient, String password,
			String savePass) {
		AccessToken token = userClient.login("admin0@global.virt", password,
				"Migration");

		try {

			dUser.setPassword("12345");
			dUser.setPasswordEncrypted(false);
			userClient.update(token, dUser);
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			userClient.logout(token);
		}

	}

	private List<User> getUser(UserClient userClient, String password) {
		AccessToken token = userClient.login("admin0@global.virt", password,
				"Migration");
		UserQuery q = new UserQuery();
		q.setDomainName("anasta.eu");
		ResultList<User> ul;
		try {
			ul = userClient.find(token, q);
			if (ul.size() > 0) {
				return ul;
			}
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			userClient.logout(token);
		}
		return null;
	}

	private User getUser(String user, UserClient userClient, String password) {
		AccessToken token = userClient.login("admin0@global.virt", password,
				"Migration");
		UserQuery q = new UserQuery();
		q.setDomainName("anasta.eu");
		q.setLogin(user);
		ResultList<User> ul;
		try {
			ul = userClient.find(token, q);
			if (ul.size() == 1) {
				return ul.get(0);
			}
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			userClient.logout(token);
		}
		return null;
	}

	private Domain getDomain(SystemClient system, String password) {
		AccessToken token = system.login("admin0@global.virt", password,
				"Migration");
		try {
			DomainQuery q = new DomainQuery();
			q.setName(domaine);
			ResultList<Domain> ul = system.find(token, q);
			if (ul.size() == 1) {
				return ul.get(0);
			}
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			system.logout(token);
		}
		return null;
	}

	private Host getHost(SystemClient system, String password, String host) {
		AccessToken token = system.login("admin0@global.virt", password,
				"Migration");
		try {
			HostQuery q = new HostQuery();
			q.setNamePrefix(host);
			ResultList<Host> ul = system.find(token, q);
			if (ul.size() == 1) {
				return ul.get(0);
			}
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			system.logout(token);
		}
		return null;
	}

	private User createUser(User user, UserClient userClient, String password,
			Domain domain, Host host) {
		AccessToken token = userClient.login("admin0@global.virt", password,
				"Migration");
		try {
			user.setDomain(domain);
			user.setMailServer(host);
			user.setPassword("12345");
			user.setPasswordEncrypted(false);

			return userClient.create(token, user);

		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			userClient.logout(token);
		}
		return null;
	}

	private String getcryptPass(User user, UserClient userClient,
			String password) {
		AccessToken token = userClient.login("admin0@global.virt", password,
				"Migration");
		try {
			return userClient.getEncryptPassword(token, user.getId());
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			userClient.logout(token);
		}
		return null;
	}

	private User deleteUser(User user, UserClient userClient, String password) {
		AccessToken token = userClient.login("admin0@global.virt", password,
				"Migration");
		try {
			return userClient.deleteUser(token, user.getId());
		} catch (AuthFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			userClient.logout(token);
		}
		return null;
	}

	private void initlocator() {

		CalendarLocator cl;
		MailLocator ml;
		AddressBookLocator bl;
		UserLocator ul;
		// AccessToken stoken = cal.login("admin0@global.virt", key, app);

		// build webservice locator
		String oldUrl = "https://" + sHost + "/services";
		cl = new CalendarLocator();
		// via l'url du serveur Blue Mind
		this.sCalClient = cl.locate(oldUrl);
		this.dCalClient = cl.locate(newUrl);

		ml = new MailLocator();
		this.sMailClient = ml.locate(oldUrl);
		this.dMailClient = ml.locate(newUrl);
		bl = new AddressBookLocator();
		this.sBookClient = bl.locate(oldUrl);
		this.dBookClient = bl.locate(newUrl);

		this.sUserClient = UserLocator.locate(oldUrl);
		this.dUserClient = UserLocator.locate(newUrl);

		GroupLocator group = new GroupLocator();
		this.sGroupClient = group.locate(oldUrl);
		this.dGroupClient = group.locate(newUrl);

		SystemLocator sys = new SystemLocator();
		this.ssystemClient = sys.locate(oldUrl);
		this.dsystemClient = sys.locate(newUrl);
		// via l'url du serveur Blue Mind

		MailshareLocator mailshare = new MailshareLocator();
		this.sMailShareClient = mailshare.locate(oldUrl);
		this.dMailShareClient = mailshare.locate(newUrl);

	}

	private void copiPublicFolder(Folder f) {
		try {
			f.open(Folder.READ_WRITE);

			if (f.getMessageCount() > 0) {
				destfolder = createFolder(deststore, f.getFullName());
			}

			int count = f.getMessageCount();
			if (count == 0) { // No messages in the source folder
				System.out.println(f.getName() + " is empty");
				// Close folder, store and return
			}

			// Open destination folder, create if reqd
			Folder dfolder = destfolder;
			if (!dfolder.exists())
				dfolder.create(Folder.HOLDS_MESSAGES);

			end = count;

			Message[] msgs = f.getMessages(start, end);
			System.out.println("Moving " + msgs.length + " messages");

			if (msgs.length != 0) {
				f.copyMessages(msgs, dfolder);
				// System.out.println("test message not copied");
			}
			f.close(true);
			Folder[] fl = f.list();
			for (Folder fold : fl) {
				copiesubFolder(fold);

			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void copiesubFolder(Folder f) throws Exception {

		f.open(Folder.READ_WRITE);

		if (f.getMessageCount() > 0) {
			destfolder = createFolder(deststore, f.getFullName());
		}

		int count = f.getMessageCount();
		if (count == 0) { // No messages in the source folder
			System.out.println(f.getName() + " is empty");
			// Close folder, store and return
		}
		end = count;
		Message[] msgs = f.getMessages(start, end);
		System.out.println("Moving " + msgs.length + " messages");

		if (msgs.length != 0) {
			f.copyMessages(msgs, destfolder);
			// System.out.println("test message not copied");
		}
		f.close(true);
		Folder[] fl = f.list();
		for (Folder fold : fl) {
			copiesubFolder(fold);

		}
	}

	private Folder createFolder(Store deststore, String folderName) {
		boolean isCreated = true;
		Folder testFolder = null;
		try {

			testFolder = deststore.getFolder(folderName);
			if (!testFolder.exists()) {
				isCreated = testFolder.create(Folder.HOLDS_MESSAGES);
				System.out.println("created: " + isCreated);
			} else {
				System.out.println("folder OK");
			}
		} catch (Exception e) {
			System.out.println("Error creating folder: " + e.getMessage());
			isCreated = false;
		}
		return testFolder;
	}
}
