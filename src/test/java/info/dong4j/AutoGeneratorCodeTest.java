package info.dong4j;

import com.fkhwl.starter.core.util.SystemUtils;
import com.fkhwl.starter.devtools.AutoGeneratorCodeBuilder;
import com.fkhwl.starter.devtools.ModuleConfig;
import com.fkhwl.starter.devtools.TemplatesConfig;

import org.junit.jupiter.api.Test;

/**
 * <p>Company: 成都返空汇网络技术有限公司</p>
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@fkhwl.com"
 * @date 2020.03.20 16:19
 * @since 1.0.0
 */
class AutoGeneratorCodeTest {

    /**
     * Simple auto generator code
     *
     * @since 1.0.0
     */
    @Test
    void simpleAutoGeneratorCode() {
        AutoGeneratorCodeBuilder.onAutoGeneratorCode()
            // 设置存放自动生成的代码路径, 不填则默认当前项目下
            .withModelPath("")
            .withVersion("1.0.0")
            // 设置谁作者名, 默认读取 FKH_NAME_SPACE 变量
            .withAuthor(SystemUtils.USER_NAME)
            // 设置包名 (前缀默认为 com.fkhwl, 因此最终的包名为: com.fkhwl.${packageName})
            .withPackageName("demo")
            // 忽略前缀
            .withPrefix(new String[] {""})
            // 设置根据哪张表生成代码, 可写多张表
            .withTables(new String[] {"class", "student"})
            // 设置需要生成的模板 不设置则全部生成
            .withTemplate(
                TemplatesConfig.DAO,
                TemplatesConfig.SERVICE,
                TemplatesConfig.IMPL,
                TemplatesConfig.ENTITY
                         )
            // 设置需要生成的配置
            .withComponets()
            .withModuleType(ModuleConfig.ModuleType.SINGLE_MODULE)
            .build();
    }

}
