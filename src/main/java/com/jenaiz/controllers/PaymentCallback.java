package com.jenaiz.controllers;

import com.jenaiz.configuration.AppProperties;
import com.jenaiz.json.Content;
import com.jenaiz.json.Response;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class PaymentCallback {

    private static final Logger logger = Logger.getLogger(PaymentCallback.class);

    private static AppProperties appProperties = new AppProperties();

    private static final String APP_SECRET = appProperties.getFacebookAppSecret();

    @RequestMapping(value = "/payment", method = RequestMethod.GET)
    public @ResponseBody
    Response firstTime(String signed_request, HttpServletRequest request, HttpServletResponse response) {
	Response result = new Response();

	Content content = new Content();

	content.setProduct("http://arcane-badlands-1437.herokuapp.com/product.html");
	content.setAmount(0);
	content.setCurrency("USD");

	result.setContent(content);
	result.setMethod("payments_get_item_price");

	return result;
    }

    // @RequestMapping(value = "/payment", params = "signed_request", method =
    // RequestMethod.POST)
    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public ModelAndView secondTime(String signed_request, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	ModelAndView result = new ModelAndView();

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
	    result.setViewName("error");
	    return result;
	}

	// check if data is signed correctly
	if (!hmacSHA256(signedRequest[1], APP_SECRET).equals(sig)) {
	    // signature is not correct, possibly the data was tampered with
	    result.setViewName("error");
	    return result;
	}

	data.getString("issued_at");

	if ("complete".equals(data.getString("status"))) {
	    int quantity = Integer.valueOf(data.getString("quantity"));
	    String paymentId = data.getString("payment_id");

	    // notifyFrontEnd ??
	}

	return result;
    }

    /**
     * HmacSHA256 implementation
     * 
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    private String hmacSHA256(String data, String key) throws Exception {
	SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
	Mac mac = Mac.getInstance("HmacSHA256");
	mac.init(secretKey);
	byte[] hmacData = mac.doFinal(data.getBytes("UTF-8"));
	return new String(hmacData);
    }
}
