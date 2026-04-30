#!/usr/bin/env python3
"""
同步历史记录模块
"""

import os
import json
from datetime import datetime
from typing import Optional
from logger import get_logger


class SyncHistory:
    """同步历史记录管理器"""
    
    def __init__(self, history_file: str = "sync_history.json"):
        self.history_file = history_file
        self.logger = get_logger()
        self.records = []
        self.last_sync_time: Optional[str] = None
        self.load()
    
    def add_record(self, action: str, file_path: str, status: str, message: str, 
                   local_path: Optional[str] = None, cloud_path: Optional[str] = None):
        """添加同步记录"""
        record = {
            "timestamp": datetime.now().isoformat(),
            "action": action,
            "file_path": file_path,
            "local_path": local_path,
            "cloud_path": cloud_path,
            "status": status,
            "message": message
        }
        
        self.records.append(record)
        
        # 限制记录数量（最多保留 1000 条）
        if len(self.records) > 1000:
            self.records = self.records[-1000:]
        
        # 更新上次同步时间
        if action in ("upload", "download", "sync"):
            self.last_sync_time = record["timestamp"]
        
        # 保存到文件
        self.save()
        
        # 记录日志
        log_msg = f"[{action.upper()}] {file_path} - {status}: {message}"
        if status == "success":
            self.logger.info(log_msg)
        elif status == "failed":
            self.logger.error(log_msg)
        else:
            self.logger.warning(log_msg)
    
    def get_history(self, limit: int = 50) -> list:
        """获取历史记录"""
        return self.records[-limit:]
    
    def get_last_sync_time(self) -> Optional[str]:
        """获取上次同步时间"""
        return self.last_sync_time
    
    def save(self):
        """保存到文件"""
        try:
            data = {
                "records": self.records,
                "last_sync_time": self.last_sync_time
            }
            
            # 确保目录存在
            os.makedirs(os.path.dirname(self.history_file) if os.path.dirname(self.history_file) else ".", exist_ok=True)
            
            with open(self.history_file, "w", encoding="utf-8") as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
        except Exception as e:
            self.logger.error(f"保存同步历史失败: {e}")
    
    def load(self):
        """从文件加载"""
        if not os.path.exists(self.history_file):
            return
        
        try:
            with open(self.history_file, "r", encoding="utf-8") as f:
                data = json.load(f)
            
            self.records = data.get("records", [])
            self.last_sync_time = data.get("last_sync_time")
            
            self.logger.info(f"加载同步历史: {len(self.records)} 条记录")
        except Exception as e:
            self.logger.error(f"加载同步历史失败: {e}")
    
    def clear(self):
        """清空历史记录"""
        self.records = []
        self.last_sync_time = None
        self.save()
        self.logger.info("同步历史已清空")
