package info.dong4j;

import com.fkhwl.starter.launcher.FkhStarter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * <p>Company: 成都返空汇网络技术有限公司</p>
 * <p>Description: ${description} </p>
 *
 * @author dong4j
 * @version x.x.x
 * @email "mailto:dong4j@fkhwl.com"
 * @date ${YEAR}.${MONTH}.${DAY} ${HOUR}:${MINUTE}
 * @since x.x.x
 */
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class Application extends FkhStarter {
    static {
        System.setProperty("PARENT_PACKAGE_NAME", "info.dong4j");
    }
}