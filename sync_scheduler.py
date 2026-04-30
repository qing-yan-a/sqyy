#!/usr/bin/env python3
"""
定时同步调度模块
"""

import time
import threading
from datetime import datetime
from typing import Callable, Optional
from logger import get_logger


class SyncScheduler:
    """定时同步调度器"""
    
    def __init__(self, config: dict, sync_callback: Callable):
        self.config = config
        self.sync_callback = sync_callback
        self.logger = get_logger()
        self.sync_config = config.get("sync", {})
        self.interval_minutes = self.sync_config.get("interval_minutes", 45)
        self.enabled = self.sync_config.get("enabled", True)
        
        self._running = False
        self._thread: Optional[threading.Thread] = None
        self._stop_event = threading.Event()
        self._last_sync_time: Optional[datetime] = None
    
    @property
    def last_sync_time(self) -> Optional[datetime]:
        """获取上次同步时间"""
        return self._last_sync_time
    
    def start(self):
        """启动定时任务"""
        if not self.enabled:
            self.logger.info("定时同步已禁用")
            return
        
        if self._running:
            self.logger.warning("定时同步已在运行")
            return
        
        self._running = True
        self._stop_event.clear()
        self._thread = threading.Thread(target=self._run, daemon=True)
        self._thread.start()
        
        self.logger.info(f"定时同步已启动，间隔 {self.interval_minutes} 分钟")
    
    def stop(self):
        """停止定时任务"""
        if not self._running:
            return
        
        self._running = False
        self._stop_event.set()
        
        if self._thread and self._thread.is_alive():
            self._thread.join(timeout=5)
        
        self.logger.info("定时同步已停止")
    
    def sync_now(self):
        """立即执行同步"""
        self.logger.info("执行立即同步...")
        self._execute_sync()
    
    def _run(self):
        """定时任务主循环"""
        while self._running:
            # 等待指定时间或收到停止信号
            if self._stop_event.wait(timeout=self.interval_minutes * 60):
                break  # 收到停止信号
            
            if self._running:
                self._execute_sync()
    
    def _execute_sync(self):
        """执行同步任务"""
        try:
            self.logger.info("开始定时同步...")
            start_time = datetime.now()
            
            # 执行同步回调
            result = self.sync_callback()
            
            # 记录同步时间
            self._last_sync_time = datetime.now()
            elapsed = (self._last_sync_time - start_time).total_seconds()
            
            self.logger.info(f"定时同步完成，耗时 {elapsed:.1f} 秒")
            
            # 记录结果
            if result:
                success_count = len(result.get("success", []))
                failed_count = len(result.get("failed", []))
                not_found_count = len(result.get("not_found", []))
                self.logger.info(f"同步结果: 成功 {success_count}, 失败 {failed_count}, 未找到 {not_found_count}")
            
        except Exception as e:
            self.logger.error(f"定时同步异常: {e}")
