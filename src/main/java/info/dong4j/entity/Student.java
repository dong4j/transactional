package info.dong4j.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fkhwl.starter.common.base.BasePO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>Company: 成都返空汇网络技术有限公司</p>
 * <p>Description: 用户信息表 实体类  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@fkhwl.com"
 * @date 2022.11.07 22:39
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("student")
public class Student extends BasePO<Long, Student> {

    public static final String USER_NAME = "user_name";
    private static final long serialVersionUID = 1L;

    /** 名称 */
    private String userName;

}
