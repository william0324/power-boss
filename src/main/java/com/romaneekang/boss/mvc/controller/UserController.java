package com.romaneekang.boss.mvc.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.romaneekang.boss.constants.BossConst;
import com.romaneekang.boss.convert.UserConvert;
import com.romaneekang.boss.domain.UserInfo;
import com.romaneekang.boss.enums.ajax.Code;
import com.romaneekang.boss.mvc.model.ajax.AjaxResult;
import com.romaneekang.boss.mvc.model.form.UserEditForm;
import com.romaneekang.boss.mvc.model.page.PageInfo;
import com.romaneekang.boss.mvc.model.vo.UserInfoVo;
import com.romaneekang.boss.mvc.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private UserConvert userConvert;

    /**
     * 处理用户编辑请求。<br>
     * 通过@RequestBody注解，将HTTP请求体中的数据绑定到UserEditForm对象上，<br>
     * 并使用@Validated注解进行验证，确保数据的合法性和完整性。<br>
     *
     * @param userForm 包含用户编辑信息的表单对象，已经过验证。
     * @return 返回一个表示操作结果的AjaxResult对象，如果操作成功，则返回OK。
     */
    @PostMapping("/user/edit")
    public AjaxResult userEdit(@RequestBody @Validated UserEditForm userForm) {
        // 调用用户服务层的方法，执行用户编辑操作
        userService.userEdit(userForm);
        // 返回操作成功的结果
        return AjaxResult.OK();
    }

    @GetMapping("/user/pageList")
    public AjaxResult userPageList(@RequestParam Integer pageNo) {
        // 对pageNo进行一定的校验
        pageNo = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        // 分页查询
        IPage<UserInfo> pageResult = userService.userPageList(pageNo, BossConst.DEFAULT_PAGE_SIZE);
        // 获取本次查询的数据
        List<UserInfo> userList = pageResult.getRecords();
        // 当前页数 == pageNo
        long currentPage = pageResult.getCurrent();
        // 总页数
        long totalPage = pageResult.getPages();
        PageInfo pageInfo = new PageInfo(currentPage, totalPage);
        Map<String, Object> data = Map.of("list", userConvert.userInfoListToUserInfoVoList(userList), "page", pageInfo);
        return AjaxResult.OK(data);
    }

    @GetMapping("/user/queryByNo")
    public AjaxResult userQueryByNo(@RequestParam String userNo) {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (StrUtil.isBlank(userNo)) {
            return AjaxResult.error(Code.OPERATOR_PARAM_ERR);
        }
        // 查询用户
        UserInfo userInfo = userService.queryByUserNo(userNo);
        return AjaxResult.OK(userConvert.userInfoToQueryUserInfoVo(userInfo));
    }

    @PostMapping("/user/editStatus")
    public AjaxResult userEditStatus(@RequestParam String userNo) {
        if (StrUtil.isBlank(userNo)) {
            return AjaxResult.error(Code.OPERATOR_PARAM_ERR);
        }
        // 修改用户状态
        int i = userService.userEditStatus(userNo);
        if (i == 1) {
            return AjaxResult.OK();
        } else {
            return AjaxResult.error(Code.USER_EDIT_ERR);
        }
    }
}
