package com.wzk.mvc.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wzk.mvc.model.User;



@Controller
@RequestMapping("/user")
public class UserController {
	// 使用map模拟数据库 
	private Map<String, User> userMap = new HashMap<String, User>();

	public UserController() {
		userMap.put("zhangsan", new User("zhangsan", "123"));
		userMap.put("lishimin", new User("lishimin", "456"));
	}

	// 获取用户列表 
	// 访问方法: http://localhost/springmvc_user/user/users
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public String list(Model model) {
		model.addAttribute("users", userMap);
		return "/user/list";
	}

	// 跳转到添加用户页面(get请求)
	// 访问方法: http://localhost/springmvc_user/user/add
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(@ModelAttribute("user") User user) {
		return "user/add";
	}

	// 具体的添加用户处理方法(post请求) 
	// 注意: BindingResult必须在User之后, 中间不能有其他的参数
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(@Validated User user, BindingResult br) throws IOException { // @Validated: 对User数据进行校验
		if (br.hasErrors()) {
			return "user/add"; // 如果有错误, 则直接跳转到add添加用户页面
		}
		userMap.put(user.getUsername(), user);
		return "redirect:/user/users"; // 重定向到用户列表页面
	}
	
	// 查看用户信息
	// 访问方法: http://localhost/springmvc_user/user/zhangsan
	@RequestMapping(value="/{username}", method=RequestMethod.GET)
	public String show(@PathVariable String username, Model model) { // @PathVariable: 路径里面的值作为参数
		model.addAttribute(userMap.get(username)); 
		return "user/show";
	}
	
	// 跳转到修改用户信息页面
	// 访问方法: http://localhost/springmvc_user/zhangsan/update
	@RequestMapping(value="/{username}/update", method=RequestMethod.GET) 
	public String update(@PathVariable String username, Model model) { // @PathVariable: 路径里面的值作为参数
		model.addAttribute(userMap.get(username)); // 等同: model.addAttribute("user", userMap.get(username));
		return "user/update";
	}
	
	// 具体的修改用户处理方法(post请求) 
	// 注意: BindingResult必须在User之后, 中间不能有其他的参数
	@RequestMapping(value="/{username}/update", method=RequestMethod.POST)
	public String update(@PathVariable String username, @Validated User user, BindingResult br) {
		if(br.hasErrors()) {
			return "user/update"; // 如果有错误, 则直接跳转到update修改用户页面
		}
		userMap.remove(username);
		userMap.put(user.getUsername(), user);
		return "redirect:/user/users";
	}
	
	// 删除用户信息
	// 访问方法: http://localhost/springmvc_user/zhangsan/delete
	@RequestMapping(value="/{username}/delete",method=RequestMethod.GET)
	public String delete(@PathVariable String username) {
		userMap.remove(username);
		return "redirect:/user/users";
	}
	
}
