package info.dong4j.service.impl;

import com.fkhwl.starter.mybatis.service.impl.BaseServiceImpl;

import info.dong4j.dao.StudentDao;
import info.dong4j.entity.Student;
import info.dong4j.service.StudentRepositoryService;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Company: 成都返空汇网络技术有限公司</p>
 * <p>Description: 用户信息表 服务接口实现类 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@fkhwl.com"
 * @date 2022.11.07 22:39
 * @since 1.0.0
 */
@Slf4j
@Service
public class StudentRepositoryServiceImpl extends BaseServiceImpl<StudentDao, Student> implements StudentRepositoryService {
    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public void createByNoTransactional() {
        log.info("{}", sqlSessionTemplate.toString());
        this.baseMapper.insert(new Student("新增学生"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createByTransactionalForRequired() {
        this.baseMapper.insert(new Student("新增学生"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createByTransactionalForRequiresNew() {
        this.baseMapper.insert(new Student("新增学生"));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void createByTransactionalForSupports() {
        this.baseMapper.insert(new Student("新增学生"));
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createByTransactionalForNotSupported() {
        this.baseMapper.insert(new Student("新增学生"));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void createByTransactionalForMandatory() {
        this.baseMapper.insert(new Student("新增学生"));
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void createByTransactionalForNever() {
        this.baseMapper.insert(new Student("新增学生"));
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void createByTransactionalForNested() {
        this.baseMapper.insert(new Student("新增学生"));
    }
}
