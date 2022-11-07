package info.dong4j.service;

import com.fkhwl.starter.mybatis.service.BaseService;

import info.dong4j.entity.Student;

/**
 * <p>Company: 成都返空汇网络技术有限公司</p>
 * <p>Description: 用户信息表 服务接口 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@fkhwl.com"
 * @date 2022.11.07 22:39
 * @since 1.0.0
 */
public interface StudentRepositoryService extends BaseService<Student> {

    void createByNoTransactional();

    void createByTransactionalForRequired();

    void createByTransactionalForRequiresNew();

    void createByTransactionalForSupports();

    void createByTransactionalForNotSupported();

    void createByTransactionalForMandatory();

    void createByTransactionalForNever();

    void createByTransactionalForNested();
}

