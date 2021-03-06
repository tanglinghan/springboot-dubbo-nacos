package top.b0x0.admin.common.vo.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import top.b0x0.admin.common.vo.PageVo;

import java.time.LocalDateTime;

/**
 * 日志查询类
 *
 * @author TANG
 * @date 2020-12-18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LogReqVo extends PageVo {
    private String username;
    private String description;
    private String address;
    private String requestIp;
    private String method;
    private String params;
    private String logType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
