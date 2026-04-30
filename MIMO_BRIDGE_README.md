# MiMo Bridge 使用指南

## 功能
本地小宋通过 WebSocket 直接和云端小宋（小米 AI Studio）对话，实现：
- 程序化发送消息、接收回复
- 本地记忆文件同步到云端
- 云端思考结果回传本地

## 安装依赖
```bash
pip install websocket-client requests
```

## 配置
编辑 `mimo_bridge_config.json`，填入你的 Cookie：
```json
{
    "serviceToken": "从浏览器 Cookie 复制",
    "userId": "2212029551",
    "xiaomichatbot_ph": "从浏览器 Cookie 复制"
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

### 3. 定时同步（本地小宋主循环）
```python
import time
from mimo_bridge import MiMoBridge, SyncManager

COOKIES = {...}  # 从配置文件读取

while True:
    try:
        with MiMoBridge(COOKIES) as bridge:
            sync = SyncManager(bridge, "E:\\OpenClawworkspace")
            
            # 同步到云端
            cloud_reply = sync.sync_to_cloud()
            
            # 保存云端回复
            sync.sync_from_cloud(cloud_reply)
            
            print("同步完成，等待 50 分钟...")
    except Exception as e:
        print(f"同步失败: {e}")
    
    time.sleep(50 * 60)  # 50 分钟
```

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
3. 同步频率建议 50 分钟一次（匹配小米会话生命周期）
4. 消息过长可能触发截断，建议记忆文件控制在 15KB 以内
