export const AuthScopeEnum = {
    SYSTEM: 'SYSTEM',
    PROJECT: 'PROJECT',
} as const;
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
export type AuthScopeEnumType = typeof AuthScopeEnum[keyof typeof AuthScopeEnum];