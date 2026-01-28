export const AuthScopeEnum = {
    SYSTEM: 'SYSTEM',
    PROJECT: 'PROJECT',
} as const;

export type AuthScopeEnumType = typeof AuthScopeEnum[keyof typeof AuthScopeEnum];