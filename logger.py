#!/usr/bin/env python3
"""
日志管理模块
"""

import os
import logging
from logging.handlers import RotatingFileHandler
from datetime import datetime


def setup_logger(config: dict) -> logging.Logger:
    """配置日志系统"""
    log_config = config.get("logging", {})
    log_level = log_config.get("level", "INFO")
    log_file = log_config.get("file", "logs/mimo_bridge.log")
    max_size_mb = log_config.get("max_size_mb", 10)
    backup_count = log_config.get("backup_count", 5)
    
    # 确保日志目录存在
    log_dir = os.path.dirname(log_file)
    if log_dir:
        os.makedirs(log_dir, exist_ok=True)
    
    # 创建 logger
    logger = logging.getLogger("MiMoBridge")
    logger.setLevel(getattr(logging, log_level, logging.INFO))
    
    # 清除已有的 handler
    logger.handlers.clear()
    
    # 日志格式
    formatter = logging.Formatter(
        "%(asctime)s [%(levelname)s] [%(module)s] %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S"
    )
    
    # 文件 handler（带轮转）
    file_handler = RotatingFileHandler(
        log_file,
        maxBytes=max_size_mb * 1024 * 1024,
        backupCount=backup_count,
        encoding="utf-8"
    )
    file_handler.setFormatter(formatter)
    logger.addHandler(file_handler)
    
    # 控制台 handler
    console_handler = logging.StreamHandler()
    console_handler.setFormatter(formatter)
    logger.addHandler(console_handler)
    
    logger.info("日志系统初始化完成")
    return logger


def get_logger() -> logging.Logger:
    """获取 logger 实例"""
    return logging.getLogger("MiMoBridge")
