# Java library for UserApp

## Getting started

### Finding your App Id and Token

If you don't have a UserApp account, you need to [create one](https://app.userapp.io/#/sign-up/).

* **App Id**: The App Id identifies your app. After you have logged in, you should see your `App Id` instantly. If you're having trouble finding it, [follow this guide](https://help.userapp.io/customer/portal/articles/1322336-how-do-i-find-my-app-id-).

*  **Token**: A token authenticates a user on your app. If you want to create a token for your logged in user, [follow this guide](https://help.userapp.io/customer/portal/articles/1364103-how-do-i-create-an-api-token-). If you want to authenticate using a username/password, you can acquire your token by calling `api.method("user.login").parameter("login", "username").parameter("password", "password").call();`

### Referencing the library

Add the `.jar` file to your libraries and import it with the following lines:

    import io.userapp.client.UserApp;
    import io.userapp.client.exceptions.*;

### Creating your first client
 
    UserApp.API api = new UserApp.API("YOUR-USERAPP-APP-ID");

#### Additional ways of creating a client

If you want to create a client with additional options the easiest way is to pass a `UserApp.ClientOptions` with the options as shown below.

    UserApp.ClientOptions options = new UserApp.ClientOptions();
    options.appId = "YOUR-USERAPP-APP-ID";
    options.debug = true;
    options.throwErrors = false;
    
    UserApp.API api = new UserApp.API(options);

If you pass a `String` into the constructor the first argument will be treated as the `App Id`, the second as the `Token`.

    UserApp.API api = new UserApp.API("YOUR-USERAPP-APP-ID", "AN-API-TOKEN");

## Calling services and methods

This client has no hard-coded API definitions built into it. It merly acts as a proxy which means that you'll never have to update the client once new API methods are released or changed. If you want to call a method all you have to do is look at the [API documentation](https://app.userapp.io/#/docs/) and follow the convention below:

    UserApp.Result result = api.method("service.method")
        .parameter("name", "value")
        .parameter("another-name", "another-value")
        .call();
    	
For deeper structures with objects and arrays, use `UserApp.Struct`and `UserApp.Array`:

    UserApp.Result result = api.method("service.method")
        .parameter("name", "value")
        .parameter("struct", new UserApp.Struct()
            .parameter("key", "value")
            .parameter("key2", 2)
        )
        .parameter("array", new UserApp.Array(
            "A", "B", 1.3
        ))
        .call();

#### Some examples

The API method [`user.login`](https://app.userapp.io/#/docs/user/#login) and its arguments `login` and `password` translates to:

    UserApp.Result loginResult = api.method("user.login")
        .parameter("login", "test")
        .parameter("password", "test")
        .call();

The API method [`user.invoice.search`](https://app.userapp.io/#/docs/invoice/#search) and its argument `user_id` translates to:
    
    ArrayList invoices = api.method("user.invoice.search")
        .parameter("user_id", "test123")
        .call()
        .get("items")
        .toArray();

The API method [`property.save`](https://app.userapp.io/#/docs/property/#save) and its arguments `name`, `type` and `default_value` translates to:

    UserApp.Result property = api.method("property.save")
        .parameter("name", "my new property")
        .parameter("type", "boolean")
        .parameter("default_value", true)
        .call();
	
The API [`user.logout`](https://app.userapp.io/#/docs/user/#logout) without any arguments translates to:

    api.method("user.logout").call();

## Configuration

Options determine the configuration of a client.

### Available options

* **Version** (`version`): Version of the API to call against. Default `1`.
* **App Id** (`appId`): App to authenticate against. Default `null`.
* **Token** (`token`): Token to authenticate with. Default `null`.
* **Debug mode** (`debug`): Log steps performed when sending/recieving data from UserApp. Default: `false`.
* **Secure mode** (`secure`): Call the API using HTTPS. Default: `true`.
* **Base address** (`baseAddress`): The address to call against. Default: `api.userapp.io`.
* **Throw errors** (`throwErrors`): Whether or not to throw an exception when response is an error. I.e. result `{"error_code":"SOME_ERROR","message":"Some message"}` results in an exception of type `ServiceException`.

### Setting options

The easiest way to set options is to do it in the constructor when creating a new client.

    UserApp.ClientOptions options = new UserApp.ClientOptions();
    options.appId = "YOUR-USERAPP-APP-ID";
    options.debug = true;
    options.throwErrors = false;
    
    UserApp.API api = new UserApp.API(options);

If you want to set an option after the client has been created you can do it as shown below.

    api.setOptions(options);

## Example code

A more detailed set of examples can be found in [/src/io/userapp/client/demo/Demo.java](https://github.com/userapp-io/userapp-java/blob/master/src/io/userapp/client/demo/Demo.java).

### Example code (sign up a new user)

    UserApp.API api = new UserApp.API("YOUR-USERAPP-APP-ID");

    UserApp.Result result = api.method("user.save")
        .parameter("login", "johndoe81")
        .parameter("password", "iwasfirst!111")
        .parameter("first_name", "John")
        .call();

### Example code (logging in and updating a user)

    UserApp.API api = new UserApp.API("YOUR-USERAPP-APP-ID");
    
    result = api.method("user.login")
        .parameter("login", "johndoe81")
        .parameter("password", "iwasfirst!111")
        .call();
    
    UserApp.Result result = api.method("user.save")
        .parameter("user_id", "self")
        .parameter("first_name", "John")
        .parameter("last_name", "Doe")
        .call();

    api.method("user.logout").call();

### Example code (finding a specific user)

    UserApp.API api = new UserApp.API("YOUR-USERAPP-APP-ID", "AN-API-TOKEN");

    UserApp.Result searchResult = api.method("user.search")
        .parameter("filters", new UserApp.Struct()
            .parameter("query", "*bob*")
        )
        .parameter("sort", new UserApp.Struct()
            .parameter("created_at", "desc")
        )
        .call();

    ArrayList items = searchResult.get("items").toArray();

## Versioning

If you want to configure the client to call a specific API version you can do it by setting the `version` option. If no version is set it will automatically default to `1`.

## Error handling

### Debugging

Sometimes to debug an API error it's important to see what is being sent/recieved from the calls that one make to understand the underlying reason. If you're interested in seeing these logs, you can set the client option `debug` to `true`, and the logs will be sent to the console.

	UserApp.ClientOptions options = api.getOptions();
	options.debug = true;
	
	api.setOptions(options);

### Catching errors

When the option `throwErrors` is set to `true` (default) the client will automatically throw a `ServiceException` exception when a call results in an error. I.e.

    try {
        api.User.Save(userId: "invalid user id");
    }
    catch (ServiceException exception) {
        switch(exception.getErrorCode()) {
            // Handle specific error
            case "INVALID_ARGUMENT_USER_ID":
                throw new Exception("User does not exist");
            default:
                throw new Exception(exception.getMessage());
        }
    }

Setting `throwErrors` to `false` is more of a way to tell the client to be silent. This will not throw any service specific exceptions. Though, it might throw a `UserAppException`, `InvalidServiceException` or `InvalidMethodException`.

    UserApp.Result result = api.method("user.save")
        .parameter("user_id", "invalid user id")
        .call()
        
    if (result.get("error_code").exists()) {
        if (result.get("error_code").toString().equals("INVALID_ARGUMENT_USER_ID")) {
            // Handle specific error
        }
    }

## Solving issues

### See what is being sent to/from UserApp

1. Set the client option `debug` to `true` (see section *options* for more details on setting client options).
2. Like above, set the option `throwErrors` to `false`. This disables any error exceptions (`ServiceException`) being thrown.
3. Make the API calls that you want to debug. E.g. `api.method("user.login").parameter("login", "test").call();`.
4. Check the console! See the section `Debugging`.
5. Stuck? Send the output to [support@userapp.io](mailto:support@userapp.io) and we'll help you out.

## Special cases

Even though this client works as a proxy and there are no hard-coded API definitions built into it, there are still a few tweaks that are API specific.

#### Calling API `user.login` will automatically set the client token

In other words:

    UserApp.Result loginResult = api.method("user.login")
        .parameter("login", "test")
        .parameter("password", "test")
        .call();

Is exactly the same as:
	
    UserApp.Result loginResult = api.method("user.login")
        .parameter("login", "test")
        .parameter("password", "test")
        .call();
        
    UserApp.ClientOptions options = api.getOptions();
    options.token = loginResult.get("token").toString();
    api.setOptions(options);

#### Calling API `user.logout` will automatically remove the client token

In other words:

    api.method("user.logout").call();

Is exactly the same as:
	
    api.method("user.logout").call();

    UserApp.ClientOptions options = api.getOptions();
    options.token = null;
    api.setOptions(options);

## Dependencies

* [org.json](http://json.org/java/)

## License

MIT - For more details, see LICENSE.

