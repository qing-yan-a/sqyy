#!/usr/bin/env python3
"""
文件同步管理模块
"""

import os
import json
import hashlib
import requests
import urllib.parse
from typing import Optional
from logger import get_logger


class FileManager:
    """文件同步管理器"""
    
    BASE_URL = "https://aistudio.xiaomimimo.com"
    
    def __init__(self, config: dict, cookies: dict):
        self.config = config
        self.cookies = cookies
        self.logger = get_logger()
        self.sync_config = config.get("sync", {})
        self.cloud_workspace = self.sync_config.get("cloud_workspace", "/root/.openclaw/workspace")
        self.local_workspace = self.sync_config.get("local_workspace", "E:\\OpenClawworkspace")
    
    def check_claw_status(self) -> dict:
        """检查云端小宋状态
        
        Returns:
            dict: {"status": "AVAILABLE" or "DESTROYED", "expire_time": timestamp, "message": str}
        """
        ph_encoded = urllib.parse.quote(self.cookies["xiaomichatbot_ph"], safe="")
        url = f"{self.BASE_URL}/open-apis/user/mimo-claw/status?xiaomichatbot_ph={ph_encoded}"
        
        try:
            resp = requests.get(url, headers=self._headers(), timeout=10)
            resp.raise_for_status()
            data = resp.json()
            
            if data.get("code") == 0:
                status_data = data["data"]
                return {
                    "status": status_data.get("status"),
                    "expire_time": status_data.get("expireTime"),
                    "message": status_data.get("message")
                }
            else:
                self.logger.error(f"检查状态失败: {data}")
                return {"status": "UNKNOWN", "expire_time": None, "message": str(data)}
        except Exception as e:
            self.logger.error(f"检查状态异常: {e}")
            return {"status": "ERROR", "expire_time": None, "message": str(e)}
    
    def create_claw(self) -> bool:
        """创建云端小宋
        
        Returns:
            bool: 是否创建成功
        """
        ph_encoded = urllib.parse.quote(self.cookies["xiaomichatbot_ph"], safe="")
        url = f"{self.BASE_URL}/open-apis/user/mimo-claw/create?xiaomichatbot_ph={ph_encoded}"
        
        try:
            resp = requests.post(url, headers=self._headers(), timeout=10)
            resp.raise_for_status()
            data = resp.json()
            
            if data.get("code") == 0:
                status = data["data"].get("status")
                self.logger.info(f"创建云端小宋成功: {status}")
                return True
            else:
                self.logger.error(f"创建云端小宋失败: {data}")
                return False
        except Exception as e:
            self.logger.error(f"创建云端小宋异常: {e}")
            return False
    
    def ensure_claw_available(self) -> bool:
        """确保云端小宋可用
        
        Returns:
            bool: 是否可用
        """
        status_info = self.check_claw_status()
        
        if status_info["status"] == "AVAILABLE":
            self.logger.info(f"云端小宋可用，过期时间: {status_info['expire_time']}")
            return True
        
        self.logger.info(f"云端小宋状态: {status_info['status']}，尝试创建...")
        if not self.create_claw():
            return False
        
        # 等待创建完成（最多 3 分钟）
        import time
        max_wait = 180  # 3 分钟
        wait_interval = 10  # 每 10 秒检查一次
        waited = 0
        
        while waited < max_wait:
            time.sleep(wait_interval)
            waited += wait_interval
            
            status_info = self.check_claw_status()
            if status_info["status"] == "AVAILABLE":
                self.logger.info(f"云端小宋创建完成，等待了 {waited} 秒")
                return True
            
            self.logger.info(f"等待云端小宋创建中... ({waited}/{max_wait}s)")
        
        self.logger.error(f"云端小宋创建超时（{max_wait}秒）")
        return False
    
    def _cookie_str(self) -> str:
        """生成 Cookie 字符串"""
        return (
            f'serviceToken="{self.cookies["serviceToken"]}"; '
            f'userId={self.cookies["userId"]}; '
            f'xiaomichatbot_ph="{self.cookies["xiaomichatbot_ph"]}"'
        )
    
    def _headers(self) -> dict:
        """生成请求头"""
        return {
            "accept": "*/*",
            "accept-language": "system",
            "content-type": "application/json",
            "origin": "https://aistudio.xiaomimimo.com",
            "referer": "https://aistudio.xiaomimimo.com/",
            "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                          "AppleWebKit/537.36 (KHTML, like Gecko) "
                          "Chrome/147.0.0.0 Safari/537.36",
            "x-timezone": "Asia/Shanghai",
            "Cookie": self._cookie_str(),
        }
    
    def map_cloud_to_local(self, cloud_path: str) -> str:
        """云端路径 → 本地路径
        
        /root/.openclaw/workspace/AGENTS.md
        → E:\\OpenClawworkspace\\AGENTS.md
        """
        relative = cloud_path.replace(self.cloud_workspace, "")
        relative = relative.lstrip("/")
        return os.path.join(self.local_workspace, relative.replace("/", "\\"))
    
    def map_local_to_cloud(self, local_path: str) -> str:
        """本地路径 → 云端路径
        
        E:\\OpenClawworkspace\\AGENTS.md
        → /root/.openclaw/workspace/AGENTS.md
        """
        relative = local_path.replace(self.local_workspace, "")
        relative = relative.lstrip("\\")
        return self.cloud_workspace + "/" + relative.replace("\\", "/")
    
    def list_files(self, path: Optional[str] = None) -> list:
        """获取文件列表"""
        if path is None:
            path = self.cloud_workspace
        
        ph_encoded = urllib.parse.quote(self.cookies["xiaomichatbot_ph"], safe="")
        url = f"{self.BASE_URL}/open-apis/host-files/list?xiaomichatbot_ph={ph_encoded}"
        
        try:
            resp = requests.get(url, headers=self._headers(), timeout=10)
            resp.raise_for_status()
            data = resp.json()
            
            if data.get("code") == 0:
                return data["data"].get("items", [])
            else:
                self.logger.error(f"获取文件列表失败: {data}")
                return []
        except Exception as e:
            self.logger.error(f"获取文件列表异常: {e}")
            return []
    
    def get_download_url(self, cloud_path: str) -> Optional[str]:
        """获取文件下载链接"""
        ph_encoded = urllib.parse.quote(self.cookies["xiaomichatbot_ph"], safe="")
        url = f"{self.BASE_URL}/open-apis/host-files/download?xiaomichatbot_ph={ph_encoded}"
        
        payload = {"path": cloud_path}
        
        try:
            resp = requests.post(url, headers=self._headers(), json=payload, timeout=10)
            resp.raise_for_status()
            data = resp.json()
            
            if data.get("code") == 0:
                return data["data"]["resourceUrl"]
            else:
                self.logger.error(f"获取下载链接失败: {data}")
                return None
        except Exception as e:
            self.logger.error(f"获取下载链接异常: {e}")
            return None
    
    def download_file(self, cloud_path: str) -> bool:
        """下载文件从云端到本地"""
        self.logger.info(f"下载文件: {cloud_path}")
        
        # 获取下载链接
        download_url = self.get_download_url(cloud_path)
        if not download_url:
            self.logger.error(f"无法获取下载链接: {cloud_path}")
            return False
        
        # 下载文件
        local_path = self.map_cloud_to_local(cloud_path)
        
        try:
            resp = requests.get(download_url, timeout=30)
            resp.raise_for_status()
            
            # 确保目录存在
            os.makedirs(os.path.dirname(local_path), exist_ok=True)
            
            # 写入文件
            with open(local_path, "wb") as f:
                f.write(resp.content)
            
            self.logger.info(f"下载成功: {os.path.basename(local_path)} ({len(resp.content)} bytes)")
            return True
        except Exception as e:
            self.logger.error(f"下载失败: {e}")
            return False
    
    def get_upload_info(self, file_name: str, file_content: bytes) -> Optional[dict]:
        """获取上传凭证"""
        ph_encoded = urllib.parse.quote(self.cookies["xiaomichatbot_ph"], safe="")
        url = f"{self.BASE_URL}/open-apis/resource/genUploadInfo?xiaomichatbot_ph={ph_encoded}"
        
        # 计算 MD5
        md5_hash = hashlib.md5(file_content).hexdigest()
        
        payload = {
            "fileName": file_name,
            "fileContentMd5": md5_hash
        }
        
        try:
            resp = requests.post(url, headers=self._headers(), json=payload, timeout=10)
            resp.raise_for_status()
            data = resp.json()
            
            if data.get("code") == 0:
                return data["data"]
            else:
                self.logger.error(f"获取上传凭证失败: {data}")
                return None
        except Exception as e:
            self.logger.error(f"获取上传凭证异常: {e}")
            return None
    
    def upload_to_fds(self, upload_url: str, file_content: bytes, md5_hash: str) -> bool:
        """上传文件到 FDS"""
        fds_headers = {
            "Content-Type": "application/octet-stream",
            "content-md5": md5_hash,
            "Origin": "https://aistudio.xiaomimimo.com",
            "Referer": "https://aistudio.xiaomimimo.com/",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                          "AppleWebKit/537.36 (KHTML, like Gecko) "
                          "Chrome/147.0.0.0 Safari/537.36",
        }
        
        try:
            resp = requests.put(upload_url, headers=fds_headers, data=file_content, timeout=30)
            resp.raise_for_status()
            return True
        except Exception as e:
            self.logger.error(f"上传到 FDS 失败: {e}")
            return False
    
    def upload_file(self, local_path: str) -> dict:
        """上传文件从本地到云端
        
        Returns:
            dict: {"success": bool, "download_url": str or None}
        """
        if not os.path.exists(local_path):
            self.logger.error(f"本地文件不存在: {local_path}")
            return {"success": False, "download_url": None}
        
        file_name = os.path.basename(local_path)
        self.logger.info(f"上传文件: {file_name}")
        
        # 读取文件内容
        try:
            with open(local_path, "rb") as f:
                file_content = f.read()
        except Exception as e:
            self.logger.error(f"读取文件失败: {e}")
            return {"success": False, "download_url": None}
        
        # 计算 MD5
        md5_hash = hashlib.md5(file_content).hexdigest()
        
        # 获取上传凭证
        upload_info = self.get_upload_info(file_name, file_content)
        if not upload_info:
            return {"success": False, "download_url": None}
        
        # 上传到 FDS
        upload_url = upload_info.get("uploadUrl")
        download_url = upload_info.get("resourceUrl")  # 获取下载链接
        if self.upload_to_fds(upload_url, file_content, md5_hash):
            self.logger.info(f"上传成功: {file_name} ({len(file_content)} bytes)")
            return {"success": True, "download_url": download_url}
        
        return {"success": False, "download_url": None}
    
    def delete_local_file(self, cloud_path: str) -> bool:
        """删除本地文件"""
        local_path = self.map_cloud_to_local(cloud_path)
        
        if not os.path.exists(local_path):
            self.logger.warning(f"本地文件不存在，无需删除: {local_path}")
            return True
        
        try:
            os.remove(local_path)
            self.logger.info(f"删除本地文件: {local_path}")
            return True
        except Exception as e:
            self.logger.error(f"删除本地文件失败: {e}")
            return False
    
    def sync_files_to_cloud(self) -> dict:
        """同步指定文件到云端（创建云端时调用）"""
        files_to_upload = self.sync_config.get("create_upload_files", [])
        results = {
            "success": [],
            "failed": [],
            "not_found": []
        }
        
        for file_name in files_to_upload:
            local_path = os.path.join(self.local_workspace, file_name)
            
            if not os.path.exists(local_path):
                results["not_found"].append(file_name)
                self.logger.warning(f"本地文件不存在: {file_name}")
                continue
            
            upload_result = self.upload_file(local_path)
            if upload_result["success"]:
                results["success"].append({
                    "name": file_name,
                    "download_url": upload_result["download_url"]
                })
            else:
                results["failed"].append(file_name)
        
        return results
    
    def pullback_files_from_cloud(self) -> dict:
        """从云端拉取文件（50分钟后调用）"""
        files_to_pull = self.sync_config.get("pullback_files", [])
        results = {
            "success": [],
            "failed": [],
            "not_found": []
        }
        
        # 获取云端文件列表
        cloud_files = self.list_files()
        cloud_file_names = {item.get("name", ""): item for item in cloud_files}
        
        for file_name in files_to_pull:
            if file_name not in cloud_file_names:
                results["not_found"].append(file_name)
                self.logger.warning(f"云端文件不存在: {file_name}")
                continue
            
            cloud_path = f"{self.cloud_workspace}/{file_name}"
            if self.download_file(cloud_path):
                results["success"].append(file_name)
            else:
                results["failed"].append(file_name)
        
        return results
    
    def get_tongbu_folder_path(self, is_cloud: bool = False) -> str:
        """获取tongbu文件夹路径"""
        folder_name = self.sync_config.get("tongbu_folder", "tongbu")
        if is_cloud:
            return f"{self.cloud_workspace}/{folder_name}"
        else:
            return os.path.join(self.local_workspace, folder_name)
    
    def sync_tongbu_folder(self, direction: str = "both") -> dict:
        """同步tongbu文件夹
        
        Args:
            direction: "upload" (本地→云端), "download" (云端→本地), "both" (双向)
        """
        tongbu_folder = self.sync_config.get("tongbu_folder", "tongbu")
        local_tongbu = os.path.join(self.local_workspace, tongbu_folder)
        cloud_tongbu = f"{self.cloud_workspace}/{tongbu_folder}"
        
        results = {
            "upload": {"success": [], "failed": []},
            "download": {"success": [], "failed": []}
        }
        
        # 确保本地tongbu文件夹存在
        os.makedirs(local_tongbu, exist_ok=True)
        
        if direction in ["upload", "both"]:
            # 上传本地tongbu文件夹内容
            for root, dirs, files in os.walk(local_tongbu):
                for file_name in files:
                    local_path = os.path.join(root, file_name)
                    relative_path = os.path.relpath(local_path, local_tongbu)
                    cloud_path = f"{cloud_tongbu}/{relative_path.replace(os.sep, '/')}"
                    
                    upload_result = self.upload_file(local_path)
                    if upload_result["success"]:
                        results["upload"]["success"].append({
                            "name": relative_path,
                            "download_url": upload_result["download_url"]
                        })
                    else:
                        results["upload"]["failed"].append(relative_path)
        
        if direction in ["download", "both"]:
            # 下载云端tongbu文件夹内容
            cloud_files = self.list_files(cloud_tongbu)
            for item in cloud_files:
                file_name = item.get("name", "")
                cloud_path = f"{cloud_tongbu}/{file_name}"
                local_path = os.path.join(local_tongbu, file_name)
                
                if self.download_file(cloud_path):
                    results["download"]["success"].append(file_name)
                else:
                    results["download"]["failed"].append(file_name)
        
        return results
