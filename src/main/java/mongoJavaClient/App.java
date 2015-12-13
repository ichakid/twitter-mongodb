package mongoJavaClient;
import java.util.Scanner;

public class App {

	public static void main(String[] args) {

    	String username = "";
    	boolean login = false;
    	Querying twitter = new Querying();
    	
    	Scanner in = new Scanner(System.in);
    	System.out.println("Login (1) or Register (2)?");
    	int choice = in.nextInt();
    	System.out.println("Input username:password = ");
    	String inputs = in.next();
    	
    	String input[] = inputs.split(":");
    	username = input[0];
    	String password = input[1];
    	if (choice == 1) {
    		login = twitter.login(username, password);
    	} else if (choice == 2) {
    		twitter.register(username, password);
    		login = true;
    	}
    	if (!login) {
    		System.out.println ("Username or password doesn't match.");
    	}
    	while (login) {    		
    		String command = in.nextLine();
    		if (!command.isEmpty()) {
				String[] split = command.split("\\s+");
				switch (split[0]){
					case "follow" :
						if (!twitter.isUserFollows(username, split[1])) {
							twitter.follow(username, split[1]);
							System.out.println("You are now following " + split[1]);
						} else
							System.out.println("You already follow " + split[1]);
						break;
					case "timeline" :
						twitter.showTweets(username, Querying.TIMELINE);
						break;
					case "userline" :
						twitter.showTweets(split[1], Querying.USERLINE);
						break;
					case "logout" :
						login = false;
						break;
					default:
						twitter.tweet(username, command);
						break;
				}
    		}
    	}
    	System.out.println("Terminating...");
    	twitter.close();
	}

}
