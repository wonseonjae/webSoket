package kopo.websoket.controller;

import kopo.websoket.handler.ChatHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Slf4j
@Controller
@RequestMapping(value = "chat")
public class ChatController {


    @RequestMapping(value = "intro")
    public String intro() {
        return "/chat/intro";
    }


    @RequestMapping(value = "room")
    public String room() {
        return "/chat/chatroom";
    }


    @RequestMapping(value = "roomList")
    @ResponseBody
    public Set<String> roomList() {

        return ChatHandler.roomInfo.keySet();

    }
}

