package com.htc.controller;

import com.htc.entity.Message;
import com.htc.entity.Page;
import com.htc.entity.User;
import com.htc.service.MessageService;
import com.htc.service.UserService;
import com.htc.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    /**
     * 获取私信列表
     */
    @GetMapping(path = "/letter/list")
    public String getLetterList(Model model, Page page) {
        //用户信息
        User user = hostHolder.getUser();

        //分页信息
        page.setLimit(10);
        page.setPath("/letter/list");
        page.setRows(messageService.getConversationCount(user.getUserId()));

        //会话列表
        List<Message> conversationList = messageService.getConversations(user.getUserId(), page.getOffset(), page.getRows());

        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);    //消息
                //该会话私信总数量
                map.put("letterCount", messageService.getLetterCount(message.getConversationId()));
                //该会话的未读消息
                map.put("unreadCount", messageService.getLetterUnreadCount(user.getUserId(), message.getConversationId()));
                //获取目标用户id
                int targetId = (user.getUserId() == message.getFromId()) ? message.getToId() : message.getFromId();
                map.put("target", userService.getUserById(targetId));

                conversations.add(map);
            }
        }

        model.addAttribute("conversations", conversations);
        //查询当前用户的未读消息数总和
        int unreadCount = messageService.getLetterUnreadCount(user.getUserId(), null);
        model.addAttribute("letterUnreadCount", unreadCount);

        return "/site/letter";
    }

    /**
     * 获取某个会话的私信详情
     */
    @GetMapping(path = "/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        //分页信息
        page.setLimit(10);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.getLetterCount(conversationId));

        //查询私信数据
        List<Message> letterList = messageService.getLetters(conversationId, page.getOffset(), page.getRows());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.getUserById(message.getFromId()));

                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        //私信的对象
        model.addAttribute("target",getLetterTarget(conversationId));

        return "/site/letter-detail";
    }

    //根据会话id获取私信的对象User
    private User getLetterTarget(String conversationId){
        String[] ids=conversationId.split("_");
        int id0=Integer.parseInt(ids[0]);
        int id1=Integer.parseInt(ids[1]);

        if(hostHolder.getUser().getUserId()==id0){
            return userService.getUserById(id1);
        }else{
            return userService.getUserById(id0);
        }
    }
}
