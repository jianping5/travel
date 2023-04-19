package com.travel.official.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.travel.common.common.ErrorCode;
import com.travel.common.constant.CommonConstant;
import com.travel.common.constant.TypeConstant;
import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.reward.ConsumeRecordAddRequest;
import com.travel.common.model.dto.reward.ExchangeRecordAddRequest;
import com.travel.common.model.entity.User;
import com.travel.common.service.InnerUserService;
import com.travel.common.utils.SqlUtils;
import com.travel.common.utils.UserHolder;
import com.travel.official.mapper.DerivativeMapper;
import com.travel.official.model.dto.derivative.DerivativeQueryRequest;
import com.travel.official.model.entity.Derivative;
import com.travel.official.model.entity.Official;
import com.travel.official.model.vo.DerivativeVO;
import com.travel.official.service.DerivativeService;
import com.travel.official.service.OfficialService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【derivative(周边表)】的数据库操作Service实现
* @createDate 2023-03-30 16:50:48
*/
@Service
public class DerivativeServiceImpl extends ServiceImpl<DerivativeMapper, Derivative>
    implements DerivativeService{

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private OfficialService officialService;

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private Gson gson;

    @Override
    public void validDerivative(Derivative derivative, boolean add) {
        if (derivative == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String derivativeName = derivative.getDerivativeName();
        String intro = derivative.getIntro();
        // todo: 这两者可以考虑使用默认值（若用户不传）
        String coverUrl = derivative.getCoverUrl();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(derivativeName, intro), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(derivativeName) && derivativeName.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "周边名称过长");
        }
        if (StringUtils.isNotBlank(intro) && intro.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    @Override
    public DerivativeVO getDerivativeVO(Derivative derivative) {
        DerivativeVO derivativeVO = DerivativeVO.objToVo(derivative);

        // todo：需要关联官方信息吗？

        return derivativeVO;
    }

    @Override
    public Page<DerivativeVO> getDerivativeVOPage(Page<Derivative> derivativePage) {
        List<Derivative> derivativeList = derivativePage.getRecords();
        Page<DerivativeVO> derivativeVOPage = new Page<>(derivativePage.getCurrent(), derivativePage.getSize(), derivativePage.getTotal());
        if (CollectionUtils.isEmpty(derivativeList)) {
            return derivativeVOPage;
        }

        // todo：需要关联官方信息吗？

        // 填充信息
        List<DerivativeVO> derivativeVOList = derivativeList.stream().map(derivative -> {
            DerivativeVO derivativeVO = DerivativeVO.objToVo(derivative);

            return derivativeVO;
        }).collect(Collectors.toList());
        derivativeVOPage.setRecords(derivativeVOList);
        return derivativeVOPage;
    }

    @Override
    public QueryWrapper<Derivative> getQueryWrapper(DerivativeQueryRequest derivativeQueryRequest) {

        QueryWrapper<Derivative> queryWrapper = new QueryWrapper<>();
        if (derivativeQueryRequest == null) {
            return queryWrapper;
        }
        // 获取查询条件中的字段
        String searchText = derivativeQueryRequest.getSearchText();
        String sortField = derivativeQueryRequest.getSortField();
        String sortOrder = derivativeQueryRequest.getSortOrder();
        Long id = derivativeQueryRequest.getId();
        String derivativeName = derivativeQueryRequest.getDerivativeName();
        String intro = derivativeQueryRequest.getIntro();
        Long userId = derivativeQueryRequest.getUserId();
        Integer derivativeState = derivativeQueryRequest.getDerivativeState();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("team_name", searchText).or().like("intro", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(derivativeName), "team_name", derivativeName);
        queryWrapper.like(StringUtils.isNotBlank(intro), "intro", intro);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        queryWrapper.eq(ObjectUtils.isNotEmpty(derivativeState), "derivative_state", 0);
        return queryWrapper;
    }

    @Override
    public Derivative addDerivative(Derivative derivative) {
        // todo: 判重（周边名字）是否需要？

        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long officialId = loginUser.getOfficialId();
        Integer typeId = loginUser.getTypeId();
        // 设置用户 id
        derivative.setUserId(loginUserId);
        // 设置官方 id
        derivative.setOfficialId(officialId);
        // 设置官方类型
        derivative.setTypeId(typeId);

        // 添加到数据库中
        boolean saveResult = this.save(derivative);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);

        // 获取该 team
        Derivative newDerivative = this.getById(derivative.getId());

        return newDerivative;
    }

    @Override
    public boolean deleteDerivative(Derivative derivative) {
        // todo: 考虑加事务

        // 将周边状态设置为已下架
        derivative.setDerivativeState(2);
        boolean result = this.updateById(derivative);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public boolean updateDerivative(Derivative derivative) {
        // todo：需要判重吗？

        // 判断当前用户是否为当前周边的创建人
        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();
        Long derivativeUserId = derivative.getUserId();
        ThrowUtils.throwIf(!loginUserId.equals(derivativeUserId), ErrorCode.NO_AUTH_ERROR);

        // 更新数据库
        boolean updateResult = this.updateById(derivative);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public List<DerivativeVO> listRcmdDerivativeVO(long current, long size) {
        // 创建团队视图体数组
        List<DerivativeVO> derivativeVOList = new ArrayList<>();

        // 判断有无缓存
        RList<String> derivativeVORList = redissonClient.getList("travel:derivative:recommend");

        // 若有，则直接从缓存中读取
        if (CollectionUtils.isNotEmpty(derivativeVORList)) {
            for (long i = (current-1)*size; i < current*size; i++) {
                String json = derivativeVORList.get((int) i);
                DerivativeVO derivativeVO = gson.fromJson(json, DerivativeVO.class);
                derivativeVOList.add(derivativeVO);
            }
            return derivativeVOList;
        }

        // 若无，则从数据库中读取
        QueryWrapper<Derivative> derivativeQueryWrapper = new QueryWrapper<>();
        derivativeQueryWrapper.last("order by 5*obtain_count+3*view_count desc limit " + size);
        derivativeVOList = this.list(derivativeQueryWrapper).stream().map(derivative -> getDerivativeVO(derivative)).collect(Collectors.toList());

        // todo: 并将写缓存的任务添加到消息队列
        // 定义交换机名称
        String exchangeName = "travel.topic";

        // 定义消息
        String message = "cache.derivative";

        // 发送消息，让对应线程将数据写入缓存
        rabbitTemplate.convertAndSend(exchangeName, "cache.derivative", message);

        return derivativeVOList;
    }

    @Override
    public Official obtainDerivative(Long id, Integer obtainMethod) {

        User loginUser = UserHolder.getUser();
        Long loginUserId = loginUser.getId();


        // todo：待优化
        Derivative derivative = this.getById(id);
        Long officialId = derivative.getOfficialId();

        // todo：返回官方用户，获取联系方式
        QueryWrapper<Official> officialQueryWrapper = new QueryWrapper<>();
        officialQueryWrapper.eq("id", officialId);
        officialQueryWrapper.select("id", "user_id", "official_name", "contact");
        Official official = officialService.getOne(officialQueryWrapper);

        // 增加该周边的获取次数
        UpdateWrapper<Derivative> derivativeUpdateWrapper = new UpdateWrapper<>();
        derivativeUpdateWrapper.eq("id", id);
        derivativeUpdateWrapper.setSql("obtain_count = obtain_count + 1");

        // 获取周边价格
        double price = derivative.getPrice();

        String exchangeName = "travel.topic";

        // 现金获取
        if (obtainMethod == 0) {
            // todo：并记录当前用户的行为到用户行为表中（暂时不需要）


            // todo：将消费记录添加到兑换记录表中，并生成唯一凭证
            ConsumeRecordAddRequest consumeRecordAddRequest = new ConsumeRecordAddRequest();
            consumeRecordAddRequest.setUserId(loginUserId);
            // todo：此处消耗的是现金（就不保存了）
            consumeRecordAddRequest.setTokenAccount(0);
            consumeRecordAddRequest.setContent("您使用现金购买了周边");
            consumeRecordAddRequest.setConsumeType(TypeConstant.DERIVATIVE.getTypeIndex());
            consumeRecordAddRequest.setConsumeId(id);
            rabbitTemplate.convertAndSend(exchangeName, "consume.derivative", consumeRecordAddRequest);

        }

        // 获取周边代币
        int token = (int) price;

        // 代币兑换
        if (obtainMethod == 1) {
            // todo：消耗代币，并判断是否足够
            innerUserService.updateToken(loginUserId, token, false);

            // todo：将兑换记录添加到兑换记录表中，并生成唯一凭证
            ExchangeRecordAddRequest exchangeRecordAddRequest = new ExchangeRecordAddRequest();
            exchangeRecordAddRequest.setDerivativeId(id);
            exchangeRecordAddRequest.setTokenAccount(token);
            exchangeRecordAddRequest.setUserId(loginUserId);
            String certificate = DigestUtils.md5DigestAsHex(("derivative").getBytes());
            exchangeRecordAddRequest.setCertificate(certificate);
            rabbitTemplate.convertAndSend(exchangeName, "exchange.derivative", exchangeRecordAddRequest);
            // todo：记录当前用户的行为到用户行为表中（暂时不需要）

        }

        return official;
    }
}




