package com.jenaiz.controllers;

import com.jenaiz.configuration.AppProperties;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/login")
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class);

    private static AppProperties appProperties = new AppProperties();

    private static final String SCOPE = appProperties.getFacebookScope();

    private static final String REDIRECT_URL = appProperties.getFacebookRedirectUrl();

    private static final String CANVAS_URL = appProperties.getFacebookCanvasUrl();

    private static final String APP_ID = appProperties.getFacebookAppId();

    private static final String APP_SECRET = appProperties.getFacebookAppSecret();

    private static final String DIALOG_OAUTH = appProperties.getFacebookDialogOauth();

    private static final String ACCESS_TOKEN = appProperties.getFacebookAccessToken();

    @RequestMapping(value = "/", params = "signed_request", method = RequestMethod.POST)
    public ModelAndView secondTime(@RequestParam("signed_request") String signed_request, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	ModelAndView mav = new ModelAndView("game");
	try {
	    Base64 base64 = new Base64(true);

	    String[] signedRequest = request.getParameter("signed_request").split("\\.", 2);

	    String sig = new String(base64.decode(signedRequest[0].getBytes("UTF-8")));
	    String payload = signedRequest[1];
	    payload = payload.replace("-", "+").replace("_", "/").trim();

	    String base64Converted = new String(base64.decode(payload.getBytes("UTF-8")));
	    logger.info(base64Converted);
	    JSONObject data = (JSONObject) JSONSerializer.toJSON(base64Converted);

	    if (!data.getString("algorithm").equals("HMAC-SHA256")) {
		logger.error("There was some error with the algorithm of the JSON content!(Algorithm: "
			+ data.getString("algorithm"));
		mav.setViewName("error");
		return mav;
	    }

	    // check if data is signed correctly
	    if (!hmacSHA256(signedRequest[1], APP_SECRET).equals(sig)) {
		// signature is not correct, possibly the data was tampered with
		System.out.println("ERROR !!!!!!!!");
		return null;
	    }

	    if (!data.has("user_id") || !data.has("oauth_token")) {
		String redirectUrl = createRedirectUrl();

		response.getWriter().println(redirectUrl);

		return null;
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    mav.setViewName("error");
	}
	return mav;
    }

    private String createRedirectUrl() throws UnsupportedEncodingException {
	StringBuilder sb = new StringBuilder();

	sb.append("<script>");
	sb.append("top.location.href = \"");
	sb.append(DIALOG_OAUTH);
	sb.append("?client_id=");
	sb.append(APP_ID);
	sb.append("&redirect_uri=");
	sb.append(URLEncoder.encode(REDIRECT_URL, "UTF-8"));
	sb.append("&scope=");
	sb.append(SCOPE);
	sb.append("\"");
	sb.append("</script>");

	return sb.toString();
    }

    @RequestMapping(value = "/", params = "code", method = RequestMethod.GET)
    public void accessCodePost(ModelMap model, @RequestParam("code") String code, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	String url = ACCESS_TOKEN + "?client_id=" + APP_ID + "&redirect_uri=" + REDIRECT_URL + "&code=" + code
		+ "&client_secret=" + APP_SECRET;

	String out = callFacebookAPI(url);
	String[] parameters = out.split("&");
	String accessToken = readValue(parameters[0]);
	long expires = new Long(readValue(parameters[1]));

	final FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
	User user = facebookClient.fetchObject("me", User.class);

	// TODO do something !!

	response.sendRedirect(CANVAS_URL);
    }

    private String readValue(final String input) {
	final String[] params = input.split("=");

	return params[1];
    }

    private String callFacebookAPI(String url) throws IOException, MalformedURLException {
	return new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
    }

    @RequestMapping(value = "/", params = "error_reason", method = RequestMethod.GET)
    @ResponseBody
    public void error(@RequestParam("error_reason") String errorReason, @RequestParam("error") String error,
	    @RequestParam("error_description") String description, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	try {
	    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, description);
	    System.out.println(errorReason);
	    System.out.println(error);
	    System.out.println(description);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    // HmacSHA256 implementation
    private String hmacSHA256(String data, String key) throws Exception {
	SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
	Mac mac = Mac.getInstance("HmacSHA256");
	mac.init(secretKey);
	byte[] hmacData = mac.doFinal(data.getBytes("UTF-8"));
	return new String(hmacData);
    }
}
