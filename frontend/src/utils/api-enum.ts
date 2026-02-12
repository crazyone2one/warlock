// 接口参数表格的参数类型
export const RequestParams = {
    STRING: 'string',
    INTEGER: 'integer',
    NUMBER: 'number',
    BOOLEAN: 'boolean',
    ARRAY: 'array',
    JSON: 'json',
    FILE: 'file',
} as const;
export type RequestParamsType = typeof RequestParams[keyof typeof RequestParams];
// 接口请求方法
export const RequestMethods = {
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    DELETE: 'DELETE',
    PATCH: 'PATCH',
    OPTIONS: 'OPTIONS',
    HEAD: 'HEAD',
    CONNECT: 'CONNECT',
} as const;
export type RequestMethodsType = typeof RequestMethods[keyof typeof RequestMethods];
// 接口断言类型
export const ResponseAssertion = {
    RESPONSE_CODE: 'RESPONSE_CODE',
    RESPONSE_BODY: 'RESPONSE_BODY',
    RESPONSE_HEADERS: 'RESPONSE_HEADERS',
    RESPONSE_HEADER: 'RESPONSE_HEADER',
    RESPONSE_TIME: 'RESPONSE_TIME',
    SCRIPT: 'SCRIPT',
    VARIABLE: 'VARIABLE',
} as const;
export type ResponseAssertionType = typeof ResponseAssertion[keyof typeof ResponseAssertion];
// 接口断言-断言匹配条件
export const RequestAssertionCondition = {
    CONTAINS: 'CONTAINS',
    EMPTY: 'EMPTY',
    EQUALS: 'EQUALS',
    END_WITH: 'END_WITH',
    GT: 'GT',
    GT_OR_EQUALS: 'GT_OR_EQUALS',
    LENGTH_EQUALS: 'LENGTH_EQUALS',
    LENGTH_GT: 'LENGTH_GT',
    LENGTH_GT_OR_EQUALS: 'LENGTH_GT_OR_EQUALS',
    LENGTH_LT: 'LENGTH_LT',
    LENGTH_LT_OR_EQUALS: 'LENGTH_LT_OR_EQUALS',
    LENGTH_NOT_EQUALS: 'LENGTH_NOT_EQUALS',
    LT: 'LT',
    LT_OR_EQUALS: 'LT_OR_EQUALS',
    NOT_CONTAINS: 'NOT_CONTAINS',
    NOT_EMPTY: 'NOT_EMPTY',
    NOT_EQUALS: 'NOT_EQUALS',
    START_WITH: 'START_WITH',
} as const;
export type RequestAssertionConditionType = typeof RequestAssertionCondition[keyof typeof RequestAssertionCondition];

// 接口断言-响应体断言-文档类型
export const ResponseBodyAssertionDocument = {
    JSON: 'JSON',
    XML: 'XML',
} as const;
export type ResponseBodyAssertionDocumentType = typeof ResponseBodyAssertionDocument[keyof typeof ResponseBodyAssertionDocument];
// 接口断言-响应体断言类型
export const ResponseBodyAssertion = {
    DOCUMENT: 'DOCUMENT',
    JSON_PATH: 'JSON_PATH',
    REGEX: 'REGEX',
    XPATH: 'XPATH',
    SCRIPT: 'SCRIPT',
} as const;
export type ResponseBodyAssertionType = typeof ResponseBodyAssertion[keyof typeof ResponseBodyAssertion];
// 接口断言-响应体断言-文档断言类型
export const ResponseBodyDocumentAssertion = {
    ARRAY: 'array',
    BOOLEAN: 'boolean',
    INTEGER: 'integer',
    NUMBER: 'number',
    STRING: 'string',
} as const;
export type ResponseBodyDocumentAssertionType = typeof ResponseBodyDocumentAssertion[keyof typeof ResponseBodyDocumentAssertion];

// 接口断言-响应体断言-文档断言类型
export const RequestExtractExpressionEnum = {
    REGEX: 'REGEX',
    JSON_PATH: 'JSON_PATH',
    X_PATH: 'X_PATH',
} as const;
export type RequestExtractExpressionEnumType = typeof RequestExtractExpressionEnum[keyof typeof RequestExtractExpressionEnum];

export const LanguageEnum = {
    PLAINTEXT: 'PLAINTEXT' as const,
    JAVASCRIPT: 'JAVASCRIPT' as const,
    TYPESCRIPT: 'TYPESCRIPT' as const,
    CSS: 'CSS' as const,
    LESS: 'LESS' as const,
    SASS: 'SASS' as const,
    HTML: 'HTML' as const,
    SQL: 'SQL' as const,
    JSON: 'JSON' as const,
    JAVA: 'JAVA' as const,
    PYTHON: 'PYTHON' as const,
    XML: 'XML' as const,
    YAML: 'YAML' as const,
    SHELL: 'SHELL' as const,
    BEANSHELL: 'BEANSHELL' as const,
    BEANSHELL_JSR233: 'BEANSHELL_JSR233' as const,
    GROOVY: 'GROOVY' as const,
    NASHORNSCRIPT: 'NASHORNSCRIPT' as const,
    RHINOSCRIPT: 'RHINOSCRIPT' as const,
} as const;
export type Language = (typeof LanguageEnum)[keyof typeof LanguageEnum];

// 接口请求-前后置操作-处理器类型
export const RequestConditionProcessorEnum = {
    SCRIPT: 'SCRIPT',
    SQL: 'SQL',
    TIME_WAITING: 'TIME_WAITING',
    EXTRACT: 'EXTRACT',
    SCENARIO_SCRIPT: 'ENV_SCENARIO_SCRIPT',
    REQUEST_SCRIPT: 'ENV_REQUEST_SCRIPT',
} as const;
export type RequestConditionProcessor = typeof RequestConditionProcessorEnum[keyof typeof RequestConditionProcessorEnum];
// 接口请求体格式
export const RequestBodyFormatEnum = {
    NONE: 'NONE',
    FORM_DATA: 'FORM_DATA',
    WWW_FORM: 'WWW_FORM',
    JSON: 'JSON',
    XML: 'XML',
    RAW: 'RAW',
    BINARY: 'BINARY',
} as const;
export type RequestBodyFormat = typeof RequestBodyFormatEnum[keyof typeof RequestBodyFormatEnum];

export const RequestContentTypeEnum = {
    JSON: 'application/json',
    TEXT: 'application/text',
    JAVASCRIPT: 'application/javascript',
    OCTET_STREAM: 'application/octet-stream',
} as const;
export type RequestContentType = typeof RequestContentTypeEnum[keyof typeof RequestContentTypeEnum];

export const RequestExtractEnvEnum = {
    ENVIRONMENT: 'ENVIRONMENT',
    TEMPORARY: 'TEMPORARY',
} as const;
export type RequestExtractEnv = typeof RequestExtractEnvEnum[keyof typeof RequestExtractEnvEnum];
// 接口请求-参数提取-表达式匹配结果规则类型
export const RequestExtractResultMatchingRuleEnum = {
    ALL: 'ALL',
    RANDOM: 'RANDOM',
    SPECIFIC: 'SPECIFIC',
} as const;
export type RequestExtractResultMatchingRule = typeof RequestExtractResultMatchingRuleEnum[keyof typeof RequestExtractResultMatchingRuleEnum];

// 接口请求-参数提取-表达式匹配结果规则类型
export const RequestExtractExpressionRuleEnum = {
    EXPRESSION: 'EXPRESSION',
    GROUP: 'GROUP',
} as const;
export type RequestExtractExpressionRule = typeof RequestExtractExpressionRuleEnum[keyof typeof RequestExtractExpressionRuleEnum];

// 接口请求-参数提取-提取范围
export const RequestExtractScopeEnum = {
    BODY: 'BODY',
    BODY_AS_DOCUMENT: 'BODY_AS_DOCUMENT',
    UNESCAPED_BODY: 'UNESCAPED_BODY',
    REQUEST_HEADERS: 'REQUEST_HEADERS',
    RESPONSE_CODE: 'RESPONSE_CODE',
    RESPONSE_HEADERS: 'RESPONSE_HEADERS',
    RESPONSE_MESSAGE: 'RESPONSE_MESSAGE',
    URL: 'URL',
} as const;
export type RequestExtractScope = typeof RequestExtractScopeEnum[keyof typeof RequestExtractScopeEnum];


// 接口请求-响应体断言-Xpath断言类型
export const ResponseBodyXPathAssertionFormatEnum = {
    HTML: 'HTML',
    XML: 'XML',
} as const;
export type ResponseBodyXPathAssertionFormat = typeof ResponseBodyXPathAssertionFormatEnum[keyof typeof ResponseBodyXPathAssertionFormatEnum];


// 接口响应体格式
export const ResponseBodyFormatEnum = {
    NONE: 'NONE',
    XML: 'XML',
    JSON: 'JSON',
    RAW: 'RAW',
    BINARY: 'BINARY',
} as const;
export type ResponseBodyFormat = typeof ResponseBodyFormatEnum[keyof typeof ResponseBodyFormatEnum];
// 接口响应体格式
export const RequestDefinitionStatusEnum = {
    DEPRECATED: 'DEPRECATED',
    PROCESSING: 'PROCESSING',
    DEBUGGING: 'DEBUGGING',
    DONE: 'DONE',
} as const;
export type RequestDefinitionStatus = typeof RequestDefinitionStatusEnum[keyof typeof RequestDefinitionStatusEnum];
// 接口响应体格式
export const RequestComposition = {
    BASE_INFO: 'BASE_INFO',
    HEADER: 'HEADER',
    BODY: 'BODY',
    PRECONDITION: 'PRECONDITION',
    POST_CONDITION: 'POST_CONDITION',
    SETTING: 'SETTING',
} as const;
export type RequestCompositionType = typeof RequestComposition[keyof typeof RequestComposition];