package com.sqyy.hospital.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqyy.hospital.persistence.entity.VisitRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VisitRecordMapper extends BaseMapper<VisitRecordEntity> {

    @Select("select * from visit_record where id = #{id} for update")
    VisitRecordEntity selectByIdForUpdate(Long id);
}
