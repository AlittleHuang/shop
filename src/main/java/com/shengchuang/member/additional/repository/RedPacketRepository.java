package com.shengchuang.member.additional.repository;

import com.shengchuang.member.additional.domain.RedPacket;
import com.shengchuang.common.mvc.repository.CommonRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RedPacketRepository
        extends CommonRepository<RedPacket, Integer>, JpaSpecificationExecutor<RedPacket> {


}