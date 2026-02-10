package cn.master.horde.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/2/9, 星期一
 **/
public class ApiDataUtils {
    private static final JsonMapper objectMapper = JsonMapper.builder()
            .changeDefaultPropertyInclusion(include ->
                    include.withValueInclusion(JsonInclude.Include.NON_NULL))
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .findAndAddModules()
            .build();
    // 默认内置子组件，如果有新的子组件，需要在这里添加
    static final List<NamedType> namedTypes = new LinkedList<>();

    // static {
    //     // 默认内置的子组件
    //     namedTypes.add(new NamedType(MsHTTPElement.class, MsHTTPElement.class.getSimpleName()));
    //     namedTypes.add(new NamedType(MsCommonElement.class, MsCommonElement.class.getSimpleName()));
    //     namedTypes.add(new NamedType(MsIfController.class, MsIfController.class.getSimpleName()));
    //     namedTypes.add(new NamedType(MsLoopController.class, MsLoopController.class.getSimpleName()));
    //     namedTypes.add(new NamedType(MsOnceOnlyController.class, MsOnceOnlyController.class.getSimpleName()));
    //     namedTypes.add(new NamedType(MsConstantTimerController.class, MsConstantTimerController.class.getSimpleName()));
    //     namedTypes.add(new NamedType(MsScriptElement.class, MsScriptElement.class.getSimpleName()));
    //     namedTypes.add(new NamedType(MsJMeterComponent.class, MsJMeterComponent.class.getSimpleName()));
    //     setObjectMapper(objectMapper);
    //     namedTypes.forEach(objectMapper::registerSubtypes);
    // }
}
