package com.lianxi.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lianxi.entity.User;
import com.lianxi.entity.utils.UserDto;
import com.lianxi.exception.ServiceException;
import com.lianxi.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialException;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    //按照id查询数据
    public User getById(Integer id){
        User user = userMapper.selectById(id);
        return user;
    }

    //查询所有数据
    public List<User> getAll() {
        List<User> users = userMapper.selectList(null);
        users.forEach(user -> {
            System.out.println(user);
        });
        return users;
    }



    //根据id删除数据
    public int deleteById(Integer id){
        int count = userMapper.deleteById(id);
        return count;
    }

    //批量删除
    public int deleteBatch(List<Integer> ids){
        int i = userMapper.deleteBatchIds(ids);
        return i;
    }
    //批量插入
    public boolean saveBatch(List<User> list){
        list.forEach(user -> {
            userMapper.insert(user);
        });
        return true;
    }

    //分页查询
    public HashMap<String, Object> selectByPage(Integer pageNum, Integer pageSize,String username){
        Page<User> page=new Page<>(pageNum,pageSize);
        QueryWrapper qw=new QueryWrapper<>();
        qw.like("username",username);
        Page<User> users = userMapper.selectPage(page, qw);
        long total = users.getTotal();
        List<User> records = users.getRecords();
        System.out.println(records);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("total",total);
        hashMap.put("data",records);
        return hashMap;
    }

    public String save(User user) {
        for (User user1 : userMapper.selectList(null)) {
            if (user1.getId()==user.getId()){
                int i = userMapper.updateById(user);
                return "修改成功";
            }
        }
        int insert = userMapper.insert(user);
        return "添加成功";

    }

    public UserDto login(UserDto userDto) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",userDto.getUsername());
        queryWrapper.eq("password",userDto.getPassword());
        User one=userMapper.selectOne(queryWrapper);
        if(one!=null){
            BeanUtils.copyProperties(one,userDto);//将one里的属性值copy到userDto中，这里是给昵称和头像赋值
            return userDto;
        }else{
            throw new ServiceException(20010,"用户名或密码错误");
        }

    }

    public boolean register(UserDto userDto) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",userDto.getUsername());
        User one=userMapper.selectOne(queryWrapper);
        if(one==null){
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(userDto.getPassword());
            userMapper.insert(user);
            return true;
        }else{
            throw new ServiceException(20010,"注册失败,用户名重复");
        }
    }
}
