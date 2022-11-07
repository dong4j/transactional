package info.dong4j.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>Company: 成都返空汇网络技术有限公司</p>
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version x.x.x
 * @email "mailto:dong4j@fkhwl.com"
 * @date 2022.11.07 23:23
 * @since x.x.x
 */
@Service
public class UnionService {
    @Resource
    private StudentRepositoryService studentRepositoryService;
    @Resource
    private ClassRepositoryService classRepositoryService;

    public void clear(){
        studentRepositoryService.remove(new QueryWrapper<>());
        classRepositoryService.remove(new QueryWrapper<>());
    }

    public int countForStudent(){
        return studentRepositoryService.count();
    }

    public int countForClass(){
        return classRepositoryService.count();
    }

    public void create(){
        studentRepositoryService.createByNoTransactional();
        classRepositoryService.createByNoTransactional();
    }

    /**
     * 外部无事务, 内部方法 1 无事务, 内部方法 2 有事务且抛出异常
     */
    public void create1(){
        studentRepositoryService.createByNoTransactional();
        classRepositoryService.createByTransactionalForRequiredWithException();
    }

    /**
     * 外部方法有事务, 内部方法 1 无事务, 内部方法 2 无事务且抛出异常
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void create2(){
        studentRepositoryService.createByNoTransactional();
        classRepositoryService.createByNoTransactionalWithException();
    }

    /**
     * 外部方法有事务, 内部方法 1 无事务, 内部方法 2 有事务且抛出异常
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void create3(){
        studentRepositoryService.createByNoTransactional();
        classRepositoryService.createByTransactionalForRequiredWithException();
    }
}
