package com.lianxi.controller;

import cn.hutool.core.io.resource.MultiFileResource;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.lianxi.entity.User;
import com.lianxi.entity.utils.R;
import com.lianxi.entity.utils.UserDto;
import com.lianxi.service.UserService;
import com.sun.deploy.net.URLEncoder;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public R register(@RequestBody UserDto userDto){
        System.out.println(userDto);
        userService.register(userDto);
        return R.ok();
    }

    @PostMapping("/login")
    public R login(@RequestBody UserDto userDto){
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        if(StrUtil.isBlank(username)||StrUtil.isBlank(password)){
            return R.error(20010,"用户名或密码为空");
        }
        userDto = userService.login(userDto);
        return R.ok().data("data",userDto);

    }

    @GetMapping
    public R getAll(){
        List<User> users = userService.getAll();
        return R.ok().data("data",users);
    }

    @GetMapping("/{id}")
    public R getById(@PathVariable Integer id){
        User user = userService.getById(id);
        return R.ok().data("data",user);
    }

    //新增和修改
    @PostMapping
    public R save(@RequestBody User user){
        String s = userService.save(user);
        return R.ok().data("data",s);
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Integer id){
        int i = userService.deleteById(id);
        return R.ok();
    }

    //批量删除
    @DeleteMapping("/batch")
    public R deleteBatch(@RequestBody List<Integer> ids){
        System.out.println(ids);
        int i = userService.deleteBatch(ids);
        System.out.println("批量删除");
        return R.ok();
    }

    @GetMapping("/page")
    public R getByPage(@RequestParam Integer pageNum,
                       @RequestParam Integer pageSize,
                       @RequestParam String username){

        HashMap<String, Object> map = userService.selectByPage(pageNum, pageSize,username);
        return R.ok().data("data",map);
    }

    /**
     * 导出接口
     */
    @GetMapping("/export")
    public R export(HttpServletResponse response) throws IOException {
        //从数据库查询出所有数据
        List<User> list=userService.getAll();
        //通过工具类创建writer写出到磁盘路径
//        ExcelWriter writer= ExcelUtil.getWriter(fileUploadPath+"/用户信息.xlsx");
        //在内存操作，写出到浏览器
        ExcelWriter writer=ExcelUtil.getWriter(true);
        //自定义标题别名
//        writer.addHeaderAlias("username","用户名");
//        writer.addHeaderAlias("password","密码");
//        writer.addHeaderAlias("nickname","昵称");
//        writer.addHeaderAlias("email","邮箱");
//        writer.addHeaderAlias("phone","电话");
//        writer.addHeaderAlias("address","地址");
//        writer.addHeaderAlias("createTime","创建时间");
//        writer.addHeaderAlias("avatarUrl","头像");

        //一次性写出list内的对象到Excel，使用默认样式，强制输出标题
        writer.write(list,true);

        //设置浏览器响应格式
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        String fileName= URLEncoder.encode("用户信息","UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+fileName+".xls");


        ServletOutputStream out=response.getOutputStream();
        writer.flush(out,true);
        out.close();
        writer.close();
        return R.ok();
    }
    /**
     * excel导入数据
     */
    @PostMapping("/import")
    public R imp(MultipartFile file) throws Exception {
        InputStream is = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(is);
        List<User> list = reader.readAll(User.class);
        userService.saveBatch(list);
        reader.close();
        is.close();
        return R.ok();
    }



}
