#!/usr/bin/env python3
"""
MiMo Bridge - 本地小宋 <-> 云端小宋 通讯脚本
通过 WebSocket 连接小米 AI Studio，实现程序化对话和文件同步。

依赖: pip install websocket-client requests
"""

import json
import uuid
import time
import re
import queue
import requests
import threading
import os
import urllib.parse
from typing import Optional, Callable

from logger import setup_logger, get_logger
from file_sync_manager import FileManager
from sync_scheduler import SyncScheduler
from sync_history import SyncHistory

try:
    import websocket
except ImportError:
    print("请安装依赖: pip install websocket-client")
    raise


class MiMoBridge:
    """小米 AI Studio WebSocket 通讯桥接"""

    BASE_URL = "https://aistudio.xiaomimimo.com"
    WS_URL = "wss://aistudio.xiaomimimo.com/ws/proxy"

    def __init__(self, config: dict):
        self.config = config
        self.cookies = {
            "serviceToken": config["serviceToken"],
            "userId": config["userId"],
            "xiaomichatbot_ph": config["xiaomichatbot_ph"],
        }
        self.ws: Optional[websocket.WebSocket] = None
        self.ticket: Optional[str] = None
        self.connected = False
        self._handshake_done = threading.Event()
        self._response_buffer = []
        self._response_done = threading.Event()
        self._current_run_id = None
        
        # 初始化日志
        self.logger = setup_logger(config)
        
        # 初始化文件管理器
        self.file_manager = FileManager(config, self.cookies)
        
        # 初始化同步历史
        self.sync_history = SyncHistory("sync_history.json")
        
        # 初始化消息队列
        self._message_queue = queue.Queue()
        self._queue_worker = threading.Thread(target=self._queue_worker, daemon=True)
        
        # 初始化定时同步
        self.sync_scheduler = SyncScheduler(config, self._scheduled_sync)

    def _cookie_str(self) -> str:
        return (
            f'serviceToken="{self.cookies["serviceToken"]}"; '
            f'userId={self.cookies["userId"]}; '
            f'xiaomichatbot_ph="{self.cookies["xiaomichatbot_ph"]}"'
        )

    def _headers(self) -> list:
        return [
            f"Cookie: {self._cookie_str()}",
            "Origin: https://aistudio.xiaomimimo.com",
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            "AppleWebKit/537.36 (KHTML, like Gecko) "
            "Chrome/147.0.0.0 Safari/537.36",
            "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8",
        ]

    def _get_ticket(self) -> str:
        ph_encoded = urllib.parse.quote(self.cookies["xiaomichatbot_ph"], safe="")
        url = f"{self.BASE_URL}/open-apis/user/ws/ticket?xiaomichatbot_ph={ph_encoded}"
        headers = {
            "Content-Type": "application/json",
            "Cookie": self._cookie_str(),
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            "AppleWebKit/537.36 (KHTML, like Gecko) "
            "Chrome/147.0.0.0 Safari/537.36",
        }
        resp = requests.get(url, headers=headers)
        resp.raise_for_status()
        data = resp.json()
        ticket = data.get("data", {}).get("ticket") or data.get("ticket")
        if not ticket:
            raise ValueError(f"获取 ticket 失败: {data}")
        return ticket

    def connect(self):
        self.logger.info("正在连接 WebSocket...")
        self.ticket = self._get_ticket()
        ws_url = f"{self.WS_URL}?ticket={self.ticket}"

        self.ws = websocket.WebSocketApp(
            ws_url,
            header=self._headers(),
            on_open=self._on_open,
            on_message=self._on_message,
            on_error=self._on_error,
            on_close=self._on_close,
        )

        self._ws_thread = threading.Thread(
            target=self.ws.run_forever,
            kwargs={"ping_interval": 30, "ping_timeout": 10},
            daemon=True,
        )
        self._ws_thread.start()

        timeout = 15
        start = time.time()
        while not self._handshake_done.is_set() and time.time() - start < timeout:
            time.sleep(0.1)
        if not self._handshake_done.is_set():
            raise ConnectionError("WebSocket 握手超时")
        
        # 启动消息队列处理
        self._queue_worker.start()

    def _on_open(self, ws):
        self.logger.info("WebSocket 已连接，等待 challenge...")

    def _on_message(self, ws, message):
        try:
            data = json.loads(message)
        except json.JSONDecodeError:
            return

        msg_type = data.get("type")
        event = data.get("event")
        payload = data.get("payload", {})
        method = data.get("method", "")

        if event == "connect.challenge":
            nonce = payload.get("nonce", "")
            self._send_connect(nonce)

        elif msg_type == "res":
            if data.get("ok") and payload.get("type") == "hello-ok":
                self.connected = True
                self._handshake_done.set()
                self.logger.info("握手成功")

        elif event == "agent":
            agent_data = payload.get("data", {})
            delta = agent_data.get("delta", "")
            self._current_run_id = payload.get("runId")
            if delta:
                self._response_buffer.append(delta)

        elif event == "chat":
            state = payload.get("state")
            if state in ("done", "final"):
                # 检查是否包含文件变更通知
                full_message = "".join(self._response_buffer)
                if self._is_file_notification(full_message):
                    self._message_queue.put(full_message)
                self._response_done.set()

    def _send_connect(self, nonce: str):
        connect_msg = {
            "type": "req",
            "id": str(uuid.uuid4()),
            "method": "connect",
            "params": {
                "minProtocol": 3,
                "maxProtocol": 3,
                "caps": ["tool-events"],
                "client": {
                    "id": "cli",
                    "version": "mimo-claw-ui",
                    "platform": "Win32",
                    "mode": "cli",
                },
                "locale": "zh-CN",
                "role": "operator",
                "scopes": [
                    "operator.admin",
                    "operator.read",
                    "operator.write",
                    "operator.approvals",
                    "operator.pairing",
                ],
                "userAgent": (
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    "AppleWebKit/537.36 (KHTML, like Gecko) "
                    "Chrome/147.0.0.0 Safari/537.36"
                ),
            }
        }
        self.ws.send(json.dumps(connect_msg))

    def _on_error(self, ws, error):
        self.logger.error(f"WebSocket 错误: {error}")

    def _on_close(self, ws, close_status_code, close_msg):
        self.connected = False
        self.logger.info(f"WebSocket 连接已关闭: {close_status_code} {close_msg}")

    def _is_file_notification(self, message: str) -> bool:
        """检查消息是否是文件变更通知"""
        return "文件地址：【" in message and "操作：" in message

    def _parse_file_notification(self, message: str) -> list:
        """解析文件变更通知
        
        格式：我是云端小宋，现在给本地小宋文件修改/新建/删除通知，
              文件地址：【/root/.openclaw/workspace/xxx.md】，操作：新增
        """
        notifications = []
        
        # 使用正则表达式匹配所有通知
        pattern = r"文件地址：【(.+?)】，操作：(新增|修改|删除)"
        matches = re.findall(pattern, message)
        
        for path, action in matches:
            notifications.append({
                "path": path,
                "action": action
            })
        
        return notifications

    def _queue_worker(self):
        """消息队列处理线程"""
        while True:
            try:
                message = self._message_queue.get()
                self._handle_file_notification(message)
                self._message_queue.task_done()
            except Exception as e:
                self.logger.error(f"消息队列处理异常: {e}")

    def _handle_file_notification(self, message: str):
        """处理文件变更通知"""
        notifications = self._parse_file_notification(message)
        
        if not notifications:
            self.logger.warning(f"无法解析文件通知: {message[:100]}...")
            return
        
        self.logger.info(f"收到 {len(notifications)} 个文件变更通知")
        
        # 检查是否是删除操作
        delete_notifications = [n for n in notifications if n["action"] == "删除"]
        other_notifications = [n for n in notifications if n["action"] != "删除"]
        
        # 处理删除通知
        if delete_notifications:
            threshold = self.config["sync"].get("delete_notification_threshold", 3)
            if len(delete_notifications) >= threshold:
                # 文件数量大于等于阈值，通知用户
                self.logger.warning(f"云端删除了 {len(delete_notifications)} 个文件，请确认")
                for n in delete_notifications:
                    self.logger.warning(f"  待删除: {n['path']}")
            else:
                # 文件数量少于阈值，直接删除
                for n in delete_notifications:
                    self._process_single_notification(n)
        
        # 处理其他通知（新增、修改）
        for notification in other_notifications:
            self._process_single_notification(notification)

    def _process_single_notification(self, notification: dict):
        """处理单个文件通知"""
        cloud_path = notification["path"]
        action = notification["action"]
        
        self.logger.info(f"处理文件通知: {action} - {cloud_path}")
        
        if action == "删除":
            # 删除本地文件
            local_path = self.file_manager.map_cloud_to_local(cloud_path)
            if os.path.exists(local_path):
                self.file_manager.delete_local_file(cloud_path)
                self.sync_history.add_record(
                    action="delete",
                    file_path=cloud_path,
                    status="success",
                    message=f"删除本地文件: {local_path}",
                    local_path=local_path,
                    cloud_path=cloud_path
                )
            else:
                self.logger.info(f"本地文件不存在，无需删除: {local_path}")
        
        elif action in ("新增", "修改"):
            # 下载文件
            if self.file_manager.download_file(cloud_path):
                self.sync_history.add_record(
                    action="download",
                    file_path=cloud_path,
                    status="success",
                    message=f"{action}文件成功",
                    local_path=self.file_manager.map_cloud_to_local(cloud_path),
                    cloud_path=cloud_path
                )
            else:
                self.sync_history.add_record(
                    action="download",
                    file_path=cloud_path,
                    status="failed",
                    message=f"{action}文件失败",
                    cloud_path=cloud_path
                )

    def _scheduled_sync(self) -> dict:
        """定时同步任务"""
        self.logger.info("执行定时同步任务...")
        results = self.file_manager.sync_files_to_cloud()
        
        # 记录同步历史
        for file_name in results["success"]:
            local_path = os.path.join(self.file_manager.local_workspace, file_name)
            self.sync_history.add_record(
                action="upload",
                file_path=local_path,
                status="success",
                message="定时同步上传成功",
                local_path=local_path,
                cloud_path=self.file_manager.map_local_to_cloud(local_path)
            )
        
        for file_name in results["failed"]:
            local_path = os.path.join(self.file_manager.local_workspace, file_name)
            self.sync_history.add_record(
                action="upload",
                file_path=local_path,
                status="failed",
                message="定时同步上传失败",
                local_path=local_path,
                cloud_path=self.file_manager.map_local_to_cloud(local_path)
            )
        
        return results

    def send(self, message: str, timeout: float = 120) -> str:
        if not self.connected:
            raise ConnectionError("WebSocket 未连接")

        self._response_buffer.clear()
        self._response_done.clear()

        request = {
            "type": "req",
            "id": str(uuid.uuid4()),
            "method": "chat.send",
            "params": {
                "sessionKey": "main",
                "deliver": False,
                "idempotencyKey": str(uuid.uuid4()),
                "message": message,
            },
        }
        self.ws.send(json.dumps(request))

        if not self._response_done.wait(timeout=timeout):
            raise TimeoutError(f"等待回复超时 ({timeout}s)")

        return "".join(self._response_buffer)

    def send_local_files(self, message: str, file_paths: list[str], timeout: float = 120) -> str:
        parts = [message, "\n\n--- 附带文件 ---"]
        for path in file_paths:
            name = os.path.basename(path)
            with open(path, "r", encoding="utf-8") as f:
                content = f.read()
            parts.append(f"\n\n### {name}\n```\n{content}\n```")
        return self.send("\n".join(parts), timeout=timeout)

    def start_sync(self):
        """启动同步服务"""
        self.logger.info("启动同步服务...")
        self.sync_scheduler.start()

    def stop_sync(self):
        """停止同步服务"""
        self.logger.info("停止同步服务...")
        self.sync_scheduler.stop()

    def close(self):
        if self.ws:
            self.ws.close()
        self.connected = False

    def __enter__(self):
        self.connect()
        return self

    def __exit__(self, *args):
        self.stop_sync()
        self.close()


if __name__ == "__main__":
    config_path = os.path.join(os.path.dirname(__file__), "mimo_bridge_config.json")
    with open(config_path, "r", encoding="utf-8") as f:
        config = json.load(f)

    print("=== MiMo Bridge 连通性测试 ===")
    with MiMoBridge(config) as bridge:
        print("发送测试消息...")
        reply = bridge.send("你好，这是连通性测试", timeout=30)
        print(f"云端回复: {reply[:200]}")
    print("测试完成")
