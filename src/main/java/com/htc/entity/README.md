## 表DiscussPost
用于存储帖子信息，包括以下字段：  
- **postId** — 帖子的编号
- **userId** — 用户id
- **title** — 标题，最多50字
- **content** — 内容，最多2000字
- **type** — 类型，0普通，1置顶
- **status** — 状态，0正常，1精华，2拉黑
- **createTime** — 创建时间
- **commentCount** — 评论数量
- **score** — 帖子的分数

## 表User
存储用户信息：  
- **userId** — 主键，用户id
- **username** — 用户名，最多20字
- **password** — 密码，经过加密的结果
- **salt** — 盐值，辅助加密
- **email** — 邮箱
- **type** —  0普通用户，1管理员，2版主
- **headImageUrl** — 头像图片地址
- **createTime** — 注册时间