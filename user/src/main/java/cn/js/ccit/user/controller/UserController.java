package cn.js.ccit.user.controller;

import cn.js.ccit.common.Result;
import cn.js.ccit.user.entity.UserInfo;
import cn.js.ccit.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public Result<?> save(@RequestBody UserInfo u) { return Result.success(userService.save(u)); }

    @PutMapping
    public Result<?> update(@RequestBody UserInfo u) { return Result.success(userService.update(u)); }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) { return Result.success(userService.delete(id)); }

    @GetMapping("/{id}")
    public Result<UserInfo> get(@PathVariable Long id) { return Result.success(userService.getById(id)); }

    @GetMapping("/list")
    public Result<?> list() { return Result.success(userService.list()); }

    // 登录接口
    @PostMapping("/login")
    public Result<UserInfo> login(@RequestParam String username, @RequestParam String password) {
        UserInfo u = userService.login(username, password);
        return u != null ? Result.success(u) : Result.error("用户名或密码错误");
    }
}