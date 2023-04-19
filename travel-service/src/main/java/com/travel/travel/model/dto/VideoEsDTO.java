package com.travel.travel.model.dto;

import com.travel.travel.model.entity.Video;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 28/3/2023 下午 5:36
 */
// todo 取消注释开启 ES（须先配置 ES）
@Document(indexName = "video")
@Data
public class VideoEsDTO implements Serializable {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 所属用户 id
     */
    private Long userId;

    /**
     * 所属团队 id
     */
    private Long teamId;

    /**
     * 标题
     */
    private String title;

    /**
     * 权限（0：公开 1：部分可见 2：私密）
     */
    private Integer permission;

    /**
     * 标签
     */
    private String tag;

    /**
     * 文章首句话
     */
    private String intro;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 视频状态（0：正常 1：异常 2：删除）
     */
    private Integer videoState;

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;


    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param videoEsDTO
     * @return
     */
    public static Video dtoToObj(VideoEsDTO videoEsDTO) {
        if (videoEsDTO == null) {
            return null;
        }
        Video video = new Video();
        BeanUtils.copyProperties(videoEsDTO, video);
        return video;
    }

    /**
     * 对象转包装类
     *
     * @param video
     * @return
     */
    public static VideoEsDTO objToDto(Video video) {
        if (video == null) {
            return null;
        }
        VideoEsDTO videoEsDTO = new VideoEsDTO();
        BeanUtils.copyProperties(video, videoEsDTO);

        return videoEsDTO;
    }

}
