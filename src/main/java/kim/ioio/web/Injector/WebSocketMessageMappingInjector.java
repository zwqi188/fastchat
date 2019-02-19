package kim.ioio.web.Injector;

import kim.ioio.common.utils.ClassUtil;
import kim.ioio.web.annotation.WsEndpoint;
import kim.ioio.web.constants.ParamKey;
import kim.ioio.web.service.WebSocketMessageDispatchService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

@Component
public class WebSocketMessageMappingInjector {

    private final static String SCAN_WSENDPOINT_PACKAGES = "kim.ioio.web.service";

    @Autowired
    private WebSocketMessageDispatchService webSocketMessageDispatchService;

    public void process(WebSocketSession session, TextMessage message) {
        try {
            Set<Class<?>> classSet = ClassUtil.getClasses(SCAN_WSENDPOINT_PACKAGES);
            if (classSet == null) {
                return;
            }
            for (Class<?> clazz : classSet) {
                // 获取方法上的注解
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    WsEndpoint wsEndpoint = method.getAnnotation(WsEndpoint.class);
                    String annotaionSendType = wsEndpoint.sendType();
                    if (!StringUtils.isEmpty(annotaionSendType)) {
                        String payload = message.getPayload();
                        JSONObject messageJo = new JSONObject(payload);
                        String sendType = messageJo.getString(ParamKey.SEND_TYPE);
                        if (StringUtils.endsWithIgnoreCase(annotaionSendType, sendType)) {
                            method.invoke(webSocketMessageDispatchService, messageJo);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
