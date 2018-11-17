package com.shengchuang.member.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import com.shengchuang.member.additional.service.RechargeService;
import com.shengchuang.member.additional.service.UserLevelService;
import com.shengchuang.common.mvc.pageable.PageRequestMap;
import com.shengchuang.common.mvc.view.JsonMap;
import com.shengchuang.common.mvc.view.JsonVO;
import com.shengchuang.common.util.Assert;
import com.shengchuang.common.util.PoiUtil;
import com.shengchuang.common.util.TimeUtil;
import com.shengchuang.common.util.WebUtil;
import com.shengchuang.member.core.domain.Recharge;
import com.shengchuang.member.core.domain.User;
import com.shengchuang.member.core.domain.Withdraw;
import com.shengchuang.member.core.domain.enmus.Event;
import com.shengchuang.member.core.service.BalanceService;
import com.shengchuang.member.core.service.WithdrawService;
import com.shengchuang.base.AbstractController;

@RestController
@RequestMapping("/admin")
public class AdminRestController extends AbstractController {

    private static boolean done = false;
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private UserLevelService userLevelService;

    /**
     * @param @param  fileNames
     * @param @param  request
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: encodeFileName
     * @Description: 导出文件转换文件名称编码
     */
    public static String encodeFileName(String fileNames, HttpServletRequest request) {
        return WebUtil.encodeFileName(fileNames, request);
    }

    /**
     * 充值记录
     *
     * @return
     */
    @RequestMapping("/balance/recharge/log")
    public View rechargeLog() {
        PageRequestMap pageRequest = getPageRequestMap();
        Page<Recharge> page = rechargeService.listByUserId(pageRequest);
        rechargeService.loadUsername(page.getContent());
        return new JsonVO(page);
    }

    /**
     * 提现记录
     *
     * @return
     */
    @RequestMapping("/balance/withdraw/log")
    public View withdrawLog() {
        Page<Withdraw> page = withdrawService.getPage(getPageRequestMap());
        withdrawService.loadUsername(page.getContent());
        return new JsonVO(page);
    }

    /**
     * 同意充值
     *
     * @param id
     * @return
     */
    @RequestMapping("/recharge/agree")
    public View rechargeAgree(Integer id) {
        rechargeService.agree(id);
        return new JsonMap("操作成功");
    }

    /**
     * 检验交易密码
     *
     * @return
     */
    @RequestMapping("/check/password")
    public View checkAdminPwd() {
        checkAdminSecondPwd();
        return new JsonMap("操作成功");
    }

    /**
     * 同意提现
     *
     * @param id
     * @return
     */
    @RequestMapping("/withdraw/agree")
    public View withdrawAgree(Integer id) {
        withdrawService.agree(id);
        return new JsonMap("操作成功");
    }

    @RequestMapping("/recharge/disagree")
    public View rechargeDisagree(Integer id) {
        rechargeService.disagree(id);
        return new JsonMap("操作成功");
    }

    /**
     * 拒绝提现
     *
     * @param id
     * @return
     */
    @RequestMapping("/withdraw/disagree")
    public View withdrawDisagree(Integer id) {
        withdrawService.disagree(id);
        return new JsonMap("操作成功");
    }

    /**
     * 身份证信息审核通过
     *
     * @param id 用户id
     * @return
     */
    @RequestMapping("/idStatus/agree")
    public View idStatusAgree(Integer id) {
    	User user = userService.getOne(id);
    	Assert.notNull(user, "参数错误");
    	userService.agree(user);
        return new JsonMap("操作成功");
    }

    @RequestMapping("/idStatus/disagree")
    public View idStatusDisagree(Integer id) {
    	User user = userService.getOne(id);
    	Assert.notNull(user, "参数错误");
    	userService.disagree(user);
        return new JsonMap("操作成功");
    }
    /**
     * 系统充值
     *
     * @param username
     * @param type
     * @param amount
     * @return
     */
    @RequestMapping("/recharge")
    public View recharge(String username, Integer type, Double amount, String info, Integer operation) {
        Event event = operation == null ? Event.RECHARGE_BY_ADMIN : Event.of(operation);
        rechargeService.rechargeByAdmin(username, amount, type, info, event);
        return new JsonMap("操作成功");
    }

    /**
     * 统计用户总人数和每天新增用户
     *
     * @return
     */
    @RequestMapping("/user/count")
    public View userCount() {
        Long sumUsers = userService.getSumUsers();
        Long newAddUsers = userService.getNewAddUsers();
        return new JsonMap().add("sumUsers", sumUsers)
                .add("newAddUsers", newAddUsers);
    }

    /**
     * 会员列表导出excle
     *
     * @return
     */
    @RequestMapping("/user/exportExcel")
    @ResponseBody
    public void userExcel(HttpServletRequest request) {
        List<User> userList = userService.getUserList(null, getPageRequestMap());
        //  userLevelService.loadUscLevel(userList);
        userService.loadPusername(userList);
        //List<BalanceLog> balanceLogList = balanceLogService.getBalanceLog(null, getPageRequestMap());
        //获取明细名称配置
        String excleName = encodeFileName("会员列表", request);
        //logger.info("userList size:" + userList.size());
        HttpServletResponse httpResponse = response();
        Map<Integer, User> map = userList.parallelStream().collect(Collectors.toMap(u -> u.getId(), u -> u));
        try (XSSFWorkbook workbook = new XSSFWorkbook(); OutputStream out = httpResponse.getOutputStream()) {
            Map<String, Function<User, String>> XSSFWorkbookMap = new LinkedHashMap<>();
            XSSFWorkbookMap.put("ID编号", user -> map.get(user.getId()).getUsername());
            XSSFWorkbookMap.put("姓名", user -> map.get(user.getId()).getActualName());
            XSSFWorkbookMap.put("身份证号码", user -> map.get(user.getId()).getIdCard());

            XSSFWorkbookMap.put("手机号码", user -> map.get(user.getId()).getPhone());
            XSSFWorkbookMap.put("推荐人ID编号", user -> map.get(user.getId()).getReferrer() == null ? ""
                    : map.get(user.getId()).getReferrer().getUsername());
            XSSFWorkbookMap.put("注册时间",
                    user -> TimeUtil.DEFAULT_DATE_TIME_FORMATTER.get().format(map.get(user.getId()).getRegistTime()));
            PoiUtil.writeXSSFWorkbook(excleName, userList, workbook, XSSFWorkbookMap);
            httpResponse.setContentType("application/vnd.ms-excel;charset=utf-8");
//            httpResponse.addHeader(
//                    "Content-Disposition",
//                    "attachment;filename=" + new String((excleName + ".xlsx").getBytes(), "ISO-8859-1"));
            httpResponse.addHeader(
                    "Content-Disposition",
                    "attachment;filename=" + excleName + ".xlsx");
            workbook.write(out);
            //    logger.info("Content-Length:" + response().getHeader("Content-Length"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 明细导出excle
     *
     * @return
     */
    @RequestMapping("/withdraw/exportExcel")
    @ResponseBody
    public void withdrawExcel(HttpServletRequest request) {
        List<Withdraw> withdrawList = withdrawService.getWithdraw(null, getPageRequestMap());
        //获取明细名称配置
        String excleName = encodeFileName("提现记录", request);
        // logger.info("balanceLogList size:" + balanceLogList.size());
        HttpServletResponse httpResponse = response();
        List<User> users = userService.createCriteria()
                .andIn("id", withdrawList.stream().map(b -> b.getUserId()).collect(Collectors.toSet()))
                .getList();
        Map<Integer, User> map = users.parallelStream().collect(Collectors.toMap(u -> u.getId(), u -> u));
        try (XSSFWorkbook workbook = new XSSFWorkbook(); OutputStream out = httpResponse.getOutputStream()) {
            Map<String, Function<Withdraw, String>> XSSFWorkbookMap = new LinkedHashMap<>();
            XSSFWorkbookMap.put("ID编号", withdraw -> map.get(withdraw.getUserId()) == null ? null :
                    map.get(withdraw.getUserId()).getUsername());
            XSSFWorkbookMap.put("提现金额", withdraw -> String.valueOf(withdraw.getAmount()));
            XSSFWorkbookMap.put("收款类型", withdraw -> withdraw.getPayInfo().getDisplayType());
            XSSFWorkbookMap.put("提现ID编号", withdraw -> withdraw.getPayInfo().getName());
            XSSFWorkbookMap.put("提现帐号", withdraw -> withdraw.getPayInfo().getAccount());
            XSSFWorkbookMap.put("状态", withdraw -> withdraw.getStatusname());
            XSSFWorkbookMap.put("时间",
                    withdraw -> TimeUtil.DEFAULT_DATE_TIME_FORMATTER.get().format(withdraw.getCreateTime()));
            PoiUtil.writeXSSFWorkbook(excleName, withdrawList, workbook, XSSFWorkbookMap);
            httpResponse.setContentType("application/vnd.ms-excel;charset=utf-8");
            httpResponse.addHeader(
                    "Content-Disposition",
                    "attachment;filename=" + excleName + ".xlsx");
            workbook.write(out);
            //    logger.info("Content-Length:" + response().getHeader("Content-Length"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
