<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en"> 
    <head>
        <title>Facebook Sample Application</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    </head>
    <body>       
        <script>
		  window.fbAsyncInit = function() {
		  FB.init({
		    appId      : '462661423817785', // App ID
		    channelUrl : '//jenaiz.homelinux.net/channel.html', // Channel File
		    status     : true, // check login status
		    cookie     : true, // enable cookies to allow the server to access the session
		    xfbml      : true  // parse XFBML
		  });
		
		  FB.Event.subscribe('auth.authResponseChange', function(response) {
		    if (response.status === 'connected') {
		      testAPI();
		    } else if (response.status === 'not_authorized') {
		      FB.login();
		    } else {
		      FB.login();
		    }
		  });
		  FB.ui({
		        method: 'pay',
		        action: 'purchaseitem',
		        product: '/jenaiz.homelinux.net/products.html',
		        quantity: 10 //,                  // optional, defaults to 1
		        //request_id: 'YOUR_REQUEST_ID' // optional, must be unique for each payment
		      },
		      callback_payment
		  );
		
		  };
		
		function callback_payment() {
		  alert("hello callback payment");
		}
		
		  (function(d){
		   var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
		   if (d.getElementById(id)) {return;}
		   js = d.createElement('script'); js.id = id; js.async = true;
		   //js.src = "//connect.facebook.net/en_US/all.js";
		   js.src = "//connect.facebook.net/en_US/all/debug.js";
		   ref.parentNode.insertBefore(js, ref);
		  }(document));
		
		  function testAPI() {
		    console.log('Welcome!  Fetching your information.... ');
		    FB.api('/me', function(response) {
		      console.log('Good to see you, ' + response.name + '.');
		    });
		  }
		</script>

   </body>
</html>
