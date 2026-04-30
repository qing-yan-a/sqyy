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
    
    def upload_file(self, local_path: str) -> bool:
        """上传文件从本地到云端"""
        if not os.path.exists(local_path):
            self.logger.error(f"本地文件不存在: {local_path}")
            return False
        
        file_name = os.path.basename(local_path)
        self.logger.info(f"上传文件: {file_name}")
        
        # 读取文件内容
        try:
            with open(local_path, "rb") as f:
                file_content = f.read()
        except Exception as e:
            self.logger.error(f"读取文件失败: {e}")
            return False
        
        # 计算 MD5
        md5_hash = hashlib.md5(file_content).hexdigest()
        
        # 获取上传凭证
        upload_info = self.get_upload_info(file_name, file_content)
        if not upload_info:
            return False
        
        # 上传到 FDS
        upload_url = upload_info.get("uploadUrl")
        if self.upload_to_fds(upload_url, file_content, md5_hash):
            self.logger.info(f"上传成功: {file_name} ({len(file_content)} bytes)")
            return True
        
        return False
    
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
        """同步指定文件到云端"""
        files_to_upload = self.sync_config.get("files_to_upload", [])
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
            
            if self.upload_file(local_path):
                results["success"].append(file_name)
            else:
                results["failed"].append(file_name)
        
        return results
