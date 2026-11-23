package cn.js.ccit.user.service;

import cn.js.ccit.user.entity.UserInfo;
import cn.js.ccit.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public boolean save(UserInfo u) { return userMapper.insert(u) > 0; }
    public boolean update(UserInfo u) { return userMapper.updateById(u) > 0; }
    public boolean delete(Long id) { return userMapper.deleteById(id) > 0; }
    public UserInfo getById(Long id) { return userMapper.selectById(id); }
    public List<UserInfo> list() { return userMapper.selectList(new QueryWrapper<>()); }

    // 登录校验
    public UserInfo login(String username, String password) {
        return userMapper.selectOne(new QueryWrapper<UserInfo>()
                .eq("username", username).eq("password", password));
    }
}