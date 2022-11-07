package info.dong4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.InvalidTimeoutException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * <p>Company: 成都返空汇网络技术有限公司</p>
 * <p>Description: Propagation.REQUIRED 测试 </p>
 *
 * @author dong4j
 * @version x.x.x
 * @email "mailto:dong4j@fkhwl.com"
 * @date 2022.11.07 22:42
 * @since x.x.x
 */
public class RequiredTest extends TransactionTest {

    /**
     * 场景一: 外部无事务, 内部使用 REQUIRED
     * 条件: 1. 外部方法无事务; 2. 方法 1 正常执行; 3. 方法 2 执行后抛出异常;
     * 结果: 方法 1 成功入库, 方法 2 失败回滚;
     * 结论: 外部方法无事务, 内部方法添加 required, 内部方法抛出异常后, 内部方法回滚, 且不影响外部方法, 外部方法执行成功;
     * AOP 逻辑:
     * 1. 进入 create1(), 不存在事务;
     * 2. 进入 createByNoTransactional(), 不存在事务;
     * 3. createByNoTransactional() 执行成功, 数据正常入库;
     * 4. 进入 createByTransactionalForRequiredWithException() 存在事务, 获取 connection;
     * 5. 设置 connection.setAutoCommit(false);
     * 6. 执行代理类的 createByTransactionalForRequiredWithException();
     * 7. 代理类抛出异常执行 connection.rollback();
     */
    @Test
    void testTransactionalForRequired() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            unionService.create1();
        });

        Assertions.assertEquals(1, unionService.countForStudent());
        Assertions.assertEquals(0, unionService.countForClass());
    }

    /**
     * 场景二: 外部有事务(REQUIRED), 内部无事务 (外部事务开启的 session 在后续方法中共用)
     * 条件: 1. 外部有事务; 2. 方法 1 正常执行; 3. 方法 2 执行后抛出异常;
     * 结果: 方法 1 失败回滚, 方法 2 失败回滚;
     * 结论: 外部有事务且内部方法抛出异常且没有显式捕获, 将影响外部方法执行, 导致外部方法事务回滚;
     * AOP 逻辑:
     * 1. 进入 create2(), 存在事务, 获取 connection;
     * 2. 设置 connection.setAutoCommit(false);
     * 3. 执行代理类的 create2();
     * 3.1 进入 createByNoTransactional(), 不存在事务;
     * 3.2 createByNoTransactional() 执行成功;
     * 3.3 进入 createByTransactionalForRequiredWithException() 不存在事务;
     * 3.4 createByTransactionalForRequiredWithException() 执行成功但是抛出异常;
     * 4. create2() 未显式捕获异常, 导致 connection.rollback();
     */
    @Test
    void testTransactionalForRequired2() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            unionService.create2();
        });

        Assertions.assertEquals(0, unionService.countForStudent());
        Assertions.assertEquals(0, unionService.countForClass());
    }

    /**
     * 场景三: 外部有事务(REQUIRED), 内部有事务(REQUIRED)
     */
    @Test
    void testTransactionalForRequired3() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            unionService.create3();
        });

        Assertions.assertEquals(0, unionService.countForStudent());
        Assertions.assertEquals(0, unionService.countForClass());
    }



}
