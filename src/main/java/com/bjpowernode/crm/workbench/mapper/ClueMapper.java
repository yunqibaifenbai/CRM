package com.bjpowernode.crm.workbench.mapper;

import com.bjpowernode.crm.workbench.domain.Clue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClueMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Sun Sep 03 01:15:28 CST 2023
     */
    int deleteClueById(String clueId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Sun Sep 03 01:15:28 CST 2023
     */
    int insert(Clue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Sun Sep 03 01:15:28 CST 2023
     */
    int insertClue(Clue clue);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Sun Sep 03 01:15:28 CST 2023
     */
    Clue selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Sun Sep 03 01:15:28 CST 2023
     */
    int updateByPrimaryKeySelective(Clue record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_clue
     *
     * @mbggenerated Sun Sep 03 01:15:28 CST 2023
     */
    int updateByPrimaryKey(Clue record);

    /*
    * 根据分页查询线索列表
    * */
    List<Clue> selectClueByConditionForPage(Map<String,Object> map);

    /*
    * 根据条件查询线索列表总数
    * */
    int selectCountClueByCondition(Map<String,Object> map);

    Clue selectClueForDerailById(String clueId);

    Clue selectClueById(String clueId);
}