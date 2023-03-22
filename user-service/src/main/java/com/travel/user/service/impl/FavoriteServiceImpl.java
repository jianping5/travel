package com.travel.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.user.model.entity.Favorite;
import com.travel.user.service.FavoriteService;
import com.travel.user.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【favorite(收藏夹)】的数据库操作Service实现
* @createDate 2023-03-22 14:34:09
*/
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite>
    implements FavoriteService{

}




