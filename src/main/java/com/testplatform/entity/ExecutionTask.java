package com.testplatform.entity;

import com.testplatform.entity.enums.TaskStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

/**
 * 执行任务实体类
 * 使用Redis存储的任务信息
 */
@Data
@RedisHash("execution_tasks")
public class ExecutionTask {
    @Id
    private String taskId;
    
    private String caseId;
    
    private TaskStatus status = TaskStatus.QUEUED;  // 设置默认状态为已排队
    
    @TimeToLive
    private Long timeout;
} 