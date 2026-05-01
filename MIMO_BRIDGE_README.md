# MiMo Bridge 使用指南

## 功能
本地小宋通过 WebSocket 直接和云端小宋（小米 AI Studio）对话，实现：
- 程序化发送消息、接收回复
- 智能文件同步（创建时上传、50分钟后回传、tongbu文件夹持续同步）
- 云端思考结果回传本地

## 同步策略

### 创建云端小宋时（上传）
- SOUL.md → 云端（让云端有人格）
- IDENTITY.md → 云端（让云端有身份）
- MEMORY.md → 云端（共享记忆）
- USER.md → 云端（用户信息）
- tongbu/ → 云端（同步文件夹）

### 50分钟后（回传）
- MEMORY.md ← 云端（云端的详细工作记录）
- USER.md ← 云端（可能有更新）

### tongbu文件夹
- 持续双向同步，有修改就触发
- 用于项目文件、临时文档等需要共享的内容

## 安装依赖
```bash
pip install websocket-client requests
```

## 配置
编辑 `mimo_bridge_config.json`，填入你的 Cookie：
```json
{
    "serviceToken": "从浏览器 Cookie 复制",
    "userId": "你的用户ID",
    "xiaomichatbot_ph": "从浏览器 Cookie 复制",
    "sync": {
        "enabled": true,
        "cloud_workspace": "/root/.openclaw/workspace",
        "local_workspace": "E:\\OpenClawworkspace",
        "create_upload_files": [
            "SOUL.md",
            "IDENTITY.md",
            "MEMORY.md",
            "USER.md"
        ],
        "pullback_files": [
            "MEMORY.md",
            "USER.md"
        ],
        "pullback_delay_minutes": 50,
        "tongbu_folder": "tongbu",
        "conflict_resolution": "local_wins",
        "delete_notification_threshold": 3
    },
    "logging": {
        "level": "INFO",
        "file": "logs/mimo_bridge.log",
        "max_size_mb": 10,
        "backup_count": 5
    }
}
```

### Cookie 获取方法
1. 打开 https://aistudio.xiaomimimo.com/
2. F12 → Application → Cookies
3. 复制 `serviceToken`、`userId`、`xiaomichatbot_ph`

⚠️ `serviceToken` 约 24 小时过期，过期后需重新获取

## 使用方式

### 1. 简单对话
```python
from mimo_bridge import MiMoBridge

cookies = {
    "serviceToken": "xxx",
    "userId": "2212029551",
    "xiaomichatbot_ph": "xxx",
}

with MiMoBridge(cookies) as bridge:
    reply = bridge.send("你好，同步一下记忆")
    print(reply)
```

### 2. 带文件同步
```python
with MiMoBridge(cookies) as bridge:
    reply = bridge.send_local_files(
        "同步记忆",
        [
            "E:\\OpenClawworkspace\\MEMORY.md",
            "E:\\OpenClawworkspace\\memory\\2026-04-30.md",
        ]
    )
    print(reply)
```

### 3. 启动主程序
```bash
python main.py
```

启动后程序会：
- 检查云端小宋状态
- 建立 WebSocket 连接
- 让云端创建tongbu文件夹
- 上传核心文件到云端
- 启动50分钟定时回传任务
- 启动tongbu文件夹持续同步

## 通讯协议说明

```
本地小宋                          小米 AI Studio
    │                                  │
    │  GET /open-apis/user/ws/ticket   │
    │ ────────────────────────────────► │
    │ ◄─ ticket ────────────────────── │
    │                                  │
    │  WSS /ws/proxy?ticket=xxx        │
    │ ════════════════════════════════► │  WebSocket 连接
    │                                  │
    │  {type:"req", method:"chat.send"}│
    │ ────────────────────────────────► │  发送消息
    │                                  │
    │  {event:"agent", delta:"你"}     │
    │ ◄──────────────────────────────── │  流式回复
    │  {event:"agent", delta:"好"}     │
    │ ◄──────────────────────────────── │
    │  {event:"chat", state:"done"}    │
    │ ◄──────────────────────────────── │  回复完成
```

## 文件传输限制
- ✅ 支持: .txt, .md, .json 等文本文件（内容内联到消息中）
- ✅ 支持: .png, .jpg 等图片（通过 URL）
- ❌ 不支持: .zip, .tar 等压缩包
- ❌ 不支持: 直接上传本地二进制文件

## 注意事项
1. Cookie 过期需手动刷新（约 24 小时）
2. 云端会话 1 小时后销毁，但可以随时新建
3. tongbu文件夹用于需要双向同步的文件，有修改就触发同步
4. 消息过长可能触发截断，建议记忆文件控制在 15KB 以内
