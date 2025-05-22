package org.qiyu.live.msg.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: 萱子王
 * @CreateTime: 2025-05-12
 * @Description: 记录校验短信验证码结果的实体类
 * @Version: 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6646686564968092419L;

    private boolean checkStatus;

    private String desc;


}
