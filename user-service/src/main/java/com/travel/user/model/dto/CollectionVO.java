package com.travel.user.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.travel.user.model.entity.Collection;
import com.travel.user.model.entity.Follow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zgy
 * @create 2023-04-12 16:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionVO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 收藏夹 id
     */
    private Long favoriteId;

    /**
     * 收藏夹名称
     */
    private String favoriteName;

    /**
     * 收藏对象类型
     */
    private Integer collectionObjType;

    /**
     * 收藏对象 id
     */
    private Long collectionObjId;


    private static final long serialVersionUID = 1L;
    public static CollectionVO objToVo(Collection collection){
        if(collection == null){
            return null;
        }
        CollectionVO collectionVO = new CollectionVO();
        BeanUtil.copyProperties(collection,collectionVO);
        return collectionVO;
    }

}
