| 事务的传播机制 | 说明                                                                                                                                          | 备注                                                                                                                                                                          |
| -------------- | --------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| required       | 如果当前存在事务，就加入该事务。 如果当前没有事务，就创建一个新事务。 这是最常用的设置。                                                      | 只创建一个事务。                                                                                                                                                              |
| requires_new   | 不管是否存在事务，都创建一个新的事务。 **老事务** 先挂起，再创建 **新事务**， **新事务** 执行完并提交， 接着，继续执行 **老事务**，最后提交。 | 1、每次都创建一个新的事务。 2、创建 **新事务** 前，**老事务** 先挂起。 3、先执行的方法后提交事务，后执行的方法先提交事务。 4、**老事务** 的回滚，不会影响 **新事务** 的提交。 |
| nested         | 如果当前存在事务，则在嵌套事务内执行。 如果当前没有事务，则执行与 `required` 类似的操作。                                                     | nested 创建事务。                                                                                                                                                             |
| supports       | **支持当前事务。** 如果当前存在事务，就加入该事务， 如果当前不存在事务，就以非事务执行。                                                      | supports 不会创建事务。                                                                                                                                                       |
| not_supported  | **不支持事务。** 如果当前存在事务，就把当前事务 **挂起**。 如果当前没有事务，就以非事务执行。                                                 |                                                                                                                                                                               |
| mandatory      | **强制、必须使用事务。** 如果当前 **已经存在事务**，就加入该事务， 如果当前不存在事务，就 **抛出异常**。                                      | 1、mandatory 不会创建事务。 2、mandatory 执行的前提是已经存在事务。                                                                                                           |
| never          | **禁止事务** 。 如果当前存在事务，则 **抛出异常**， 如果当前没有事务，以非事务方式执行.                                                    | 必须在一个 **没有事务** 中执行，否则报错。                                                                                                                                    |

## Spring 事务

Spring 事务的底层原理是使用 AOP 实现了如下步骤:

1. 新建一个数据库连接 connect;
2. 设置 connect 的 autoCommit = false;
3. **执行代理方法**;
4. 执行 connect 的 commit() 提交事务;

## required

如果当前存在事务，就加入该事务。 如果当前没有事务，就创建一个新事务。

```java
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertStudent(StudentDo studentDo) {  
	studentMapper.insertStudent(studentDo);  
	System.out.println("----------------------->Student插入成功!");  
}  

@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertClass(ClassDo classDo) {  
	classMapper.insertClass(classDo);  
	System.out.println("----------------------->Class插入成功!");  
}

@Test  
public void insertTest() {  
	studentService.insertStudent(studentDo);  
	classService.insertClass(classDo);  
	  
}
```

结果：两条数据均被插入数据库。由于外部方法并没有开启事务，所以内部方法均在自己的事务提交或者回滚，因此外部方法中存在异常，内部方法事务不会回滚。

底层原理:

1. 获取 studentService 对应的数据库连接;
2. 设置 connect 的 autoCommit = false;
3. **执行代理方法**;
4. 执行 studentService connect 的 commit() 提交事务;
5. 获取 classService 对应的数据库连接;
6. 设置 connect 的 autoCommit = false;
7. **执行代理方法**;
8. 执行 classService connect 的 commit() 提交事务;

```java
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertStudent(StudentDo studentDo) {  
	studentMapper.insertStudent(studentDo);  
	System.out.println("----------------------->Student插入成功!");  
}  

//此方法中抛出异常  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertClassByException(ClassDo classDo) throws CustomException {  
	classMapper.insertClass(classDo);  
	throw new CustomException();  
}

@Test  
public void insertExceptionTest() throws CustomException {  
	studentService.insertStudent(studentDo);  
	classService.insertClassByException(classDo);  
}
```

结果：第一数据成功插入，第二条数据因异常存在，事务回滚。内部方法均在各个的事务中运行，class 事务回滚，student 数据不会受到影响。

底层原理:

1. 获取 studentService 对应的数据库连接;
2. 设置 connect 的 autoCommit = false;
3. **执行代理方法**;
4. 执行 studentService connect 的 commit() 提交事务;
5. 获取 classService 对应的数据库连接;
6. 设置 connect 的 autoCommit = false;
7. **执行代理方法**;
8. 抛出异常回滚;

调用者开启事务

```java
@Test  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertInnerExceptionThrowsTest() throws CustomException {  
	studentService.insertStudent(studentDo);  
	classService.insertClassByException(classDo);  
}
```

结果: 内部方法虽然存在事务传播行为，但是外部方法也存在事务且使用 `Propagation.REQUIRED` 修饰，所有内部方法不会新建事务，直接运行在当前事务中，所以 student、class 均会被回滚。

底层原理:

1. 获取 insertInnerExceptionThrowsTest 对应的数据库连接;
2. 设置 connect 的 autoCommit = false;
3. **执行代理方法** (insertStudent + insertClassByException 共享调用者事务);
4. 抛出异常全部回滚;

调用者开启事务并捕获内部异常:

```java
@Test  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertInnerExceptionTest() {  
    studentService.insertStudent(studentDo);  
    try {  
        classService.insertClassByException(classDo);  
    } catch (CustomException e) {  
        e.printStackTrace();  
    }  
}
```

结果：外围方法开启事务，内部方法加入外围方法事务，内部方法抛出异常回滚，即使方法被 catch 不被外围方法感知，整个事务依然回滚。同 2 一样，调用者方法执行操作和被调用者中的方法操作结果均被回滚。

### 总结

1. 通过这两个方法我们证明了在外围方法未开启事务的情况下 `Propagation_Required` 修饰的内部方法会新开自己的事务，且开启的事务相互独立，互不干扰, 即 **如果不存在事务就开启新事务**。
2. 通过最后一个测试方法证明外部如果开启事务, 内部就共享这个事务, 不管是否存在事务, 即 **如果当前存在事务就加入事务**;

## requires_new

不管是否存在事务，都创建一个新的事务。 **老事务** 先挂起，再创建 **新事务**， **新事务** 执行完并提交， 接着，继续执行 **老事务**，最后提交。

```java
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)  
public void insertStudent(StudentDo studentDo) {  
	studentMapper.insertStudent(studentDo);  
	System.out.println("----------------------->Student插入成功!");  
}  

@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)  
public void insertClassByException(ClassDo classDo) throws CustomException {  
	classMapper.insertClass(classDo);  
	throw new CustomException();  
}  
  
@Test  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertInnerExceptionTest() {  
	studentService.insertStudent(studentDo);  
	try {  
		classService.insertClassByException(classDo);  
	} catch (CustomException e) {  
		e.printStackTrace();  
	}  
}
```

结果解析：insertStudent()，insertClassByException() 方法执行时，外部方法事务被挂起，内部方法会新建事务，直至该方法执行结束，恢复外部方法事务执行。两者之间事务存在隔离性，insertClassByException() 方法遇到异常，触发事务回滚机制，但 insertStudent() 执行结果并未受到影响。

底层原理:

1. 进入 insertInnerExceptionTest() 后获取 connection1;
2. 设置 connection1.autoCommit = false;
3. 进入 insertStudent() 后获取 connection2;
4. 设置 connection2.autoCommit = false;
5. 执行 insertStudent() 逻辑;
6. 执行 connection2.commit;
7. 进入 insertClassByException() 后获取 connection3;
8. 设置 connection3.autoCommit = false;
9. 执行 insertClassByException() 后异常回滚 (connection3.rollback());
10. insertInnerExceptionTest() 捕获 insertClassByException() 抛出的异常;
11. 执行 connection1.commit;

## nested

如果当前存在事务，则在嵌套事务内执行。 如果当前没有事务，则执行与 `required` 类似的操作。

- 表示当前方法已经存在一个事务，那么该方法将会在嵌套事务中运行;
- 嵌套的事务可以独立与当前事务进行单独地提交或者回滚;
- 如果当前事务不存在，那么其行为与 required 一样;
- 嵌套事务的概念就是内层事务依赖于外层事务。外层事务失败时，会回滚内层事务所做的动作;
- 而内层事务操作失败并不会引起外层事务的回滚;

### 外部未开启事务, 内部则新建事务

```java
@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)  
public void insertStudent(StudentDo studentDo) {  
	studentMapper.insertStudent(studentDo);  
	System.out.println("----------------------->Student插入成功!");  
}  

@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)  
public void insertClass(ClassDo classDo) {  
	classMapper.insertClass(classDo);  
	System.out.println("----------------------->Class插入成功!");  
}

@Test  
public void insertTest() {  
	studentService.insertStudent(studentDo);  
	classService.insertClass(classDo);  
	throw new RuntimeException();  
}  
```

结果: insertTest() 未开启事务, 则 insertStudent() 开启事务并成功提交, insertClass() 开启事务并成功提交, 最后抛出异常, 但是不影响上面 2 个事务;

底层原理:

1. 进入 insertStudent() 并获取对应的 connection1;
2. connection1.setAutoCommit = false;
3. 执行 insertStudent 代理逻辑;
4. connection1.commit();
5. 进入 insertClass() 并获取对应的 connection2;
6. connection2.setAutoCommit = false;
7. 执行 insertClass 代理逻辑;
8. connection2.commit();

因为 commit 后才抛出异常, 因此不会再回滚.

### 外部方法开启事务

```java
@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)  
public void insertStudent(StudentDo studentDo) {  
	studentMapper.insertStudent(studentDo);  
	System.out.println("----------------------->Student插入成功!");  
}  

@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)  
public void insertClassByException(ClassDo classDo) throws CustomException {  
	classMapper.insertClass(classDo);  
	throw new RuntimeException();  
}

@Test  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertInnerExceptionThrowsTest() throws CustomException {  
	studentMapper.insertStudent(studentDo);  
	classService.insertClassByException(classDo);  
}  
```

结果: 全部方法都回滚.

- 如果外部方法发生异常，则内部事务一起发生回滚操作；

底层原理:

1. 进入 insertInnerExceptionThrowsTest 并获取 connection1;
2. connection1.setAutoCommit = false;
3. 执行代理逻辑:
	1. 进入 insertStudent 并获取 connection2;
	2. connection2.setAutoCommit = false;
	3. 执行代理逻辑;
	4. connection2.commit();
	5. 进入 insertClassByException 并获取 connection3;
	6. connection3.setAutoCommit = false;
	7. 执行代理逻辑;
	8. 抛出异常, connection3.rollback;
4. 未显式捕获异常, 则 connection1.rollback, 并且将 connection2 对应的数据回滚;

```java
@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)  
public void insertStudent(StudentDo studentDo) {  
	studentMapper.insertStudent(studentDo);  
	System.out.println("----------------------->Student插入成功!");  
}  

@Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)  
public void insertClassByException(ClassDo classDo) throws CustomException {  
	classMapper.insertClass(classDo);  
	throw new RuntimeException();  
}

@Test  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertInnerExceptionThrowsTest() throws CustomException {  
	studentMapper.insertStudent(studentDo);  
	try {
		classService.insertClassByException(classDo);  
	} catch (Exception e){
		log.error(e);	
	}
}  
```

结果: insertStudent 提交成功, insertClassByException 回滚.

- 如果外部无异常情况，内部被调用方法存在异常情况，则内部方法独立回滚

## supports

**支持当前事务。** 如果当前存在事务，就加入该事务， 如果当前不存在事务，就以非事务执行。

```java
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)  
public void insertStudent(StudentDo studentDo) {  
	studentMapper.insertStudent(studentDo);  
	System.out.println("----------------------->Student插入成功!");  
}  

@Test  
public void insertSupportsTest() {  
	studentService.insertStudent(studentDo);  
}
```

结果：如果单纯的调用 insertStudent() 方法，则以非事务执行，即使后面存在异常情况，执行操作结果不会触发事务回滚机制。即 **当前不存在事务则以 非事务 方式运行**.

```java
@Test  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public void insertSupportsTest() {  
	studentService.insertStudent(studentDo);  
}
```

结果: 该方法以 required 修饰，则会新建一个事务，内部调用 insertStudent() 方法，所以 insertStudent() 会加入到当前事务中执行。即 **如果存在事务则以 事务 方式运行**.

## not_supported

**不支持事务。** 如果当前存在事务，就把当前事务 **挂起**。 如果当前没有事务，就以非事务执行。表示被修饰的方法不应该运行在事务中。如果调用者存在当前事务，则该方法运行期间，当前事务将被挂起。

```java
@Transactional(propagation = Propagation.NOT_SUPPORTED, rollbackFor = Exception.class)  
public void insertClassByException(ClassDo classDo) throws CustomException {  
	classMapper.insertClass(classDo);  
	throw new CustomException();  
}  

@Test  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertInnerExceptionTest() {  
	try {  
		classService.insertClassByException(classDo);  
	} catch (CustomException e) {  
		e.printStackTrace();  
	}  
}
```

结果解释：即使外部方法开启事务，但是 insertClassByException() 执行时当前事务会挂起，not_support 以非事务方式运行，所以即使遇到异常情况，执行结果也不会触发回滚。

如果外部不捕获异常, insertClassByException() 则会回滚, 因为外部使用 REQUIRED 修饰, 内部抛出异常且没有显式不过, 外部方法代表的整个事务会回滚.

## mandatory

**强制、必须使用事务。**

```java
@Transactional(propagation = Propagation.MANDATORY,rollbackFor = Exception.class)  
public void insertStudent(StudentDo studentDo) {  
	studentMapper.insertStudent(studentDo);  
	System.out.println("----------------------->Student插入成功!");  
}  

@Test  
public void insertMandatoryTest1() {  
	studentService.insertStudent(studentDo);  
}

@Test  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertMandatoryTest2() {  
	studentService.insertStudent(studentDo);  
}
```

结果: mandatory 表示被修饰的方法 **必须** 在事务中运行。当调用 insertMandatoryTest1() 时，因为当前没有一个活动的事务，则会抛出异常 `throw new IllegalTransactionStateException(“Transaction propagation ‘mandatory’ but no existing transaction found”);` 

当调用 insertMandatoryTest2() 时，insertStudent 则加入到 insertMandatoryTest2 的事务中以事务的方式执行。

## never

**禁止事务** 。 如果当前存在事务，则 **抛出异常**， 如果当前没有事务，以非事务方式执行. 

表示被修饰的方法不应该运行事务上下文中。如果调用者或者该方法中存在一个事务正在运行，则会抛出异常。

```java
@Transactional(propagation = Propagation.NEVER, rollbackFor = Exception.class)  
public void insertStudent(StudentDo studentDo) {  
	studentMapper.insertStudent(studentDo);  
	System.out.println("----------------------->Student插入成功!");  
}  

@Test  
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)  
public void insertInnerExceptionTest() {  
	studentService.insertStudent(studentDo);  
}
```

结果: 直接抛出 `IllegalTransactionException: Existing transaction for transaction marked with propagation 'never'`.

## 总结

| 传播等级      | 当前是否存在事务 | 行为                   | 结果 |
|:------------- |:---------------- |:---------------------- |:---- |
| required      | 有               | 加入事务               |      |
| required      | 无               | 新建事务               |      |
| requires_new  | 有               | 挂起外部事务, 新增事务 |  内部影响外部    |
| requires_new  | 无               | 新增事务               | 外部不影响内部     |
| supports      | 有               | 加入事务               |      |
| supports      | 无               | 非事务运行             |      |
| not_supported | 有               | 挂起当前事务,非事务运行           |      |
| not_supported | 无               | 非事务运行             |      |
| nested        | 有               | 嵌套事务               | 内部不影响外部     |
| nested        | 无               | 新建事务, 同 required  |  外部影响内部    |
| mandatory     | 有               | 加入事务               |      |
| mandatory     | 无               | 抛出异常               |      |
| never         | 有               | 抛出异常               |      |
| never         | 无               | 非事务运行             |      |

分类:

| 类别           | 事务传播类型 |
| -------------- | ------------ |
| 支持当前事务   | required     |
| ^^             | supports     |
| ^^             | mandatory    |
| 不支持当前事务 | requires_new |
| ^^             | not_supports |
| ^^             | never        |
| 嵌套事务       | nested       |

区别:

1. never 与 not_supported: 如果当前存在异常, 前者是直接 **抛出异常**, 后者是 **挂起当前事务并以非事务方式运行**;
2. never 与 mandatory: 如果当前存在异常, 前者是直接 **抛出异常**, 后者是当前不存在事务, 则直接 **抛出异常**;

`org.springframework.transaction.support.AbstractPlatformTransactionManager#getTransaction`

```java
public final TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {  
    TransactionDefinition def = definition != null ? definition : TransactionDefinition.withDefaults();  
    Object transaction = this.doGetTransaction();  
    boolean debugEnabled = this.logger.isDebugEnabled();  
    if (this.isExistingTransaction(transaction)) {  
        return this.handleExistingTransaction(def, transaction, debugEnabled);  
    } else if (def.getTimeout() < -1) {  
        throw new InvalidTimeoutException("Invalid transaction timeout", def.getTimeout());  
    } else if (def.getPropagationBehavior() == 2) {  
        throw new IllegalTransactionStateException("No existing transaction found for transaction marked with propagation 'mandatory'");  
    } else if (def.getPropagationBehavior() != 0 && def.getPropagationBehavior() != 3 && def.getPropagationBehavior() != 6) {  
        if (def.getIsolationLevel() != -1 && this.logger.isWarnEnabled()) {  
            this.logger.warn("Custom isolation level specified but no actual transaction initiated; isolation level will effectively " +  
                             "be ignored: " + def);  
        }  
  
        boolean newSynchronization = this.getTransactionSynchronization() == 0;  
        return this.prepareTransactionStatus(def, (Object) null, true, newSynchronization, debugEnabled, (Object) null);  
    } else {  
        AbstractPlatformTransactionManager.SuspendedResourcesHolder suspendedResources = this.suspend((Object) null);  
        if (debugEnabled) {  
            this.logger.debug("Creating new transaction with name [" + def.getName() + "]: " + def);  
        }  
  
        try {  
            return this.startTransaction(def, transaction, debugEnabled, suspendedResources);  
        } catch (Error | RuntimeException var7) {  
            this.resume((Object) null, suspendedResources);  
            throw var7;  
        }  
    }  
}
```

## 适用场景

1. required: 适用于大部分场景;
2. not_supported: 发送提示消息, 站内信, 短信,邮件等, 这类要求不影响系统的主体业务逻辑, 即使操作失败页不应该对主体逻辑产生影响;
3. requires_new: 总是创建新的事务执行, 适用于不收外层方法事务影响的场景, 比如记录日志, 不管主体业务逻辑是否已经完成, 日志都要记录下来, 不能因为主体业务逻辑异常导致日志操作回滚.


> [! 代码]
> [GitHub - dong4j/transactional: spring transactional mechanism](https://github.com/dong4j/transactional)
