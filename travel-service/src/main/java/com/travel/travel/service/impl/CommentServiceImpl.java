package com.travel.travel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.travel.model.entity.Comment;
import com.travel.travel.service.CommentService;
import com.travel.travel.mapper.CommentMapper;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【comment(评论表)】的数据库操作Service实现
* @createDate 2023-03-24 19:23:06
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

}




