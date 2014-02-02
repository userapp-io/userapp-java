package io.userapp.client.demo; 

import java.util.ArrayList;

import io.userapp.client.UserApp;
import io.userapp.client.exceptions.*;

/*
 * Demo of the UserApp API Java Client.
 */
public class Demo {
	
	public static void main(String[] args) {
		try {
			/*
			 * Initiate the UserApp Java Client with your App Id
			 * https://help.userapp.io/customer/portal/articles/1322336-how-do-i-find-my-app-id-
			 */
			/*UserApp.ClientOptions options = new UserApp.ClientOptions();
			options.appId = "YOUR-USERAPP-APP-ID";
			options.debug = true;
			options.throwErrors = false;
			UserApp.API api = new UserApp.API(options);*/
			
			UserApp.API api = new UserApp.API("YOUR-USERAPP-APP-ID");
			
			/*
			 * Sign up a new user
			 * https://app.userapp.io/#/docs/user/#save
			 */
			UserApp.Result result = api.method("user.save")
				.parameter("login", "johndoe81")
				.parameter("password", "iwasfirst!111")
				.parameter("first_name", "John")
				.call();
			
			/*
			 * Log in a user with login and password
			 * https://app.userapp.io/#/docs/user/#login
			 */
			result = api.method("user.login")
				.parameter("login", "johndoe81")
				.parameter("password", "iwasfirst!111")
				.call();
			
			System.out.println("Logged in. Session Token: " + result.get("token").toString());
			
			/* 
			 * Get the logged in user with user.get()
			 * https://app.userapp.io/#/docs/user/#get
			 */
			result = api.method("user.get")
				.parameter("user_id", "self")
				.call();
			
			System.out.println("Welcome, " + result.get(0).get("first_name").toString() + "!");
			
			/* 
			 * Search invoices
			 * https://app.userapp.io/#/docs/invoice/#search
			 */
			ArrayList invoices = api.method("user.invoice.search")
				.parameter("user_id", "self")
				.parameter("sort", new UserApp.Struct()
					.parameter("created_at", "asc")
					.parameter("updated_at", "asc")
				)
				.parameter("fields", new UserApp.Array(
					"invoice_id", "created_at"
				))
				.call().get("items").toArray();
			
			/* 
			 * Log out the user
			 * https://app.userapp.io/#/docs/user/#logout
			 */
			api.method("user.logout").call();
			
			System.out.println("Logged out.");
		}
		catch (InvalidMethodException exception) {
			System.out.println("The method you are trying to call does not exist: " + exception.getMessage());
		}
		catch (InvalidServiceException exception) {
			System.out.println("The service you are trying to call does not exist: " + exception.getMessage());
		}
		catch (ServiceException exception) {
			System.out.println("The API responded with an error: " + exception.getMessage());
		}
		catch (TransportException exception) {
			System.out.println("Something went wrong with the connection: " + exception.getMessage());
		}
		catch(UserAppException exception) {
			System.out.println("General exception: " + exception.getMessage());
		}
	}
	
}