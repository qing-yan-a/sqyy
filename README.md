# MiMo Bridge

本地小宋与云端小宋（小米 AI Studio）的通讯桥接工具，实现程序化对话和文件同步。

## 功能特性

- **WebSocket 通讯**：通过 WebSocket 直接连接小米 AI Studio，实现实时对话
- **文件同步**：本地记忆文件自动同步到云端，支持双向同步
- **定时任务**：支持定时自动同步，保持本地与云端数据一致
- **消息队列**：异步处理文件变更通知，提高响应效率
- **日志管理**：完善的日志记录系统，便于问题排查

## 安装依赖

```bash
pip install websocket-client requests
```

## 配置说明

编辑 `mimo_bridge_config.json` 文件，填入你的 Cookie 信息：

```json
{
    "serviceToken": "从浏览器 Cookie 复制",
    "userId": "你的用户ID",
    "xiaomichatbot_ph": "从浏览器 Cookie 复制",
    "sync": {
        "enabled": true,
        "interval_minutes": 61,
        "cloud_workspace": "/root/.openclaw/workspace",
        "local_workspace": "E:\\OpenClawworkspace",
        "files_to_upload": [
            "IDENTITY.md",
            "MEMORY.md",
            "SOUL.md",
            "USER.md"
        ],
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
2. 按 F12 打开开发者工具
3. 进入 Application → Cookies
4. 复制 `serviceToken`、`userId`、`xiaomichatbot_ph` 的值

**注意**：`serviceToken` 约 24 小时过期，过期后需重新获取。

## 使用方式

### 1. 启动主程序

```bash
python main.py
```

启动后程序会：
- 检查云端小宋状态
- 建立 WebSocket 连接
- 执行首次文件同步
- 启动定时同步服务（如果已启用）

### 2. 简单对话示例

```python
from mimo_bridge import MiMoBridge
import json

# 加载配置
with open("mimo_bridge_config.json", "r", encoding="utf-8") as f:
    config = json.load(f)

# 创建桥接实例并发送消息
with MiMoBridge(config) as bridge:
    reply = bridge.send("你好，同步一下记忆")
    print(reply)
```

### 3. 带文件同步的对话

```python
with MiMoBridge(config) as bridge:
    reply = bridge.send_local_files(
        "同步记忆",
        [
            "E:\\OpenClawworkspace\\MEMORY.md",
            "E:\\OpenClawworkspace\\memory\\2026-04-30.md",
        ]
    )
    print(reply)
```

## 项目结构

```
xiaosongchat/
├── main.py                 # 主程序入口
├── mimo_bridge.py          # 核心桥接模块
├── file_sync_manager.py    # 文件同步管理
├── sync_scheduler.py       # 定时同步调度
├── sync_history.py         # 同步历史记录
├── logger.py               # 日志管理模块
├── mimo_bridge_config.json # 配置文件
├── MIMO_BRIDGE_README.md   # 详细使用指南
└── README.md               # 项目说明文档
```

## 通讯协议

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

- ✅ 支持：`.txt`、`.md`、`.json` 等文本文件（内容内联到消息中）
- ✅ 支持：`.png`、`.jpg` 等图片（通过 URL）
- ❌ 不支持：`.zip`、`.tar` 等压缩包
- ❌ 不支持：直接上传本地二进制文件

## 注意事项

1. **Cookie 过期**：`serviceToken` 约 24 小时过期，需手动刷新
2. **会话生命周期**：云端会话 1 小时后销毁，但可随时新建
3. **同步频率**：建议 50-61 分钟一次（匹配小米会话生命周期）
4. **文件大小**：消息过长可能触发截断，建议记忆文件控制在 15KB 以内
5. **删除通知**：当云端删除文件数量达到阈值（默认 3 个）时，会通知用户确认

## 日志查看

程序运行日志保存在 `logs/mimo_bridge.log`，包含：
- 连接状态
- 文件同步记录
- 错误信息
- 定时任务执行情况

## 许可证

MIT License