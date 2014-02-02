package io.userapp.client;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONWriter;

import io.userapp.client.exceptions.*;
import io.userapp.client.rest.Restful;
import io.userapp.client.rest.RestfulContext;
import io.userapp.client.rest.UserCredentials;
import io.userapp.client.rest.core.HttpResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * Implementation of the UserApp API.
 * https://app.userapp.io/#/docs/
 */
public class UserApp {
	
	/* Configuration object */
	public static class ClientOptions {
		public int version = 1;
		public String appId = null;
		public String token = null;
		public boolean debug = false;
		public boolean secure = true;
		public String baseAddress = "api.userapp.io";
		public boolean throwErrors = true;
		
		public ClientOptions() {}
		
		public ClientOptions(String appId) {
			this(appId, null);
		}
		
		public ClientOptions(String appId, String token) {
			this.appId = appId;
			this.token = token;
		}
	}
	
	/* Representation of an input parameter */
	public static class Parameter {
		String name;
		Object value;
		
		public Parameter(String name, Object value) {
			this.name = name;
			this.value = value;
		}
	}
	
	/* Representation of an input parameter struct */
	public static class Struct {
		ArrayList<UserApp.Parameter> parameters = new ArrayList<UserApp.Parameter>();
		
		public Struct() {}
		public UserApp.Struct parameter(String name, Object value) {
			this.parameters.add(new UserApp.Parameter(name, value));
			return this;
		}
		
		/* Convert to JSON */
		private String toJSON() {
			String result = "";
			
			for (UserApp.Parameter parameter : this.parameters) {
				if (result.length() > 0) {
					result += ",";
				}
				result += UserApp.ObjectToJson(parameter);
			}
			
			return "{" + result + "}";
		}
	}
	
	/* Representation of an input parameter array */
	public static class Array {
		ArrayList<Object> items = new ArrayList<Object>();
		
		public Array(Object ... items) {
			for (Object item : items) {
				this.items.add(item);
			}
		}
		
		/* Convert to JSON */
		private String toJSON() {
			String result = "";
			
			for (Object item : this.items) {
				if (result.length() > 0) {
					result += ",";
				}
				result += UserApp.ObjectToJson(item);
			}
			
			return "[" + result + "]";
		}
	}
	
	/* Representation of a result */
	public static class Result extends HashMap<String, Object> {
		private static final long serialVersionUID = -911210576998047758L;
		public Object result = null;
		
		public Result(HashMap<String, Object> hashMap) {
			super(hashMap);
		}
		
		public UserApp.Result get(Object key) {
			UserApp.Result result = new UserApp.Result(this);
			
			if (this.result != null) {
				if (this.result.getClass().getSimpleName().equalsIgnoreCase("ArrayList")) {
					if (key.getClass().getSimpleName().equalsIgnoreCase("Integer")) {
						result.result = ((ArrayList)this.result).get((int)key);
					} else {
						result.result = null;
					}
				} else {
					result.result = ((HashMap)this.result).get(key);
				}
			} else {
				result.result = super.get(key);
			}
			
			return result;
		}
		
		public boolean exists() {
			return this.result != null;
		}
		
		public ArrayList toArray() {
			if (this.result != null) {
				return (ArrayList) this.result;
			} else {
				return null;
			}
		}
		
		public HashMap<String, Object> toHashMap() {
			if (this.result != null) {
				return (HashMap<String, Object>) this.result;
			} else {
				return (HashMap<String, Object>) this;
			}
		}
		
		public String toString() {
			if (this.result != null) {
				return this.result.toString();
			} else {
				return super.toString();
			}
		}
		
		public int toInteger() {
			if (this.result != null) {
				return (int) this.result;
			} else {
				return 0;
			}
		}
		
		public float toFloat() {
			if (this.result != null) {
				return (float) this.result;
			} else {
				return 0F;
			}
		}
		
		public boolean toBoolean() {
			if (this.result != null) {
				return (boolean) this.result;
			} else {
				return false;
			}
		}
	}

	/* API wrapper class */
	public static class API {
		private UserApp.ClientOptions options;
		RestfulContext restfulContext = new RestfulContext();
		URI serviceUrl;
		private String methodName;
		private ArrayList<UserApp.Parameter> parameters = new ArrayList<UserApp.Parameter>();
		
		public API(String appId) {
			this.setOptions(new UserApp.ClientOptions(appId));
		}
		
		public API(String appId, String token) {
			this.setOptions(new UserApp.ClientOptions(appId, token));
		}
		
		public API(UserApp.ClientOptions options) {
			this.setOptions(options);
		}
		
		public void setOptions(UserApp.ClientOptions options) {
			this.options = options;
			this.restfulContext.setBasicAuthenticationCredentials(new UserCredentials(
				this.options.appId, (this.options.token == null ? "" : this.options.token)
			));
			this.serviceUrl = URI.create(
				String.format("%s://%s/v%d/", (this.options.secure ? "https" : "http"), this.options.baseAddress, this.options.version)
			);
		}
		
		public UserApp.ClientOptions getOptions() {
			return this.options;
		}
		
		/* Set the API method */
		public UserApp.API method(String name) {
			this.methodName = name;
			this.parameters.clear();
			return this;
		}
		
		/* Add an input parameter */
		public UserApp.API parameter(String name, Object value) {
			this.parameters.add(new UserApp.Parameter(name, value));
			return this; 
		}
		
		/* Convert all input parameters to JSON */
		private String toJSON() {
			String result = "";
			
			for (UserApp.Parameter parameter : this.parameters) {
				if (result.length() > 0) {
					result += ",";
				}
				result += UserApp.ObjectToJson(parameter);
			}
			
			return "{" + result + "}";
		}
		
		/* Perform the API call */
		public UserApp.Result call() throws UserAppException, TransportException, ServiceException, InvalidServiceException, InvalidMethodException {
			HttpResponse response = null;
			
	        try {
	        	String endpoint = this.serviceUrl.toString() + this.methodName + (this.options.debug ? "?$debug":"");
	        	String encodedParameters = this.toJSON();
	        	
	        	this.log(String.format("Calling URL '%s' with parameters '%s'", endpoint, encodedParameters));
	        	
				Restful restClient = new Restful(this.restfulContext);
				response = restClient.post(endpoint, encodedParameters);
	        } 
	        catch (Exception exception)
		    {
	        	throw new TransportException(exception.getMessage(), exception);
		    }
	        
			try
	        {
				this.log(String.format("Recieved response '%s'", response.result));
				
				response.result = "{\"result\":" + response.result + "}";
				HashMap<String, Object> json = JsonHelper.getMap(new JSONObject(response.result));
				
				UserApp.Result result = new UserApp.Result(json);
				
				/* Set or remove session token? */
				if (this.methodName == "user.login" && this.options.token == null) {
					this.options.token = result.get("result").get("token").toString();
					this.setOptions(this.options);
				} else if (this.methodName == "user.logout") {
					this.options.token = null;
					this.setOptions(this.options);
				}
				
				/* Check for error */
				if (result.get("result").get("error_code").exists()) {
					Exception generatedException = null;
					String errorCode = result.get("result").get("error_code").toString();
					String message = result.get("result").get("message").toString();
					
					switch (errorCode) {
                        case "INVALID_SERVICE":
                            generatedException = new InvalidServiceException(message);
                            break;
                        case "INVALID_METHOD":
                            generatedException = new InvalidMethodException(message);
                            break;
                        default:
                        	if (this.options.throwErrors) {
                        		generatedException = new ServiceException(errorCode, message);
                        	}
                            break;
                    }
					
					if (generatedException != null) {
						throw generatedException;
					}
				}
				
				return result.get("result");
			}
			catch (UserAppException exception) {
		    	throw exception;
		    }
		    catch (Exception exception) {
		    	throw new UserAppException(exception.getMessage(), exception);
		    }
		}
		
		private void log(String message) {
			if (this.options.debug) {
				System.out.println("[UserApp Debug]: " + message);
			}
		}
	}
	
	private static String ObjectToJson(Object obj) {
		String result;
		
		if (obj == null) {
			return "null";
		}
		
		switch (obj.getClass().getSimpleName()) {
			case "Struct":
				result = ((UserApp.Struct) obj).toJSON();
				break;
			case "Array":
				result = ((UserApp.Array) obj).toJSON();
				break;
			case "Parameter":
				result = String.format("%s:%s", ObjectToJson(((UserApp.Parameter) obj).name), ObjectToJson(((UserApp.Parameter) obj).value));
				break;
			case "String":
				result = JsonHelper.quote((String)obj);
				break;
			default:
				result = obj.toString();
				break;
		}
		
		return result;
	}
	
}