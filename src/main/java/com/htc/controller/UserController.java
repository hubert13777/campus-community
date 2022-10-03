package com.htc.controller;

import com.htc.annotation.LoginRequired;
import com.htc.entity.User;
import com.htc.service.UserService;
import com.htc.tool.CommunityUtil;
import com.htc.tool.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    /**
     * 上传头像文件
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile image, Model model) {
        if (image == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }
        //重命名文件，避免重复
        String fileName = image.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));  //获取文件后缀
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "图片文件后缀不正确!");
            return "/site/setting";
        }
        //存入指定路径
        File folder=new File(uploadPath);
        if(!folder.exists()&&!folder.isDirectory()){    //确保文件夹存在
            folder.mkdirs();
        }

        fileName = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + fileName);
        try {
            image.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常!", e);
        }
        //更新当前用户头像路径
        //形式：http://localhost:8081/community/user/header/xx.png
        User user = hostHolder.getUser();
        String newUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getUserId(), newUrl);
        return "redirect:/index";
    }

    /**
     * 获取头像图片内容
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        fileName = uploadPath + "/" + fileName;
        String suffix = fileName.substring(fileName.lastIndexOf('.'));    //获取后缀
        //响应图片
        response.setContentType("image/" + suffix);
        FileInputStream fs = null;
        try {
            OutputStream os = response.getOutputStream();
            fs = new FileInputStream(fileName);
            byte[] buffer = new byte[1024];   //生成缓冲区
            int b = 0;
            while ((b = fs.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        } finally {
            try {
                if (fs != null) fs.close();
            } catch (IOException e) {
                logger.error("文件输入流关闭失败: " + e.getMessage());
            }
        }
    }
}
