package com.sqyy.hospital.patientportal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class HealthNewsQueueService {

    private static final Logger log = LoggerFactory.getLogger(HealthNewsQueueService.class);
    private static final int QUEUE_CAPACITY = 5;
    private static final int FILL_DELAY_MS = 3000;        // 填充间隔 3 秒
    private static final int FULL_POLL_DELAY_MS = 600000;  // 队列满后轮询间隔 10 分钟

    private final CozeService cozeService;
    private final ObjectMapper objectMapper;

    // 环形缓冲区
    private final String[] buffer = new String[QUEUE_CAPACITY];
    private final AtomicInteger writeIndex = new AtomicInteger(0);   // 写入位置
    private final AtomicInteger count = new AtomicInteger(0);        // 当前有效消息数
    private volatile boolean running = true;
    private Thread producerThread;

    // 每个用户的读取指针
    private final Map<String, AtomicInteger> userPointers = new ConcurrentHashMap<>();

    public HealthNewsQueueService(CozeService cozeService, ObjectMapper objectMapper) {
        this.cozeService = cozeService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void start() {
        producerThread = new Thread(this::produceLoop, "health-news-producer");
        producerThread.setDaemon(true);
        producerThread.start();
        log.info("健康资讯生产者线程已启动");
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (producerThread != null) {
            producerThread.interrupt();
        }
    }

    /**
     * 用户获取下一条消息（轮转）
     */
    public String getNextMessage(String userId) {
        int currentCount = count.get();
        if (currentCount == 0) {
            // 队列还没有消息，返回兜底
            return fallbackMessage("资讯加载中，请稍后刷新");
        }

        AtomicInteger pointer = userPointers.computeIfAbsent(userId, k -> new AtomicInteger(0));
        int index = pointer.getAndUpdate(i -> (i + 1) % QUEUE_CAPACITY) % QUEUE_CAPACITY;
        String msg = buffer[index];
        return msg != null ? msg : fallbackMessage("资讯加载中，请稍后刷新");
    }

    /**
     * 生产者循环：持续向 Agent 请求消息填入队列
     */
    private void produceLoop() {
        // 启动时等待几秒让应用完全就绪
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            return;
        }

        while (running) {
            try {
                if (count.get() >= QUEUE_CAPACITY) {
                    // 队列满，等 10 分钟再试
                    log.info("健康资讯队列已满（{}条），等待 {} 分钟后刷新", QUEUE_CAPACITY, FULL_POLL_DELAY_MS / 60000);
                    Thread.sleep(FULL_POLL_DELAY_MS);
                    if (!running) return;

                    // 刷新最旧的消息（写入位置自然会覆盖）
                }

                log.info("正在请求健康资讯...");
                String newsJson = cozeService.fetchHealthNews("queue-producer");
                if (newsJson != null && !newsJson.isBlank()) {
                    int idx = writeIndex.getAndUpdate(i -> (i + 1) % QUEUE_CAPACITY);
                    buffer[idx] = newsJson;
                    if (count.get() < QUEUE_CAPACITY) {
                        count.incrementAndGet();
                    }
                    log.info("健康资讯已入队（位置 {}），当前队列长度: {}", idx, count.get());
                } else {
                    log.warn("健康资讯返回为空，跳过");
                }

                // 队列未满时等 3 秒，满时等 10 分钟
                int delay = count.get() >= QUEUE_CAPACITY ? FULL_POLL_DELAY_MS : FILL_DELAY_MS;
                Thread.sleep(delay);

            } catch (InterruptedException e) {
                log.info("健康资讯生产者被中断");
                return;
            } catch (Exception e) {
                log.error("健康资讯生产者异常: {}", e.getMessage(), e);
                try {
                    Thread.sleep(FILL_DELAY_MS);
                } catch (InterruptedException ie) {
                    return;
                }
            }
        }
    }

    private String fallbackMessage(String reason) {
        try {
            return objectMapper.writeValueAsString(new JsonNode[]{
                    objectMapper.createObjectNode()
                            .put("title", "资讯加载中")
                            .put("summary", reason)
                            .put("image", "")
                            .put("url", "")
                            .put("source", "系统")
            });
        } catch (Exception e) {
            return "[]";
        }
    }

    public int getQueueSize() {
        return count.get();
    }
}
