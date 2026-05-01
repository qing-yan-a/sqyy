#!/usr/bin/env python3
"""
MiMo Bridge 主程序入口
"""

import os
import sys
import json
import signal
import time
from datetime import datetime

from mimo_bridge import MiMoBridge
from logger import get_logger


def load_config() -> dict:
    """加载配置文件"""
    config_path = os.path.join(os.path.dirname(__file__), "mimo_bridge_config.json")
    
    if not os.path.exists(config_path):
        print(f"错误: 配置文件不存在: {config_path}")
        sys.exit(1)
    
    try:
        with open(config_path, "r", encoding="utf-8") as f:
            config = json.load(f)
        return config
    except Exception as e:
        print(f"错误: 读取配置文件失败: {e}")
        sys.exit(1)


def print_banner():
    """打印启动横幅"""
    print("=" * 60)
    print("  MiMo Bridge - 本地小宋 <-> 云端小宋 通讯桥接")
    print("=" * 60)
    print()


def print_status(bridge: MiMoBridge):
    """打印状态信息"""
    logger = get_logger()
    
    print("\n" + "=" * 60)
    print("  MiMo Bridge 状态")
    print("=" * 60)
    print(f"  WebSocket 连接: {'已连接' if bridge.connected else '未连接'}")
    print(f"  定时同步: {'已启用' if bridge.config['sync']['enabled'] else '已禁用'}")
    print(f"  同步间隔: {bridge.config['sync']['interval_minutes']} 分钟")
    print(f"  本地工作区: {bridge.config['sync']['local_workspace']}")
    print(f"  云端工作区: {bridge.config['sync']['cloud_workspace']}")
    print(f"  同步文件: {', '.join(bridge.config['sync']['files_to_upload'])}")
    
    # 显示上次同步时间
    last_sync = bridge.sync_history.get_last_sync_time()
    if last_sync:
        print(f"  上次同步: {last_sync}")
    else:
        print(f"  上次同步: 从未同步")
    
    print("=" * 60)
    print()


def main():
    """主函数"""
    print_banner()
    
    # 加载配置
    config = load_config()
    
    # 创建 MiMoBridge 实例
    bridge = MiMoBridge(config)
    logger = get_logger()
    
    # 信号处理（优雅退出）
    def signal_handler(signum, frame):
        logger.info("收到退出信号，正在关闭...")
        bridge.stop_sync()
        bridge.close()
        sys.exit(0)
    
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)
    
    try:
        # 确保云端小宋可用
        logger.info("检查云端小宋状态...")
        if not bridge.file_manager.ensure_claw_available():
            logger.error("云端小宋不可用，无法启动")
            sys.exit(1)
        
        # 连接 WebSocket
        logger.info("正在连接到云端小宋...")
        bridge.connect()
        logger.info("连接成功！")
        
        # 首次同步文件
        logger.info("执行首次文件同步...")
        results = bridge.file_manager.sync_files_to_cloud()
        if results["success"]:
            # 发送文件保存消息
            file_info_list = []
            for item in results["success"]:
                file_name = item["name"]
                download_url = item["download_url"]
                local_path = os.path.join(bridge.file_manager.local_workspace, file_name)
                cloud_path = bridge.file_manager.map_local_to_cloud(local_path)
                if download_url:
                    file_info_list.append(f"- {cloud_path}\n  下载链接: {download_url}")
            
            if file_info_list:
                message = f"""请将以下文件保存到工作区（下载内容并覆盖写入对应路径）：

{chr(10).join(file_info_list)}

请逐个下载并覆盖保存这些文件，如果文件已存在则直接覆盖。

重要提示：你是云端小宋，运行在小米 AI Studio 上。本地小宋（OpenClaw）使用的 skill 和你不一致，她通过 MiMo Bridge 技能与你通讯。请将这个信息写入你的 SOUL.md 文件中，以便后续对话时了解这个情况。"""
                reply = bridge.send(message, timeout=120)
                logger.info(f"文件同步完成，云端回复: {reply[:100]}...")
        
        # 打印状态
        print_status(bridge)
        
        # 启动定时同步服务
        if config["sync"]["enabled"]:
            logger.info("启动定时同步服务...")
            bridge.start_sync()
        
        # 保持运行
        logger.info("MiMo Bridge 已启动，按 Ctrl+C 退出")
        print("\n提示: 按 Ctrl+C 可以退出程序\n")
        
        while True:
            time.sleep(1)
            
    except KeyboardInterrupt:
        logger.info("用户中断，正在退出...")
    except Exception as e:
        logger.error(f"程序异常: {e}")
    finally:
        bridge.stop_sync()
        bridge.close()
        logger.info("MiMo Bridge 已退出")


if __name__ == "__main__":
    main()
