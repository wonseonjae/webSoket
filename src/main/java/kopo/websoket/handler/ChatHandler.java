package kopo.websoket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.websoket.dto.ChatDTO;
import kopo.websoket.util.CmmUtil;
import kopo.websoket.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class ChatHandler extends TextWebSocketHandler {
    private static Set<WebSocketSession> client = Collections.synchronizedSet(new LinkedHashSet<>());
    public static Map<String, String> roomInfo = Collections.synchronizedMap(new LinkedHashMap<>());

    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String userName = CmmUtil.nvl((String) session.getAttributes().get("userName"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        log.info("roomName : " + roomName);
        log.info("userName : " + userName);
        log.info("roomNameHash : " + roomNameHash);

        client.forEach(s -> {
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))) {
                try {

                    //{"name":"이협건","msg":"ㅇㅎ","date":"2022. 7. 25. 오전 9:30:57"}
                    ChatDTO cDTO = new ChatDTO();
                    cDTO.setName("관리자");
                    cDTO.setMsg(userName + "님이 " + roomName + " 채팅방에 입장하셨습니다.");
                    cDTO.setDate(DateUtil.getDateTime("yyyyMMdd hh:mm:ss"));

                    String json = new ObjectMapper().writeValueAsString(cDTO);
                    log.info("json : " + json);

                    TextMessage chatMsg = new TextMessage(json);
                    s.sendMessage(chatMsg);

                    cDTO = null;

                } catch (IOException e) {
                    log.info("Error : " + e);
                }
            }
        });
        if (!client.contains(session)) {

            client.add(session);

            roomInfo.put(roomName, roomNameHash);
        }
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String userName = CmmUtil.nvl((String) session.getAttributes().get("userName"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        String msg = CmmUtil.nvl(message.getPayload());

        ChatDTO cDTO = new ObjectMapper().readValue(msg, ChatDTO.class);

        cDTO.setDate(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"));

        String json = new ObjectMapper().writeValueAsString(cDTO);

        log.info("json : " + json);


        // 웹소켓에 접속된 모든 사용자 검색
        client.forEach(s -> {

            // 내가 접속한 채팅방에 있는 세션만 메시지 보내기
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))) {
                try {

                    TextMessage chatMsg = new TextMessage(json);
                    s.sendMessage(chatMsg);

                } catch (IOException e) {
                    log.info("Error : " + e);
                }

            }
        });


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        String roomName = CmmUtil.nvl((String) session.getAttributes().get("roomName"));
        String userName = CmmUtil.nvl((String) session.getAttributes().get("userName"));
        String roomNameHash = CmmUtil.nvl((String) session.getAttributes().get("roomNameHash"));

        client.remove(session); // 접속되어있는 세션 삭제

        // 웹소켓에 접속된 모든 사용자 검색
        client.forEach(s -> {

            // 내가 접속한 채팅방에 있는 세션만 메시지 보내기
            if (roomNameHash.equals(s.getAttributes().get("roomNameHash"))) {

                try {
                    //{"name":"이협건","msg":"ㅇㅎ","date":"2022. 7. 25. 오전 9:30:57"}
                    ChatDTO cDTO = new ChatDTO();
                    cDTO.setName("관리자");
                    cDTO.setMsg(userName + "님이 " + roomName + " 채팅방에 퇴장하셨습니다.");
                    cDTO.setDate(DateUtil.getDateTime("yyyy-MM-dd hh:mm:ss"));

                    String json = new ObjectMapper().writeValueAsString(cDTO);
                    log.info("json : " + json);

                    TextMessage chatMsg = new TextMessage(json);
                    s.sendMessage(chatMsg);

                    cDTO = null;

                } catch (IOException e) {
                    log.info("Error : " + e);
                }
            }
        });


    }
}
