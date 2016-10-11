package com.mZone.epro.account.twitter;

public class TwitterConstants {

	//twitter application oauth api key
	public static final String CONSUMER_KEY = "mhRxN9ndNu5jm9fXnevK4qDm7";
	public static final String CONSUMER_SECRET = "QrtbUrH8bpzxK4SlUptBiMIRIBNj61Y81JrYe7pqPF9cwtWKSr";

	//using for app preference
	public static final String ACCOUNT_NAME = "twitter_oauth";
	public static final String ACCOUNT_ACCESS_TOKEN_SECRET = "oauth_token_secret";
	public static final String ACCOUNT_ACCESS_TOKEN = "oauth_token";
	
	//using for twitter oauth
	public static final String IEXTRA_AUTH_URL = "auth_url";
	public static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	public static final String IEXTRA_OAUTH_TOKEN = "oauth_token";
	
	//parameter for requesting access token in webview
	public static final int TWITTER_AUTH_REQUEST_CODE = 1000;
	public static final String CALLBACK_URL = "oauth://twitterlog";
	
}
