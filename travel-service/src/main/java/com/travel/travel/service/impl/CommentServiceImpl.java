package com.travel.travel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.user.UserDTO;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.travel.model.entity.Comment;
import com.travel.travel.model.request.CommentQueryRequest;
import com.travel.travel.model.vo.CommentVO;
import com.travel.travel.model.vo.CommentVOBlock;
import com.travel.travel.service.CommentService;
import com.travel.travel.mapper.CommentMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【comment(评论表)】的数据库操作Service实现
* @createDate 2023-03-24 19:23:06
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService, com.travel.common.service.CommentService {
    @DubboReference
    private InnerUserService innerUserService;
    @Resource()
    private RedissonClient redissonClient;
    @Override
    public void validComment(Comment comment, boolean add) {
        if (comment == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Integer commentObjType = comment.getCommentObjType();
        Long commentObjId = comment.getCommentObjId();
        Long parentUserId = comment.getParentUserId();
        Long topId = comment.getTopId();
        String content = comment.getContent();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(content), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(ObjectUtils.anyNull(commentObjType,commentObjId,parentUserId,topId),ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(content) && content.length() > 800) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "评论内容过长");
        }
    }

    public CommentVO getCommentVO(Comment comment) {
        CommentVO commentVO = CommentVO.objToVo(comment);

        //查询用户信息
        Long userId = comment.getUserId();
        UserDTO user = innerUserService.getUser(userId);
        if(user!=null){
            commentVO.setUserName(user.getUserName());
            commentVO.setUserAvatarUrl(user.getUserAvatar());
        }else {
            commentVO.setUserName("用户不存在");
            commentVO.setUserAvatarUrl("");
        }
        //查询用户信息
        Long parentUserId = comment.getParentUserId();
        UserDTO parentUser = innerUserService.getUser(parentUserId);
        if(user!=null){
            commentVO.setParentUserName(parentUser.getUserName());
            commentVO.setParentUserAvatarUrl(user.getUserAvatar());
        }else {
            commentVO.setParentUserName("用户不存在");
            commentVO.setParentUserAvatarUrl("");
        }

        //todo:注入点赞状态

        return commentVO;
    }

    @Override
    public Page<CommentVOBlock> queryComment(CommentQueryRequest commentQueryRequest) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (commentQueryRequest == null) {
            return null;
        }
        // 获取查询条件中的字段
        String searchText = commentQueryRequest.getSearchText();
        Integer commentObjType = commentQueryRequest.getCommentObjType();
        Long commentObjId = commentQueryRequest.getCommentObjId();
        String sortField = commentQueryRequest.getSortField();
        String sortOrder = commentQueryRequest.getSortOrder();
        Long pageSize = commentQueryRequest.getPageSize();
        Long current = commentQueryRequest.getCurrent();

        //内容模糊限制
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("content", searchText);
        }

        //未删除评论
        queryWrapper.eq("is_deleted", 0);

        //评论归属对象的类型
        queryWrapper.eq(ObjectUtils.isNotEmpty(commentObjType), "comment_obj_type", commentObjType);

        //评论归属对象的id
        queryWrapper.eq(ObjectUtils.isNotEmpty(commentObjId), "comment_obj_id", commentObjId);

        //排序
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        //一级评论
        queryWrapper.eq("top_id", 0);

        Page<Comment> commentPage = page(new Page<Comment>(current, pageSize), queryWrapper);
        List<Comment> topComments = commentPage.getRecords();
        List<Long> topIds = topComments.stream().map(k -> k.getId()).collect(Collectors.toList());
        List<Comment> comments = list(new QueryWrapper<Comment>().in("top_id", topIds).eq("is_deleted", 0));
        List<CommentVOBlock> commentVOBlocks = new ArrayList<>();
        topComments.stream().forEach(top->{
            CommentVOBlock commentVOBlock = new CommentVOBlock();
            commentVOBlock.setTopCommentVO(getCommentVO(top));
            List<CommentVO> commentVOS = comments.stream().filter(com -> com.getTopId().equals(top.getId())).sorted(new Comparator<Comment>() {
                @Override
                public int compare(Comment o1, Comment o2) {
                    return o2.getCreateTime().compareTo(o1.getCreateTime());
                }
            }).map(h -> getCommentVO(h)).collect(Collectors.toList());
            commentVOBlock.setCommentVOS(commentVOS);
            commentVOBlocks.add(commentVOBlock);
        });
        Page<CommentVOBlock> page = new Page<>(current,pageSize);
        page.setTotal(commentPage.getTotal());
        page.setPages(commentPage.getPages());
        page.setRecords(commentVOBlocks);
        return page;
    }

    @Override
    public Comment addComment(Comment comment) {
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        // 设置用户 id
        comment.setUserId(loginUserId);

        //todo:考虑事务
        // 添加到数据库中
        boolean save = save(comment);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        // 获取该文章
        Comment newComment = this.getById(comment.getId());
        return newComment;
    }

    @Override
    public boolean deleteComment(Comment comment) {
        // 将评论状态设置为已删除
        comment.setIsDeleted(1);
        boolean result = this.updateById(comment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

}




