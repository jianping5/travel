package com.travel.official.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.official.model.entity.Review;
import com.travel.official.service.ReviewService;
import com.travel.official.mapper.ReviewMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【review(点评表)】的数据库操作Service实现
* @createDate 2023-03-24 19:26:42
*/
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review>
    implements ReviewService{

}




