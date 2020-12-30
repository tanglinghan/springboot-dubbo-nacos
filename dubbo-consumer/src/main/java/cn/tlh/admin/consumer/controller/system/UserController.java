package cn.tlh.admin.consumer.controller.system;

import cn.tlh.admin.common.base.dto.RoleSmallDto;
import cn.tlh.admin.common.base.dto.UserDto;
import cn.tlh.admin.common.base.mapstruct.UserMapper;
import cn.tlh.admin.common.base.vo.BusinessResponse;
import cn.tlh.admin.common.base.vo.req.UserPassReqVo;
import cn.tlh.admin.common.base.vo.req.UserQueryReqVo;
import cn.tlh.admin.common.exception.customexception.BusinessErrorException;
import cn.tlh.admin.common.pojo.system.SysUser;
import cn.tlh.admin.common.util.*;
import cn.tlh.admin.common.util.enums.CodeEnum;
import cn.tlh.admin.consumer.shiro.ShiroUtils;
import cn.tlh.admin.service.aop.annotaion.Log;
import cn.tlh.admin.service.system.RoleService;
import cn.tlh.admin.service.system.UserService;
import cn.tlh.admin.service.system.VerifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author TANG
 * @date 2020-11-23
 */
@Api(tags = "系统：用户管理")
@RestController
@RequestMapping("system/user")
public class UserController {

//    PasswordEncoder passwordEncoder;

    @Reference(version = "${service.version}", check = false)
    UserService userService;
    @Reference(version = "${service.version}", check = false)
    RoleService roleService;
    @Reference(version = "${service.version}", check = false)
    VerifyService verificationCodeService;

    UserMapper userMapper;

    @ApiOperation("查询用户")
    @GetMapping
    // // @PreAuthorize("@el.check('SysUser:list')")
    public BusinessResponse query(UserQueryReqVo userQueryReqVo) {
        return BusinessResponse.ok(userService.selectList(userQueryReqVo));
    }

    @ApiOperation("导出用户数据")
    @GetMapping(value = "/download")
    // // @PreAuthorize("@el.check('SysUser:list')")
    public void download(HttpServletResponse response, UserQueryReqVo userQueryReqVo) throws IOException {
        List<SysUser> records = userService.selectList(userQueryReqVo).getRecords();
        List<UserDto> userDtoList = userMapper.toDto(records);
        userService.download(userDtoList, response);
    }

    @Log(description = "新增用户")
    @ApiOperation("新增用户")
    @PostMapping("/add")
    // // @PreAuthorize("@el.check('SysUser:add')")
    public BusinessResponse create(@Validated @RequestBody SysUser user) {
//        checkLevel(user);
        String generateSalt = EncryptUtils.generateSalt();
        if (StringUtils.isBlank(user.getPassword())) {
            // 默认密码 123456
            user.setPassword(ShiroUtils.sha256(AdminConstants.DEFAULT_PASSWORD, generateSalt));
        } else {
            user.setPassword(ShiroUtils.sha256(user.getPassword(), generateSalt));
        }
        System.out.println("generateSalt = " + generateSalt);
        user.setSalt(generateSalt);
        userService.create(user, ShiroUtils.getUserEntity());
        return BusinessResponse.ok();
    }

    @Log(description = "修改用户")
    @ApiOperation("修改用户")
    @PutMapping("/update")
    // // @PreAuthorize("@el.check('SysUser:edit')")
    public BusinessResponse update(@Validated @RequestBody SysUser resources) {
//        checkLevel(resources);
        userService.update(resources);
        return BusinessResponse.ok();
    }

    @Log(description = "修改用户：个人中心")
    @ApiOperation("修改用户：个人中心")
    @PutMapping(value = "center")
    public BusinessResponse center(@Validated @RequestBody SysUser resources) {
        Long userId = ShiroUtils.getUserId();
        if (!resources.getUserId().equals(userId)) {
            throw new BusinessErrorException("不能修改他人资料");
        }
        userService.updateCenter(resources);
        return BusinessResponse.ok();
    }

    @Log(description = "删除用户")
    @ApiOperation("删除用户")
    @DeleteMapping("/del")
    // // @PreAuthorize("@el.check('SysUser:del')")
    public BusinessResponse delete(@RequestBody Set<Long> ids) {
        Integer currentUserId = 1;
        List<Integer> integerList = new ArrayList<>();
        integerList.add(currentUserId);
        for (Long id : ids) {
            // roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList())
            Integer currentLevel = Collections.min(integerList);
            Integer optLevel = Collections.min(roleService.findByUsersId(id).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
            if (currentLevel > optLevel) {
                throw new BusinessErrorException("角色权限不足，不能删除：" + userService.findById(id).getUsername());
            }
        }
        userService.delete(ids);
        return BusinessResponse.ok();
    }

    @ApiOperation("修改密码")
    @PostMapping(value = "/updatePass")
    public BusinessResponse updatePass(@RequestBody UserPassReqVo passVo) throws Exception {
        String oldPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, passVo.getOldPass());
        String newPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, passVo.getNewPass());
        // SecurityUtils.getCurrentUsername()
/*        SysUser SysUser = userService.findByName("currentUsername");
        if (!passwordEncoder.matches(oldPass, SysUser.getPassword())) {
            throw new BusinessErrorException("修改失败，旧密码错误");
        }
        if (passwordEncoder.matches(newPass, SysUser.getPassword())) {
            throw new BusinessErrorException("新密码不能与旧密码相同");
        }
        userService.updatePass(SysUser.getUsername(), passwordEncoder.encode(newPass));*/
        return BusinessResponse.ok();
    }

    @ApiOperation("修改头像")
    @PostMapping(value = "/updateAvatar")
    public BusinessResponse updateAvatar(@RequestParam MultipartFile avatar) {
        return BusinessResponse.ok(userService.updateAvatar(avatar));
    }

    @Log(description = "修改邮箱")
    @ApiOperation("修改邮箱")
    @PostMapping(value = "/updateEmail/{code}")
    public BusinessResponse updateEmail(@PathVariable String code, @RequestBody SysUser SysUser) throws Exception {
        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, SysUser.getPassword());
        // String currentUsername = SecurityUtils.getCurrentUsername()
        SysUser userDto = userService.findByName("currentUsername");
/*        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            throw new BusinessErrorException("密码错误");
        }*/
        verificationCodeService.validated(CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey() + SysUser.getEmail(), code);
        userService.updateEmail(userDto.getUsername(), SysUser.getEmail());
        return BusinessResponse.ok();
    }

    /**
     * 如果当前用户的角色级别低于创建用户的角色级别，则抛出权限不足的错误
     *
     * @param resources /
     */
    private void checkLevel(SysUser resources) {
//        Integer currentLevel = Collections.min(roleService.findByUsersId(SecurityUtils.getCurrentUserId()).stream().map(RoleSmallDto::getLevel).collect(Collectors.toList()));
        Integer currentLevel = 2;
//        Integer optLevel = roleService.findByRoles(resources.getRoles());
        Integer optLevel = 1;
        if (currentLevel > optLevel) {
            throw new BusinessErrorException("角色权限不足");
        }
    }
}
