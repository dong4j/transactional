package info.dong4j;

import info.dong4j.service.UnionService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * <p>Company: 成都返空汇网络技术有限公司</p>
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version x.x.x
 * @email "mailto:dong4j@fkhwl.com"
 * @date 2022.11.07 22:44
 * @since x.x.x
 */
public class TransactionTest extends ApplicationTest{

    @Resource
    protected UnionService unionService;

    @BeforeEach
    void beforeAll() {
        unionService.clear();
    }

    @Test
    void testNoTransactional() {
        unionService.create();

        Assertions.assertEquals(1, unionService.countForStudent());
        Assertions.assertEquals(1, unionService.countForClass());
    }
}
