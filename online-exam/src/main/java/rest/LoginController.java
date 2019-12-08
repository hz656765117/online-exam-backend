package rest;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import model.LoginUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.UserService;

import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Path("/")
public class LoginController {

    @Autowired
    HttpSession httpSession;

    @Autowired
    private UserService userService;

    /***
     * 用户使用 用户名、手机号、邮箱 和 密码 登入
     * 判断 手机号：全数字 邮箱：包含'@'
     * @param multivaluedMap
     * @return 用户信息
     * @throws Exception
     */
    @POST
    @Path("login")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResponseEntity login(MultivaluedMap multivaluedMap) throws Exception {
        Map<String,Object> map = new HashMap<>(3);
        map.put("errno",0);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /***
     * 用户注册，前端需为role赋值{0,1,2}，未赋值则默认为0，即普通用户
     * 注：用户名（username）不允许包含'@' 不允许全数字
     * 注：邮箱（email） 必须包含'@'
     * 注：手机号只允许中国大陆手机号，只允许全数字
     * @param loginUsers
     * @return 用户信息（脱敏）
     * @throws Exception
     */
    @POST
    @Path("register")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public LoginUsers register(LoginUsers loginUsers) throws Exception {
        loginUsers = userService.addUser(loginUsers);
        httpSession.setAttribute("user", JSONObject.toJSON(loginUsers));
        return loginUsers;
    }

    /***
     * 检查是否有重复注册信息，不允许有相同用户名，邮箱，手机号
     * @param map { username，email，telephone }
     * @return true 无重复 | false 有重复
     * @throws Exception
     */
    @PUT
    @Path("check")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public ResponseEntity check(Map<String,String> map) throws Exception {
        if ( map.containsKey("username") || map.containsKey("email") || map.containsKey("telephone") ) {
            boolean flag = userService.check(map);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

}