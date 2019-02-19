package kim.ioio.web.service;

import kim.ioio.web.WebSocketMessageHandler;
import kim.ioio.web.annotation.WsEndpoint;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMessageDispatchService {

    private final static String TO_USER = "toUser";

    @WsEndpoint(sendType = TO_USER)
    public void forwardToUserTextMessage(JSONObject messageJo) {
        String toUserId = messageJo.getString("to");
        String fromUserId = messageJo.getString("from");
        String messageContent = messageJo.getString("messageContent");

        JSONObject messagePayLoadJo = new JSONObject();
        messagePayLoadJo.put("messageType", 2);
        messagePayLoadJo.put("to", toUserId);
        messagePayLoadJo.put("from", fromUserId);
        messagePayLoadJo.put("messageContent", messageContent);
        WebSocketMessageHandler.sendMessageToUser(toUserId, messagePayLoadJo.toString());
    }
}
