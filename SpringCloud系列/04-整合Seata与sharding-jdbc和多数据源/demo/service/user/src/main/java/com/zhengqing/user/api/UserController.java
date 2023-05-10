package com.zhengqing.user.api;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.zhengqing.user.entity.User;
import com.zhengqing.user.feign.OrderClient;
import com.zhengqing.user.service.IUserService;
import io.seata.spring.annotation.GlobalTransactional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 * 用户api
 * </p>
 *
 * @author zhengqingya
 * @description
 * @date 2021/01/13 10:11
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Api(tags = {"用户api"})
public class UserController {

    private final IUserService userService;
    private final OrderClient orderClient;

    @GetMapping("/hello")
    public String hello() {
        return this.orderClient.hello();
    }

    @GetMapping("")
    @ApiOperation("详情")
    @DS("db-test")
    public User detail(@RequestParam Long userId) {
        return this.userService.detail(userId);
    }

    @PostMapping("save")
    @ApiOperation("保存数据")
    public String save(@RequestBody User user) {
        user.setCreateTime(new Date());
        this.userService.saveOrUpdate(user);
        return "OK";
    }

    @PutMapping("update")
    @ApiOperation("更新数据")
    @GlobalTransactional
    public User update(@RequestParam Long userId) {
        User user = this.userService.detail(userId);
        user.setRemark("更新了...");
        user.updateById(); // 如果在此之前有其它事务在操作，这里拿不到全局锁，会报错：“Caused by: io.seata.rm.datasource.exec.LockConflictException: get global lock fail, xid:172.16.16.88:8091:144503564361254214, lockKeys:t_user:20”
        return user;
    }


    @PostMapping("testSeata")
    @ApiOperation("测试分布式事务")
    public String testSeata(@RequestBody User user) {
        this.userService.testSeata(user);
        return "OK";
    }

    @PostMapping("testDynamicDataShource")
    @ApiOperation("测试多数据源")
    @Transactional(rollbackFor = Exception.class)
//    @DSTransactional
    @DS("db-test")
    public void testDynamicDataShource(@RequestBody User user) {
        user.setCreateTime(new Date());
        boolean b = this.userService.saveOrUpdate(user);

//        int i = 1 / 0;

        //手动切换
//        DynamicDataSourceContextHolder.push(UcmsDataSourceConstant.CALLING);
//        String peek = DynamicDataSourceContextHolder.peek();
//        DynamicDataSourceContextHolder.poll();
    }


}
