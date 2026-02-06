package cn.master.horde.common.service;

import cn.master.horde.common.constants.ModuleConstants;
import cn.master.horde.common.util.NodeSortUtils;
import cn.master.horde.model.dto.BaseTreeNode;
import cn.master.horde.model.dto.api.ModuleCountDTO;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : 11's papa
 * @since : 2026/2/5, 星期四
 **/
@Component
public class ModuleTreeService {
    protected static final long LIMIT_POS = NodeSortUtils.DEFAULT_NODE_INTERVAL_POS;

    public BaseTreeNode getDefaultModule(String name) {
        // 默认模块下不允许创建子模块。  它本身也就是叶子节点。
        return new BaseTreeNode(ModuleConstants.DEFAULT_NODE_ID, name, ModuleConstants.NODE_TYPE_DEFAULT, ModuleConstants.ROOT_NODE_PARENT_ID);
    }

    public List<BaseTreeNode> buildTreeAndCountResource(List<BaseTreeNode> traverseList, @NotNull List<ModuleCountDTO> moduleCountDTOList, boolean haveVirtualRootNode, String virtualRootName) {
        // 构建模块树
        List<BaseTreeNode> baseTreeNodeList = this.buildTreeAndCountResource(traverseList, haveVirtualRootNode, virtualRootName);
        // 构建模块节点统计的数据结构
        Map<String, Integer> resourceCountMap = moduleCountDTOList.stream().collect(Collectors.toMap(ModuleCountDTO::getModuleId, ModuleCountDTO::getDataCount));
        // 为每个节点赋值资源数量
        sumModuleResourceCount(baseTreeNodeList, resourceCountMap);
        return baseTreeNodeList;
    }

    private void sumModuleResourceCount(List<BaseTreeNode> baseTreeNodeList, Map<String, Integer> resourceCountMap) {
        for (BaseTreeNode node : baseTreeNodeList) {
            // 赋值子节点的资源数量
            this.sumModuleResourceCount(node.getChildren(), resourceCountMap);
            // 当前节点的资源数量（包含子节点）
            long childResourceCount = 0;
            for (BaseTreeNode childNode : node.getChildren()) {
                childResourceCount += childNode.getCount();
            }

            if (resourceCountMap.containsKey(node.getId())) {
                node.setCount(childResourceCount + resourceCountMap.get(node.getId()));
            } else {
                node.setCount(childResourceCount);
            }

        }
    }

    public List<BaseTreeNode> buildTreeAndCountResource(List<BaseTreeNode> traverseList, boolean haveVirtualRootNode, String virtualRootName) {

        List<BaseTreeNode> baseTreeNodeList = new ArrayList<>();
        if (haveVirtualRootNode) {
            BaseTreeNode defaultNode = this.getDefaultModule(virtualRootName);
            defaultNode.genModulePath(null);
            baseTreeNodeList.add(defaultNode);
        }
        int lastSize = 0;
        Map<String, BaseTreeNode> baseTreeNodeMap = new HashMap<>();

        // 根节点预先处理
        baseTreeNodeList.addAll(traverseList.stream().filter(treeNode -> StringUtils.equalsIgnoreCase(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)).toList());
        baseTreeNodeList.forEach(item -> baseTreeNodeMap.put(item.getId(), item));
        traverseList = (List<BaseTreeNode>) CollectionUtils.removeAll(traverseList, baseTreeNodeList);

        // 循环处理子节点
        while (CollectionUtils.isNotEmpty(traverseList) && traverseList.size() != lastSize) {
            lastSize = traverseList.size();
            List<BaseTreeNode> notMatchedList = new ArrayList<>();
            for (BaseTreeNode treeNode : traverseList) {
                if (!baseTreeNodeMap.containsKey(treeNode.getParentId()) && !StringUtils.equalsIgnoreCase(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
                    notMatchedList.add(treeNode);
                    continue;
                }
                BaseTreeNode node = new BaseTreeNode(treeNode.getId(), treeNode.getName(), treeNode.getType(), treeNode.getParentId());
                node.genModulePath(baseTreeNodeMap.get(treeNode.getParentId()));
                baseTreeNodeMap.put(treeNode.getId(), node);

                if (StringUtils.equalsIgnoreCase(treeNode.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
                    baseTreeNodeList.add(node);
                } else if (baseTreeNodeMap.containsKey(treeNode.getParentId())) {
                    baseTreeNodeMap.get(treeNode.getParentId()).addChild(node);
                }
            }
            traverseList = notMatchedList;
        }
        return baseTreeNodeList;
    }
}
