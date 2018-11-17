package com.shengchuang.member.additional.service;

import com.shengchuang.member.additional.domain.PwdProtected;
import com.shengchuang.member.additional.repository.PwdProtectedRepository;
import com.shengchuang.common.util.Assert;
import com.shengchuang.base.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PwdProtectedService extends AbstractService<PwdProtected, Integer> {

    @Autowired
    private PwdProtectedRepository pwdProtectedRepository;

    @Transactional
    public void update(PwdProtected pwdProtected) {
        Assert.notNull(pwdProtected.getType(), "请选择密保问题");
        Assert.notNull(pwdProtected.getAnswer(), "请填写密保答案");
        pwdProtectedRepository.deleteByUserId(pwdProtected.getUserId());
        save(pwdProtected);
    }

    public boolean existsByUserIdAndTypeAndAnswer(int userId, int type, String answer) {
        return pwdProtectedRepository.existsByUserIdAndTypeAndAnswer(userId, type, answer);
    }
}
