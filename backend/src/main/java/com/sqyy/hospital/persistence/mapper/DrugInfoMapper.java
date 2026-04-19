package com.sqyy.hospital.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqyy.hospital.persistence.entity.DrugInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DrugInfoMapper extends BaseMapper<DrugInfoEntity> {

    @Select("select * from drug_info where id = #{id} for update")
    DrugInfoEntity selectByIdForUpdate(Long id);
}
