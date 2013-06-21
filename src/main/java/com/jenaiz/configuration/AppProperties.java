package com.jenaiz.configuration;

import com.jenaiz.exceptions.TechnicalException;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class AppProperties {

    private static final Logger logger = Logger.getLogger(AppProperties.class);

    private static final String CONFIG_PROPERTIES = "config.properties";

    private static final String FACEBOOK_SCOPE = "facebook.scope";

    private static final String FACEBOOK_CANVAS_URL = "facebook.canvas_url";

    private static final String FACEBOOK_REDIRECT_URL = "facebook.redirect_url";

    private static final String FACEBOOK_APP_ID = "facebook.app_id";

    private static final String FACEBOOK_APP_SECRET = "facebook.app_secret";

    private static final String FACEBOOK_DIALOG_OAUTH = "facebook.dialog_oauth";

    private static final String FACEBOOK_ACCESS_TOKEN = "facebook.access_token";

    private Properties appProperties = null;

    public AppProperties() {
	if (logger.isInfoEnabled())
	    logger.info("Reading the Configuration file...");

	try {
	    appProperties = read(CONFIG_PROPERTIES);
	} catch (TechnicalException e) {
	    // TODO change this behavior
	    logger.error("Error reading properties");
	}
    }

    public Properties getConfigProperties() {
	return appProperties;
    }

    protected Properties read(final String filename) throws TechnicalException {
	final Properties prop = new Properties();

	try {
	    prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename));

	} catch (IOException ex) {
	    throw new TechnicalException("Error reading properties file");
	}

	return prop;
    }

    public String getFacebookScope() {
	return appProperties.getProperty(FACEBOOK_SCOPE);
    }

    public String getFacebookCanvasUrl() {
	return appProperties.getProperty(FACEBOOK_CANVAS_URL);
    }

    public String getFacebookRedirectUrl() {
	return appProperties.getProperty(FACEBOOK_REDIRECT_URL);
    }

    public String getFacebookAppId() {
	return appProperties.getProperty(FACEBOOK_APP_ID);
    }

    public String getFacebookAppSecret() {
	return appProperties.getProperty(FACEBOOK_APP_SECRET);
    }

    public String getFacebookDialogOauth() {
	return appProperties.getProperty(FACEBOOK_DIALOG_OAUTH);
    }

    public String getFacebookAccessToken() {
	return appProperties.getProperty(FACEBOOK_ACCESS_TOKEN);
    }
}
