package com.htc.controller;

import com.alibaba.fastjson2.JSONObject;
import com.htc.entity.Message;
import com.htc.entity.Page;
import com.htc.entity.User;
import com.htc.service.MessageService;
import com.htc.service.UserService;
import com.htc.tool.CommunityConstant;
import com.htc.tool.CommunityUtil;
import com.htc.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

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
        //查询未读通知数量
        int noticeUnreadCount = messageService.getNoticeUnreadCount(user.getUserId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);


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
        List<Message> letterList = messageService.getLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.getUserById(message.getFromId()));
                //将未读消息放入ids
                if (hostHolder.getUser().getUserId() == message.getToId() && message.getStatus().equals("0")) {
                    ids.add(message.getId());
                }

                letters.add(map);
            }
        }

        //改变未读消息状态
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        model.addAttribute("letters", letters);
        //私信的对象
        model.addAttribute("target", getLetterTarget(conversationId));

        return "/site/letter-detail";
    }

    /**
     * 根据会话id获取私信的对象User(和自己相对的一方)
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getUserId() == id0) {
            return userService.getUserById(id1);
        } else {
            return userService.getUserById(id0);
        }
    }

    /**
     * 发送私信
     *
     * @param toName 私信的目标用户名
     */
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.getUserByUsername(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在!");
        }

        //创建消息实例
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getUserId());
        message.setToId(target.getUserId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setStatus("0");
        message.setCreateTime(new Date());

        messageService.addMessage(message);
        //出错的情况之后再统一处理

        return CommunityUtil.getJSONString(0);
    }

    //系统通知列表
    @GetMapping(path = "/notice/list")
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();

        //查询评论通知
        Message message = messageService.getLatestNotice(user.getUserId(), CommunityConstant.TOPIC_COMMENT);
        Map<String, Object> commentNotice;
        if (message != null) {
            commentNotice = new HashMap<>();
            commentNotice.put("message", message);
            //消去html转义字符
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            commentNotice.put("user", userService.getUserById((int) data.get("userId")));
            commentNotice.put("entityType", data.get("entityType"));
            commentNotice.put("entityId", data.get("entityId"));
            commentNotice.put("postId", data.get("postId"));

            int count = messageService.getNoticeCount(user.getUserId(), CommunityConstant.TOPIC_COMMENT);
            commentNotice.put("count", count);      //所有消息
            int unread = messageService.getNoticeUnreadCount(user.getUserId(), CommunityConstant.TOPIC_COMMENT);
            commentNotice.put("unread", unread);    //未读消息
            model.addAttribute("commentNotice", commentNotice);
        }

        //查询点赞通知
        message = messageService.getLatestNotice(user.getUserId(), CommunityConstant.TOPIC_LIKE);
        Map<String, Object> likeNotice;
        if (message != null) {
            likeNotice = new HashMap<>();
            likeNotice.put("message", message);
            //消去html转义字符
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            likeNotice.put("user", userService.getUserById((int) data.get("userId")));
            likeNotice.put("entityType", data.get("entityType"));
            likeNotice.put("entityId", data.get("entityId"));
            likeNotice.put("postId", data.get("postId"));

            int count = messageService.getNoticeCount(user.getUserId(), CommunityConstant.TOPIC_LIKE);
            likeNotice.put("count", count);      //所有消息
            int unread = messageService.getNoticeUnreadCount(user.getUserId(), CommunityConstant.TOPIC_LIKE);
            likeNotice.put("unread", unread);    //未读消息
            model.addAttribute("likeNotice", likeNotice);
        }

        //查询关注通知
        message = messageService.getLatestNotice(user.getUserId(), CommunityConstant.TOPIC_FOLLOW);
        Map<String, Object> followNotice;
        if (message != null) {
            followNotice = new HashMap<>();
            followNotice.put("message", message);
            //消去html转义字符
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            followNotice.put("user", userService.getUserById((int) data.get("userId")));
            followNotice.put("entityType", data.get("entityType"));
            followNotice.put("entityId", data.get("entityId"));

            int count = messageService.getNoticeCount(user.getUserId(), CommunityConstant.TOPIC_FOLLOW);
            followNotice.put("count", count);      //所有消息
            int unread = messageService.getNoticeUnreadCount(user.getUserId(), CommunityConstant.TOPIC_FOLLOW);
            followNotice.put("unread", unread);    //未读消息
            model.addAttribute("followNotice", followNotice);
        }

        //查询未读消息数量
        int letterUnreadCount = messageService.getLetterUnreadCount(user.getUserId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        //查询未读通知数量
        int noticeUnreadCount = messageService.getNoticeUnreadCount(user.getUserId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @GetMapping(path = "/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.getUser();

        //分页设置
        page.setLimit(10);      //每页显示10条
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.getNoticeCount(user.getUserId(), topic));

        List<Message> noticeList = messageService.getNotices(user.getUserId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();

        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                //通知
                map.put("notice", notice);
                //内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.getUserById((int) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));       //关注通知该值为null
                //通知的来源用户
                map.put("fromUser",userService.getUserById(notice.getFromId()));

                noticeVoList.add(map);
            }
        }

        model.addAttribute("notices",noticeVoList);

        //通知设为已读
        List<Integer> ids=getLetterIds(noticeList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/notice-detail";
    }

    //根据Message列表返回对应的id列表
    private List<Integer> getLetterIds(List<Message> messageList){
        List<Integer> res=new ArrayList<>();
        for(Message message:messageList){
            res.add(message.getId());
        }

        return res;
    }
}
