package gamla;

import org.riversun.fcm.FcmClient;
import org.riversun.fcm.model.EntityMessage;
import org.riversun.fcm.model.FcmResponse;

public class SendPushNotification {
	public void SendPushNotificationToBuyer(String message,String token) {

		FcmClient client = new FcmClient();
		// You can get from firebase console.
		// "select your project>project settings>cloud messaging"
		client.setAPIKey("AAAAjVTlCAY:APA91bHAr4sDmdPBY6lQT8XSiIwINsYeebpexH6LbS550eaioYcmrrl-i6FXYmRgoMDWZDiqov1ye6ES-lkEfZ66MvXxDXvEWpodnft-mIJDo-o6m-n7oicqUc_z6RQv6DKWp2f1Opga");

		// Data model for sending messages to specific entity(mobile devices,browser front-end apps)s
		EntityMessage msg = new EntityMessage();

		// Set registration token that can be retrieved
		// from Android entity(mobile devices,browser front-end apps) when calling
		// FirebaseInstanceId.getInstance().getToken();
		msg.addRegistrationToken(token);

		// Add key value pair 
		msg.putStringData("message", message);//set news here
		
		// push
		FcmResponse res = client.pushToEntities(msg);

		System.out.println(res);

	}
	public void SendPushNotificationToSeller(String message,String token) {

		FcmClient client = new FcmClient();
		// You can get from firebase console.
		// "select your project>project settings>cloud messaging"
		client.setAPIKey("AAAAI5SBfwg:APA91bFczDMKdwdeLE-wTa5ZNODayQvj8qwPu-9fIBBLke9fq8mMn7hNqhXRApJnt6jKlBlzvliCdnLbfwNVuXofDPKgdCe2T-5u3PR7Cior2IyhErTDsz0jr8tGo6DsmzYXlYoWrFUf");

		// Data model for sending messages to specific entity(mobile devices,browser front-end apps)s
		EntityMessage msg = new EntityMessage();

		// Set registration token that can be retrieved
		// from Android entity(mobile devices,browser front-end apps) when calling
		// FirebaseInstanceId.getInstance().getToken();
		msg.addRegistrationToken(token);

		// Add key value pair 
		msg.putStringData("message", message);//set news here
		
		// push
		FcmResponse res = client.pushToEntities(msg);

		System.out.println(res);

	}
	
}
